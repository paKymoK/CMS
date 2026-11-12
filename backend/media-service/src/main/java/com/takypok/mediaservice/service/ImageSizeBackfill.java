package com.takypok.mediaservice.service;

import com.takypok.mediaservice.config.StorageProperties;
import com.takypok.mediaservice.repository.UploadFileRepository;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Fills {@code size_bytes} for images that were registered without one (the seed library, and
 * anything uploaded before the column existed) from the stored file, once at startup. Rows whose
 * file is not in the images directory are left empty.
 */
@Slf4j
@Component
@Order(2)
@RequiredArgsConstructor
public class ImageSizeBackfill implements ApplicationRunner {
  private final UploadFileRepository uploadFileRepository;
  private final StorageProperties storageProperties;

  @Override
  public void run(ApplicationArguments args) {
    Long filled =
        uploadFileRepository
            .findAllBySizeBytesIsNull()
            .concatMap(
                file -> {
                  Path path =
                      Path.of(storageProperties.getImagesDir(), file.getId() + file.getExtension());
                  if (!Files.isRegularFile(path)) {
                    return Mono.empty();
                  }
                  return Mono.fromCallable(() -> Files.size(path))
                      .flatMap(
                          size -> {
                            file.setSizeBytes(size);
                            return uploadFileRepository.save(file);
                          });
                })
            .count()
            .onErrorResume(
                e -> {
                  log.warn("Image size backfill failed: {}", e.getMessage());
                  return Mono.just(0L);
                })
            .block();
    if (filled != null && filled > 0) {
      log.info("Recorded the file size of {} image(s)", filled);
    }
  }
}
