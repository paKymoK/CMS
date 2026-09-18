package com.takypok.mediaservice.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.takypok.core.exception.ApplicationException;
import com.takypok.mediaservice.config.ChunkedUploadProperties;
import com.takypok.mediaservice.config.StorageProperties;
import com.takypok.mediaservice.model.dto.Base64ChunkRequest;
import com.takypok.mediaservice.model.dto.FinishChunkedUploadRequest;
import com.takypok.mediaservice.model.dto.StartChunkedUploadRequest;
import com.takypok.mediaservice.model.dto.StartChunkedUploadResponse;
import com.takypok.mediaservice.model.entity.UploadFile;
import com.takypok.mediaservice.model.mapper.UploadFileMapper;
import com.takypok.mediaservice.repository.UploadFileRepository;
import com.takypok.mediaservice.service.UploadSessionRegistry;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

class ChunkedUploadServiceImplTest {

  @TempDir Path tempDir;

  private static final DefaultDataBufferFactory BUFFERS = new DefaultDataBufferFactory();

  private ChunkedUploadServiceImpl service;
  private ChunkedUploadProperties properties;
  private UploadFileRepository uploadFileRepository;

  @BeforeEach
  void setUp() {
    StorageProperties storageProperties = new StorageProperties();
    storageProperties.setFilesDir(tempDir.toString());

    properties = new ChunkedUploadProperties();
    properties.setChunkSizeBytes(8);
    properties.setMaxTotalSizeBytes(1_000_000);
    properties.setMaxActiveSessions(50);

    UploadSessionRegistry registry = new UploadSessionRegistry(storageProperties, properties);

    UploadFileMapper mapper = mock(UploadFileMapper.class);
    when(mapper.mapToEntity(anyString(), anyString(), anyString()))
        .thenAnswer(
            inv -> {
              UploadFile file = new UploadFile();
              file.setName(inv.getArgument(0));
              file.setExtension(inv.getArgument(1));
              file.setSiteId(inv.getArgument(2));
              return file;
            });

    Map<UUID, UploadFile> savedFiles = new ConcurrentHashMap<>();
    uploadFileRepository = mock(UploadFileRepository.class);
    when(uploadFileRepository.save(any(UploadFile.class)))
        .thenAnswer(
            inv -> {
              UploadFile file = inv.getArgument(0);
              file.setId(UUID.randomUUID());
              savedFiles.put(file.getId(), file);
              return Mono.just(file);
            });
    when(uploadFileRepository.findAllById(any(Iterable.class)))
        .thenAnswer(
            inv -> {
              Iterable<UUID> ids = inv.getArgument(0);
              List<UploadFile> found = new ArrayList<>();
              ids.forEach(id -> found.add(savedFiles.get(id)));
              found.removeIf(java.util.Objects::isNull);
              return Flux.fromIterable(found);
            });
    when(uploadFileRepository.findByIdAndSiteId(any(UUID.class), anyString()))
        .thenAnswer(
            inv -> {
              UUID id = inv.getArgument(0);
              String site = inv.getArgument(1);
              UploadFile file = savedFiles.get(id);
              return file != null && site.equals(file.getSiteId()) ? Mono.just(file) : Mono.empty();
            });
    when(uploadFileRepository.deleteById(any(UUID.class)))
        .thenAnswer(
            inv -> {
              savedFiles.remove((UUID) inv.getArgument(0));
              return Mono.empty();
            });
    when(uploadFileRepository.deleteAllById(any(Iterable.class)))
        .thenAnswer(
            inv -> {
              Iterable<UUID> ids = inv.getArgument(0);
              ids.forEach(savedFiles::remove);
              return Mono.empty();
            });

    service =
        new ChunkedUploadServiceImpl(
            registry, properties, storageProperties, uploadFileRepository, mapper);
  }

  private String startSession(long totalSize) {
    return startSession(totalSize, "vn");
  }

  private String startSession(long totalSize, String site) {
    StartChunkedUploadResponse response =
        service
            .start(
                new StartChunkedUploadRequest(
                    UUID.randomUUID().toString(), "test.txt", totalSize, site))
            .block();
    assertThat(response.chunkSizeBytes()).isEqualTo(8);
    return response.sessionId();
  }

  private void writeChunk(String sessionId, int index, byte[] bytes) {
    service.writeChunk(sessionId, index, Flux.just(buffer(bytes))).block();
  }

  private static DataBuffer buffer(byte[] bytes) {
    return BUFFERS.wrap(bytes);
  }

  @Test
  void happyPath_assemblesFileByteIdentical() throws Exception {
    byte[] data = "ABCDEFGHIJKLMNOPQRST".getBytes(StandardCharsets.UTF_8); // 20 bytes, chunkSize=8
    String sessionId = startSession(data.length);

    writeChunk(sessionId, 0, sliceOf(data, 0, 8));
    writeChunk(sessionId, 1, sliceOf(data, 8, 16));
    writeChunk(sessionId, 2, sliceOf(data, 16, 20));

    UploadFile result = service.finish(sessionId, new FinishChunkedUploadRequest(3)).block();

    assertThat(result.getId()).isNotNull();
    byte[] written = Files.readAllBytes(tempDir.resolve(result.getId() + ".txt"));
    assertThat(written).isEqualTo(data);
  }

  @Test
  void listFiles_returnsFinishedFilesButNotInProgressSessions() throws Exception {
    byte[] data = "ABCDEFGH".getBytes(StandardCharsets.UTF_8); // exactly one chunk
    String finishedSessionId = startSession(data.length);
    writeChunk(finishedSessionId, 0, data);
    UploadFile finished =
        service.finish(finishedSessionId, new FinishChunkedUploadRequest(1)).block();

    // A second session left mid-upload: its ".part" file must not show up as an uploaded file.
    String inProgressSessionId = startSession(data.length);
    writeChunk(inProgressSessionId, 0, data);

    List<com.takypok.mediaservice.model.dto.ChunkedUploadedFile> files =
        service.listFiles("vn").block();

    assertThat(files).hasSize(1);
    assertThat(files.get(0).name()).isEqualTo(finished.getId() + ".txt");
    assertThat(files.get(0).originalName()).isEqualTo("test.txt");
    assertThat(files.get(0).sizeBytes()).isEqualTo(data.length);
  }

  @Test
  void deleteFile_removesFileAndDbRow() throws Exception {
    byte[] data = "ABCDEFGH".getBytes(StandardCharsets.UTF_8);
    String sessionId = startSession(data.length);
    writeChunk(sessionId, 0, data);
    UploadFile uploaded = service.finish(sessionId, new FinishChunkedUploadRequest(1)).block();
    String diskName = uploaded.getId() + ".txt";

    service.deleteFile(diskName, "vn").block();

    assertThat(Files.exists(tempDir.resolve(diskName))).isFalse();
    assertThat(service.listFiles("vn").block()).isEmpty();
  }

  @Test
  void deleteFile_isIdempotentForAlreadyMissingFile() {
    assertThatCode(() -> service.deleteFile(UUID.randomUUID() + ".txt", "vn").block())
        .doesNotThrowAnyException();
  }

  @Test
  void deleteFile_rejectsPathTraversalName() {
    assertThatThrownBy(() -> service.deleteFile("../evil.txt", "vn").block())
        .isInstanceOf(ApplicationException.class);
  }

  @Test
  void deleteFile_doesNotRemoveAnotherSitesFile() throws Exception {
    byte[] data = "ABCDEFGH".getBytes(StandardCharsets.UTF_8);
    String sessionId = startSession(data.length, "vn");
    writeChunk(sessionId, 0, data);
    UploadFile uploaded = service.finish(sessionId, new FinishChunkedUploadRequest(1)).block();
    String diskName = uploaded.getId() + ".txt";

    service.deleteFile(diskName, "jp").block();

    assertThat(Files.exists(tempDir.resolve(diskName))).isTrue();
    assertThat(service.listFiles("vn").block()).hasSize(1);
    assertThat(service.listFiles("jp").block()).isEmpty();
  }

  @Test
  void deleteAllFiles_removesEveryFinishedFile() throws Exception {
    byte[] data = "ABCDEFGH".getBytes(StandardCharsets.UTF_8);
    String session1 = startSession(data.length);
    writeChunk(session1, 0, data);
    service.finish(session1, new FinishChunkedUploadRequest(1)).block();

    String session2 = startSession(data.length);
    writeChunk(session2, 0, data);
    service.finish(session2, new FinishChunkedUploadRequest(1)).block();

    assertThat(service.listFiles("vn").block()).hasSize(2);

    service.deleteAllFiles("vn").block();

    assertThat(service.listFiles("vn").block()).isEmpty();
    try (Stream<Path> remaining = Files.list(tempDir)) {
      assertThat(remaining.toList()).isEmpty();
    }
  }

  @Test
  void deleteAllFiles_onlyRemovesTheGivenSitesFiles() throws Exception {
    byte[] data = "ABCDEFGH".getBytes(StandardCharsets.UTF_8);
    String vnSession = startSession(data.length, "vn");
    writeChunk(vnSession, 0, data);
    service.finish(vnSession, new FinishChunkedUploadRequest(1)).block();

    String jpSession = startSession(data.length, "jp");
    writeChunk(jpSession, 0, data);
    UploadFile jpFile = service.finish(jpSession, new FinishChunkedUploadRequest(1)).block();

    service.deleteAllFiles("vn").block();

    assertThat(service.listFiles("vn").block()).isEmpty();
    assertThat(service.listFiles("jp").block()).hasSize(1);
    assertThat(Files.exists(tempDir.resolve(jpFile.getId() + ".txt"))).isTrue();
  }

  @Test
  void writeChunkBase64_decodesAndAssemblesByteIdentical() throws Exception {
    byte[] data = "ABCDEFGHIJKLMNOPQRST".getBytes(StandardCharsets.UTF_8); // 20 bytes, chunkSize=8
    String sessionId = startSession(data.length);

    service
        .writeChunkBase64(
            sessionId,
            0,
            new Base64ChunkRequest(Base64.getEncoder().encodeToString(sliceOf(data, 0, 8))))
        .block();
    service
        .writeChunkBase64(
            sessionId,
            1,
            new Base64ChunkRequest(Base64.getEncoder().encodeToString(sliceOf(data, 8, 16))))
        .block();
    service
        .writeChunkBase64(
            sessionId,
            2,
            new Base64ChunkRequest(Base64.getEncoder().encodeToString(sliceOf(data, 16, 20))))
        .block();

    UploadFile result = service.finish(sessionId, new FinishChunkedUploadRequest(3)).block();

    byte[] written = Files.readAllBytes(tempDir.resolve(result.getId() + ".txt"));
    assertThat(written).isEqualTo(data);
  }

  @Test
  void writeChunkBase64_rejectsInvalidBase64() {
    String sessionId = startSession(8);

    assertThatThrownBy(
            () ->
                service
                    .writeChunkBase64(sessionId, 0, new Base64ChunkRequest("not-valid-base64!!"))
                    .block())
        .isInstanceOf(ApplicationException.class);
  }

  @Test
  void writeChunkEncrypted_decryptsAndAssemblesByteIdentical() throws Exception {
    byte[] data = "ABCDEFGHIJKLMNOPQRST".getBytes(StandardCharsets.UTF_8); // 20 bytes, chunkSize=8
    String sessionId = startSession(data.length);

    service
        .writeChunkEncrypted(sessionId, 0, Flux.just(buffer(encryptGcm(sliceOf(data, 0, 8)))))
        .block();
    service
        .writeChunkEncrypted(sessionId, 1, Flux.just(buffer(encryptGcm(sliceOf(data, 8, 16)))))
        .block();
    service
        .writeChunkEncrypted(sessionId, 2, Flux.just(buffer(encryptGcm(sliceOf(data, 16, 20)))))
        .block();

    UploadFile result = service.finish(sessionId, new FinishChunkedUploadRequest(3)).block();

    byte[] written = Files.readAllBytes(tempDir.resolve(result.getId() + ".txt"));
    assertThat(written).isEqualTo(data);
  }

  @Test
  void writeChunkEncrypted_rejectsGarbageCiphertext() {
    String sessionId = startSession(8);
    byte[] garbage = "not-real-gcm-ciphertext!".getBytes(StandardCharsets.UTF_8);

    assertThatThrownBy(
            () -> service.writeChunkEncrypted(sessionId, 0, Flux.just(buffer(garbage))).block())
        .isInstanceOf(ApplicationException.class);
  }

  private static byte[] encryptGcm(byte[] plaintext) throws Exception {
    byte[] key = Base64.getDecoder().decode(new ChunkedUploadProperties().getEncryptionKeyBase64());
    byte[] iv = new byte[12];
    new SecureRandom().nextBytes(iv);
    Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
    cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"), new GCMParameterSpec(128, iv));
    byte[] ciphertext = cipher.doFinal(plaintext);
    byte[] wire = new byte[iv.length + ciphertext.length];
    System.arraycopy(iv, 0, wire, 0, iv.length);
    System.arraycopy(ciphertext, 0, wire, iv.length, ciphertext.length);
    return wire;
  }

  @Test
  void outOfOrderConcurrentChunks_stillAssembleCorrectly() throws Exception {
    byte[] data = "ABCDEFGHIJKLMNOPQRST".getBytes(StandardCharsets.UTF_8);
    String sessionId = startSession(data.length);

    List<Integer> indexes = List.of(2, 0, 1); // deliberately out of order
    Flux.fromIterable(indexes)
        .flatMap(
            index -> {
              byte[] chunk =
                  index == 2 ? sliceOf(data, 16, 20) : sliceOf(data, index * 8, index * 8 + 8);
              return service.writeChunk(sessionId, index, Flux.just(buffer(chunk)));
            })
        .blockLast();

    UploadFile result = service.finish(sessionId, new FinishChunkedUploadRequest(3)).block();

    byte[] written = Files.readAllBytes(tempDir.resolve(result.getId() + ".txt"));
    assertThat(written).isEqualTo(data);
  }

  @Test
  void duplicateChunk_isIdempotent() throws Exception {
    byte[] data = "ABCDEFGH".getBytes(StandardCharsets.UTF_8); // exactly one chunk
    String sessionId = startSession(data.length);

    writeChunk(sessionId, 0, data);
    writeChunk(sessionId, 0, data); // retry of the same chunk

    UploadFile result = service.finish(sessionId, new FinishChunkedUploadRequest(1)).block();

    byte[] written = Files.readAllBytes(tempDir.resolve(result.getId() + ".txt"));
    assertThat(written).isEqualTo(data);
  }

  @Test
  void finish_rejectsWhenAChunkIsMissing() {
    byte[] data = "ABCDEFGHIJKLMNOPQRST".getBytes(StandardCharsets.UTF_8);
    String sessionId = startSession(data.length);

    writeChunk(sessionId, 0, sliceOf(data, 0, 8));
    // chunk 1 deliberately skipped
    writeChunk(sessionId, 2, sliceOf(data, 16, 20));

    assertThatThrownBy(() -> service.finish(sessionId, new FinishChunkedUploadRequest(3)).block())
        .isInstanceOf(ApplicationException.class);
  }

  @Test
  void writeChunk_rejectsUnknownSessionId() {
    assertThatThrownBy(
            () ->
                service.writeChunk(
                    UUID.randomUUID().toString(), 0, Flux.just(buffer("x".getBytes()))))
        .isInstanceOf(ApplicationException.class);
  }

  @Test
  void writeChunk_rejectsOversizedChunk() {
    String sessionId = startSession(100);
    byte[] tooBig = "ABCDEFGHI".getBytes(StandardCharsets.UTF_8); // 9 bytes > chunkSizeBytes(8)

    assertThatThrownBy(() -> service.writeChunk(sessionId, 0, Flux.just(buffer(tooBig))).block())
        .isInstanceOf(ApplicationException.class);
  }

  @Test
  void writeChunk_rejectsOnceTotalSizeCapExceeded() {
    properties.setMaxTotalSizeBytes(10); // smaller than two 8-byte chunks combined
    String sessionId = startSession(16);

    writeChunk(sessionId, 0, "ABCDEFGH".getBytes(StandardCharsets.UTF_8));

    assertThatThrownBy(
            () ->
                service
                    .writeChunk(
                        sessionId,
                        1,
                        Flux.just(buffer("IJKLMNOP".getBytes(StandardCharsets.UTF_8))))
                    .block())
        .isInstanceOf(ApplicationException.class);
  }

  @Test
  void start_rejectsPathTraversalSessionId() {
    StartChunkedUploadRequest request =
        new StartChunkedUploadRequest("../../etc/passwd", "f.txt", 1L, "vn");

    assertThatThrownBy(() -> service.start(request).block())
        .isInstanceOf(ApplicationException.class);
  }

  private static byte[] sliceOf(byte[] data, int from, int to) {
    return java.util.Arrays.copyOfRange(data, from, to);
  }
}
