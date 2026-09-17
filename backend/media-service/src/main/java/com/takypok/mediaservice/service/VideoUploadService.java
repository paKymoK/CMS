package com.takypok.mediaservice.service;

import com.takypok.mediaservice.config.UploadProperties;
import com.takypok.mediaservice.util.UploadSizeLimiter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoUploadService {

  private final VideoStorageService storageService;
  private final UploadProperties uploadProperties;

  public Mono<Void> save(Flux<DataBuffer> data, String videoId) {
    Path path = storageService.rawPath(videoId);
    return DataBufferUtils.write(
            UploadSizeLimiter.enforce(data, uploadProperties.getMaxVideoSize(), "Video"),
            path,
            StandardOpenOption.CREATE,
            StandardOpenOption.TRUNCATE_EXISTING)
        .onErrorResume(e -> deletePartial(path).then(Mono.error(e)));
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
