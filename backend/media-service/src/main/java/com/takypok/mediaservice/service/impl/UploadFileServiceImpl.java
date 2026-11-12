package com.takypok.mediaservice.service.impl;

import static com.takypok.mediaservice.util.FileUtil.getFileExtension;

import com.takypok.core.exception.ApplicationException;
import com.takypok.core.model.Message;
import com.takypok.mediaservice.config.StorageProperties;
import com.takypok.mediaservice.config.UploadProperties;
import com.takypok.mediaservice.model.entity.UploadFile;
import com.takypok.mediaservice.model.mapper.UploadFileMapper;
import com.takypok.mediaservice.repository.UploadFileRepository;
import com.takypok.mediaservice.service.UploadFileService;
import com.takypok.mediaservice.util.UploadSizeLimiter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class UploadFileServiceImpl implements UploadFileService {
  private final UploadFileRepository uploadFileRepository;
  private final UploadFileMapper uploadFileMapper;
  private final StorageProperties storageProperties;
  private final UploadProperties uploadProperties;

  private static final int MAX_TITLE = 200;
  private static final int MAX_ALT = 500;
  private static final int MAX_DESCRIPTION = 2000;

  @Override
  public Mono<UploadFile> upload(FilePart filePart, String siteId) {
    String filename = filePart.filename();
    String extension = getFileExtension(filename);
    return uploadFileRepository
        .save(uploadFileMapper.mapToEntity(filename, extension, siteId))
        .flatMap(
            uploadFile -> {
              Path path = Path.of(storageProperties.getImagesDir(), uploadFile.getId() + extension);
              return DataBufferUtils.write(
                      UploadSizeLimiter.enforce(
                          filePart.content(), uploadProperties.getMaxImageSize(), "Image"),
                      path,
                      StandardOpenOption.CREATE,
                      StandardOpenOption.TRUNCATE_EXISTING)
                  .onErrorResume(e -> deletePartial(path).then(Mono.error(e)))
                  .then(Mono.fromCallable(() -> Files.size(path)))
                  .flatMap(
                      size -> {
                        uploadFile.setSizeBytes(size);
                        return uploadFileRepository.save(uploadFile);
                      });
            });
  }

  @Override
  public Mono<List<UploadFile>> listForSite(String siteId) {
    return uploadFileRepository.findAllBySiteId(siteId).collectList();
  }

  @Override
  public Mono<UploadFile> updateMetadata(
      UUID id, String siteId, String title, String altText, String description) {
    return uploadFileRepository
        .findByIdAndSiteId(id, siteId)
        .switchIfEmpty(
            Mono.error(
                new ApplicationException(
                    Message.Application.ERROR, "No image " + id + " for site " + siteId)))
        .flatMap(
            file -> {
              file.setTitle(clean(title, MAX_TITLE, "Title"));
              file.setAltText(clean(altText, MAX_ALT, "Alt text"));
              file.setDescription(clean(description, MAX_DESCRIPTION, "Description"));
              return uploadFileRepository.save(file);
            });
  }

  private static String clean(String value, int max, String label) {
    String trimmed = value == null ? "" : value.strip();
    if (trimmed.length() > max) {
      throw new ApplicationException(
          Message.Application.ERROR, label + " is longer than " + max + " characters");
    }
    return trimmed.isEmpty() ? null : trimmed;
  }

  private Mono<Void> deletePartial(Path path) {
    return Mono.fromRunnable(
        () -> {
          try {
            Files.deleteIfExists(path);
          } catch (Exception e) {
            log.warn("Failed to clean up partial upload {}: {}", path, e.getMessage());
          }
        });
  }
}
