package com.takypok.mediaservice.service;

import com.takypok.mediaservice.config.StorageProperties;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
@Slf4j
public class VideoStorageService {

  private final StorageProperties props;

  @PostConstruct
  public void init() throws IOException {
    Files.createDirectories(Path.of(props.getImagesDir()));
    Files.createDirectories(Path.of(props.getRawDir()));
    Files.createDirectories(Path.of(props.getHlsDir()));
    Files.createDirectories(Path.of(props.getFilesDir()));
    log.info("Storage directories ready: {}", props.getBaseDir());
  }

  public Path rawPath(String videoId) {
    return Path.of(props.getRawDir()).resolve(videoId + ".mp4");
  }

  public Path hlsDir(String videoId) {
    return Path.of(props.getHlsDir()).resolve(videoId);
  }

  public Path qualityDir(String videoId, String quality) {
    return hlsDir(videoId).resolve(quality);
  }

  public Mono<Void> deleteVideo(String videoId) {
    return Mono.fromCallable(
            () -> {
              Files.deleteIfExists(rawPath(videoId));
              Path hls = hlsDir(videoId);
              if (Files.exists(hls)) {
                Files.walk(hls)
                    .sorted(Comparator.reverseOrder())
                    .forEach(
                        f -> {
                          try {
                            Files.delete(f);
                          } catch (IOException ignored) {
                          }
                        });
              }
              return (Void) null;
            })
        .subscribeOn(Schedulers.boundedElastic());
  }
}
