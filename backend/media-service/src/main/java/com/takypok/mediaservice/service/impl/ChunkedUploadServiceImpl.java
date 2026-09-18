package com.takypok.mediaservice.service.impl;

import com.takypok.core.exception.ApplicationException;
import com.takypok.core.model.Message;
import com.takypok.mediaservice.config.ChunkedUploadProperties;
import com.takypok.mediaservice.config.StorageProperties;
import com.takypok.mediaservice.model.UploadSession;
import com.takypok.mediaservice.model.dto.Base64ChunkRequest;
import com.takypok.mediaservice.model.dto.ChunkAckResponse;
import com.takypok.mediaservice.model.dto.ChunkedUploadedFile;
import com.takypok.mediaservice.model.dto.FinishChunkedUploadRequest;
import com.takypok.mediaservice.model.dto.StartChunkedUploadRequest;
import com.takypok.mediaservice.model.dto.StartChunkedUploadResponse;
import com.takypok.mediaservice.model.entity.UploadFile;
import com.takypok.mediaservice.model.mapper.UploadFileMapper;
import com.takypok.mediaservice.repository.UploadFileRepository;
import com.takypok.mediaservice.service.ChunkedUploadService;
import com.takypok.mediaservice.service.UploadSessionRegistry;
import com.takypok.mediaservice.util.UploadSizeLimiter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferLimitException;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChunkedUploadServiceImpl implements ChunkedUploadService {
  private static final DefaultDataBufferFactory BUFFER_FACTORY = new DefaultDataBufferFactory();
  private static final String AES_GCM_ALGORITHM = "AES/GCM/NoPadding";
  private static final int GCM_IV_LENGTH_BYTES = 12;
  private static final int GCM_TAG_LENGTH_BITS = 128;

  private final UploadSessionRegistry registry;
  private final ChunkedUploadProperties properties;
  private final StorageProperties storageProperties;
  private final UploadFileRepository uploadFileRepository;
  private final UploadFileMapper uploadFileMapper;

  @Override
  public Mono<StartChunkedUploadResponse> start(StartChunkedUploadRequest request) {
    if (request.filename() == null || request.filename().isBlank()) {
      return Mono.error(
          new ApplicationException(Message.Application.ERROR, "filename is required"));
    }
    if (request.site() == null || request.site().isBlank()) {
      return Mono.error(new ApplicationException(Message.Application.ERROR, "site is required"));
    }
    return registry
        .getOrCreate(request)
        .map(
            session ->
                new StartChunkedUploadResponse(
                    session.getSessionId(),
                    properties.getChunkSizeBytes(),
                    properties.getSessionIdleTimeoutSeconds()));
  }

  @Override
  public Mono<ChunkAckResponse> writeChunk(String sessionId, int index, Flux<DataBuffer> body) {
    UploadSession session = requireWritableSession(sessionId, index);
    return DataBufferUtils.join(body, (int) properties.getChunkSizeBytes())
        .onErrorMap(
            DataBufferLimitException.class,
            e ->
                new ApplicationException(
                    Message.Application.ERROR,
                    "Chunk exceeds maximum allowed size of "
                        + (properties.getChunkSizeBytes() / 1024)
                        + "KB"))
        .flatMap(buffer -> writeToChannel(session, index, buffer));
  }

  @Override
  public Mono<ChunkAckResponse> writeChunkBase64(
      String sessionId, int index, Base64ChunkRequest request) {
    UploadSession session = requireWritableSession(sessionId, index);
    return decodeBase64(request.data()).flatMap(buffer -> writeToChannel(session, index, buffer));
  }

  @Override
  public Mono<ChunkAckResponse> writeChunkEncrypted(
      String sessionId, int index, Flux<DataBuffer> body) {
    UploadSession session = requireWritableSession(sessionId, index);
    // Wire payload is: 12-byte GCM IV + ciphertext + 16-byte GCM tag, so it's larger than the
    // plaintext chunk it decrypts to.
    int joinLimit =
        (int) properties.getChunkSizeBytes() + GCM_IV_LENGTH_BYTES + (GCM_TAG_LENGTH_BITS / 8);
    return DataBufferUtils.join(body, joinLimit)
        .onErrorMap(
            DataBufferLimitException.class,
            e ->
                new ApplicationException(
                    Message.Application.ERROR, "Encrypted chunk exceeds maximum allowed size"))
        .flatMap(this::decryptGcm)
        .flatMap(buffer -> writeToChannel(session, index, buffer));
  }

  private Mono<DataBuffer> decryptGcm(DataBuffer encryptedBuffer) {
    return Mono.fromCallable(
        () -> {
          ByteBuffer wire = encryptedBuffer.asByteBuffer();
          byte[] payload = new byte[wire.remaining()];
          wire.get(payload);
          DataBufferUtils.release(encryptedBuffer);

          if (payload.length <= GCM_IV_LENGTH_BYTES) {
            throw new ApplicationException(
                Message.Application.ERROR, "Encrypted chunk is too short");
          }
          byte[] iv = Arrays.copyOfRange(payload, 0, GCM_IV_LENGTH_BYTES);
          byte[] ciphertext = Arrays.copyOfRange(payload, GCM_IV_LENGTH_BYTES, payload.length);
          try {
            SecretKeySpec key =
                new SecretKeySpec(
                    Base64.getDecoder().decode(properties.getEncryptionKeyBase64()), "AES");
            Cipher cipher = Cipher.getInstance(AES_GCM_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv));
            return BUFFER_FACTORY.wrap(cipher.doFinal(ciphertext));
          } catch (GeneralSecurityException e) {
            throw new ApplicationException(Message.Application.ERROR, "Failed to decrypt chunk");
          }
        });
  }

  private UploadSession requireWritableSession(String sessionId, int index) {
    UploadSession session = registry.require(sessionId);
    if (session.getFinishing().get()) {
      throw new ApplicationException(
          Message.Application.ERROR, "Upload session is finalizing; cannot accept more chunks");
    }
    if (index < 0) {
      throw new ApplicationException(Message.Application.ERROR, "Chunk index must not be negative");
    }
    return session;
  }

  private Mono<DataBuffer> decodeBase64(String base64Data) {
    return Mono.fromCallable(
        () -> {
          if (base64Data == null || base64Data.isBlank()) {
            throw new ApplicationException(Message.Application.ERROR, "Chunk data is required");
          }
          byte[] decoded;
          try {
            decoded = Base64.getDecoder().decode(base64Data);
          } catch (IllegalArgumentException e) {
            throw new ApplicationException(
                Message.Application.ERROR, "Chunk data is not valid base64");
          }
          if (decoded.length > properties.getChunkSizeBytes()) {
            throw new ApplicationException(
                Message.Application.ERROR,
                "Chunk exceeds maximum allowed size of "
                    + (properties.getChunkSizeBytes() / 1024)
                    + "KB");
          }
          return BUFFER_FACTORY.wrap(decoded);
        });
  }

  private Mono<ChunkAckResponse> writeToChannel(
      UploadSession session, int index, DataBuffer buffer) {
    int byteCount = buffer.readableByteCount();
    if (byteCount == 0) {
      DataBufferUtils.release(buffer);
      return Mono.error(
          new ApplicationException(Message.Application.ERROR, "Chunk must not be empty"));
    }
    try {
      UploadSizeLimiter.checkWithinLimit(
          session.getTotalBytesReceived().sum(),
          byteCount,
          properties.getMaxTotalSizeBytes(),
          "Upload");
    } catch (ApplicationException e) {
      DataBufferUtils.release(buffer);
      return Mono.error(e);
    }
    long position = (long) index * properties.getChunkSizeBytes();
    ByteBuffer byteBuffer = buffer.asByteBuffer();
    return writeAsync(session.getChannel(), byteBuffer, position)
        .doFinally(signal -> DataBufferUtils.release(buffer))
        .map(
            written -> {
              session.getChunkLengths().put(index, byteCount);
              session.getReceivedIndexes().add(index);
              session.getTotalBytesReceived().add(byteCount);
              session.touch();
              long totalSoFar = session.getTotalBytesReceived().sum();
              log.debug(
                  "Session {} chunk {} written ({} bytes, total {} bytes so far)",
                  session.getSessionId(),
                  index,
                  byteCount,
                  totalSoFar);
              return new ChunkAckResponse(index, byteCount, totalSoFar);
            });
  }

  private Mono<Integer> writeAsync(
      AsynchronousFileChannel channel, ByteBuffer buffer, long position) {
    return Mono.create(
        sink ->
            channel.write(
                buffer,
                position,
                null,
                new CompletionHandler<Integer, Void>() {
                  @Override
                  public void completed(Integer result, Void attachment) {
                    sink.success(result);
                  }

                  @Override
                  public void failed(Throwable exc, Void attachment) {
                    sink.error(exc);
                  }
                }));
  }

  @Override
  public Mono<UploadFile> finish(String sessionId, FinishChunkedUploadRequest request) {
    UploadSession session = registry.require(sessionId);
    if (!session.getFinishing().compareAndSet(false, true)) {
      return Mono.error(
          new ApplicationException(
              Message.Application.ERROR, "Finish already requested for this session"));
    }
    int totalChunks = request.totalChunks();
    if (totalChunks <= 0) {
      return Mono.error(
          new ApplicationException(Message.Application.ERROR, "totalChunks must be positive"));
    }
    if (session.getReceivedIndexes().size() != totalChunks
        || session.getReceivedIndexes().first() != 0
        || session.getReceivedIndexes().last() != totalChunks - 1) {
      return Mono.error(
          new ApplicationException(
              Message.Application.ERROR,
              "Upload incomplete: expected "
                  + totalChunks
                  + " chunks, received "
                  + session.getReceivedIndexes().size()));
    }

    long chunkSizeBytes = properties.getChunkSizeBytes();
    long total = 0;
    for (int i = 0; i < totalChunks; i++) {
      int len = session.getChunkLengths().getOrDefault(i, 0);
      boolean isLast = i == totalChunks - 1;
      if (!isLast && len != chunkSizeBytes) {
        return Mono.error(
            new ApplicationException(
                Message.Application.ERROR,
                "Chunk "
                    + i
                    + " has an unexpected size; only the final chunk may be shorter than "
                    + chunkSizeBytes
                    + " bytes"));
      }
      if (isLast && (len <= 0 || len > chunkSizeBytes)) {
        return Mono.error(
            new ApplicationException(Message.Application.ERROR, "Final chunk has an invalid size"));
      }
      total += len;
    }
    UploadSizeLimiter.checkWithinLimit(0, total, properties.getMaxTotalSizeBytes(), "Upload");

    long finalTotal = total;
    return closeAndPersist(session)
        .doOnSuccess(
            uploadFile -> {
              registry.remove(session.getSessionId());
              log.info(
                  "Chunked upload session {} finalized -> {} ({} bytes, {} chunks)",
                  session.getSessionId(),
                  uploadFile.getId(),
                  finalTotal,
                  totalChunks);
            });
  }

  private Mono<UploadFile> closeAndPersist(UploadSession session) {
    return uploadFileRepository
        .save(
            uploadFileMapper.mapToEntity(
                session.getFilename(), session.getExtension(), session.getSite()))
        .flatMap(
            uploadFile ->
                Mono.fromCallable(
                        () -> {
                          session.getChannel().close();
                          Path finalPath =
                              Path.of(
                                  storageProperties.getFilesDir(),
                                  uploadFile.getId() + session.getExtension());
                          Files.move(
                              session.getPartPath(),
                              finalPath,
                              StandardCopyOption.REPLACE_EXISTING);
                          return uploadFile;
                        })
                    .onErrorResume(e -> deletePartial(session.getPartPath()).then(Mono.error(e))));
  }

  private Mono<Void> deletePartial(Path path) {
    return Mono.fromRunnable(
        () -> {
          try {
            Files.deleteIfExists(path);
          } catch (Exception e) {
            log.warn("Failed to clean up partial chunked upload {}: {}", path, e.getMessage());
          }
        });
  }

  @Override
  public Mono<List<ChunkedUploadedFile>> listFiles(String siteId) {
    Path dir = Path.of(storageProperties.getFilesDir());
    return Mono.fromCallable(() -> listOnDiskFiles(dir))
        .subscribeOn(Schedulers.boundedElastic())
        .flatMap(onDiskFiles -> attachOriginalNames(onDiskFiles, siteId));
  }

  private List<OnDiskFile> listOnDiskFiles(Path dir) throws IOException {
    if (!Files.isDirectory(dir)) {
      return List.of();
    }
    // ".part" files are sessions still in flight or abandoned mid-upload — not finished files,
    // so they're excluded from what's shown as "uploaded".
    try (Stream<Path> entries = Files.list(dir)) {
      return entries
          .filter(Files::isRegularFile)
          .filter(path -> !path.getFileName().toString().endsWith(".part"))
          .map(this::toOnDiskFile)
          .toList();
    }
  }

  private OnDiskFile toOnDiskFile(Path path) {
    try {
      BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
      String diskName = path.getFileName().toString();
      return new OnDiskFile(
          diskName, parseIdPrefix(diskName), attrs.size(), attrs.lastModifiedTime().toInstant());
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  /**
   * Files land on disk named {@code <upload_file.id><extension>} (see {@link #closeAndPersist}), so
   * the id the original filename is stored under can be recovered straight from the filename — no
   * separate on-disk index needed.
   */
  private UUID parseIdPrefix(String diskName) {
    int dot = diskName.indexOf('.');
    String idPart = dot == -1 ? diskName : diskName.substring(0, dot);
    try {
      return UUID.fromString(idPart);
    } catch (IllegalArgumentException e) {
      return null;
    }
  }

  /**
   * Only files whose {@link UploadFile} row belongs to {@code siteId} are included — a file on disk
   * with no matching row for this site (either it doesn't exist or it belongs to another site) is
   * silently excluded rather than falling back to its disk name, since that fallback would
   * otherwise leak another site's uploads into this site's media library listing.
   */
  private Mono<List<ChunkedUploadedFile>> attachOriginalNames(
      List<OnDiskFile> onDiskFiles, String siteId) {
    List<UUID> ids = onDiskFiles.stream().map(OnDiskFile::id).filter(Objects::nonNull).toList();
    return uploadFileRepository
        .findAllById(ids)
        .filter(uploadFile -> siteId.equals(uploadFile.getSiteId()))
        .collectMap(UploadFile::getId, UploadFile::getName)
        .map(
            originalNamesById ->
                onDiskFiles.stream()
                    .filter(file -> originalNamesById.containsKey(file.id()))
                    .map(
                        file ->
                            new ChunkedUploadedFile(
                                file.diskName(),
                                originalNamesById.get(file.id()),
                                file.sizeBytes(),
                                file.modifiedAt()))
                    .sorted(Comparator.comparing(ChunkedUploadedFile::modifiedAt).reversed())
                    .toList());
  }

  private record OnDiskFile(String diskName, UUID id, long sizeBytes, Instant modifiedAt) {}

  @Override
  public Mono<Void> deleteFile(String name, String siteId) {
    Path path = resolveFilePath(name);
    UUID id = parseIdPrefix(name);
    if (id == null) {
      return Mono.fromRunnable(() -> deleteFileFromDisk(path))
          .subscribeOn(Schedulers.boundedElastic())
          .then();
    }
    // Only delete if the row exists AND belongs to this site — a name that resolves to another
    // site's row is left untouched (and not reported as an error, so a stale/guessed id doesn't
    // reveal whether it belongs to another site).
    return uploadFileRepository
        .findByIdAndSiteId(id, siteId)
        .flatMap(
            uploadFile ->
                uploadFileRepository
                    .deleteById(id)
                    .then(
                        Mono.fromRunnable(() -> deleteFileFromDisk(path))
                            .subscribeOn(Schedulers.boundedElastic())
                            .then()))
        .then();
  }

  @Override
  public Mono<Void> deleteAllFiles(String siteId) {
    Path dir = Path.of(storageProperties.getFilesDir());
    return Mono.fromCallable(() -> listOnDiskFiles(dir))
        .subscribeOn(Schedulers.boundedElastic())
        .flatMap(
            files -> {
              List<UUID> ids = files.stream().map(OnDiskFile::id).filter(Objects::nonNull).toList();
              return uploadFileRepository
                  .findAllById(ids)
                  .filter(uploadFile -> siteId.equals(uploadFile.getSiteId()))
                  .collectList()
                  .flatMap(
                      siteFiles -> {
                        List<UUID> siteIds = siteFiles.stream().map(UploadFile::getId).toList();
                        return uploadFileRepository
                            .deleteAllById(siteIds)
                            .then(
                                Mono.fromRunnable(
                                        () ->
                                            files.stream()
                                                .filter(file -> siteIds.contains(file.id()))
                                                .forEach(
                                                    file ->
                                                        deleteFileFromDisk(
                                                            Path.of(
                                                                storageProperties.getFilesDir(),
                                                                file.diskName()))))
                                    .subscribeOn(Schedulers.boundedElastic())
                                    .then());
                      });
            });
  }

  /**
   * Resolves a client-supplied on-disk file name to an absolute path within the files directory,
   * rejecting anything that isn't a plain file name (no path separators, no {@code ..}) so this
   * can't be used to delete files outside {@code uploads/files}.
   */
  private Path resolveFilePath(String name) {
    if (name == null || name.isBlank() || name.contains("/") || name.contains("\\")) {
      throw new ApplicationException(Message.Application.ERROR, "Invalid file name");
    }
    Path dir = Path.of(storageProperties.getFilesDir()).toAbsolutePath().normalize();
    Path resolved = dir.resolve(name).normalize();
    if (!resolved.startsWith(dir)) {
      throw new ApplicationException(Message.Application.ERROR, "Invalid file name");
    }
    return resolved;
  }

  private void deleteFileFromDisk(Path path) {
    try {
      Files.deleteIfExists(path);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
