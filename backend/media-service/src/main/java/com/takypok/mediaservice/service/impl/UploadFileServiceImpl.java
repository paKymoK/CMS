package com.takypok.mediaservice.service.impl;

import static com.takypok.mediaservice.util.FileUtil.getFileExtension;

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

  @Override
  public Mono<UploadFile> upload(FilePart filePart) {
    String filename = filePart.filename();
    String extension = getFileExtension(filename);
    return uploadFileRepository
        .save(uploadFileMapper.mapToEntity(filename, extension))
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
                  .thenReturn(uploadFile);
            });
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
