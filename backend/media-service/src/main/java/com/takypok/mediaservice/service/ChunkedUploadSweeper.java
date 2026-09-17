package com.takypok.mediaservice.service;

import com.takypok.mediaservice.config.ChunkedUploadProperties;
import com.takypok.mediaservice.model.UploadSession;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** Evicts abandoned chunked-upload sessions (client vanished mid-upload) on a simple timeout. */
@Component
@RequiredArgsConstructor
@Slf4j
public class ChunkedUploadSweeper {
  private final UploadSessionRegistry registry;
  private final ChunkedUploadProperties properties;

  @Scheduled(fixedDelayString = "#{${media.chunked-upload.sweep-interval-seconds:60} * 1000}")
  public void sweep() {
    Duration idleTimeout = Duration.ofSeconds(properties.getSessionIdleTimeoutSeconds());
    registry.evictExpired(idleTimeout).forEach(this::cleanup);
  }

  private void cleanup(UploadSession session) {
    try {
      session.getChannel().close();
    } catch (IOException e) {
      log.warn(
          "Failed to close channel for evicted session {}: {}",
          session.getSessionId(),
          e.getMessage());
    }
    try {
      Files.deleteIfExists(session.getPartPath());
    } catch (IOException e) {
      log.warn(
          "Failed to delete partial file for evicted session {}: {}",
          session.getSessionId(),
          e.getMessage());
    }
    log.warn(
        "Evicted abandoned upload session {} (idle since {}, {} bytes received)",
        session.getSessionId(),
        session.getLastActivityAt(),
        session.getTotalBytesReceived().sum());
  }
}
