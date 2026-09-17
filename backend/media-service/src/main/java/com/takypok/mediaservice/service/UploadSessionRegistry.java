package com.takypok.mediaservice.service;

import static com.takypok.mediaservice.util.FileUtil.getFileExtension;

import com.takypok.core.exception.ApplicationException;
import com.takypok.core.model.Message;
import com.takypok.mediaservice.config.ChunkedUploadProperties;
import com.takypok.mediaservice.config.StorageProperties;
import com.takypok.mediaservice.model.UploadSession;
import com.takypok.mediaservice.model.dto.StartChunkedUploadRequest;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * In-memory, single-instance session tracker for chunked uploads. Deliberately not Redis/DB-backed:
 * state doesn't survive a restart and would break if the gateway ever load-balanced one upload's
 * chunks across multiple media-service instances — an accepted limitation, not something to work
 * around here.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UploadSessionRegistry {
  private final StorageProperties storageProperties;
  private final ChunkedUploadProperties chunkedUploadProperties;
  private final ConcurrentHashMap<String, UploadSession> sessions = new ConcurrentHashMap<>();

  public Mono<UploadSession> getOrCreate(StartChunkedUploadRequest request) {
    return Mono.fromCallable(
        () -> {
          String sessionId = validateSessionId(request.sessionId());
          UploadSession existing = sessions.get(sessionId);
          if (existing != null) {
            existing.touch();
            return existing;
          }
          if (sessions.size() >= chunkedUploadProperties.getMaxActiveSessions()) {
            throw new ApplicationException(
                Message.Application.ERROR, "Too many uploads in progress, please retry shortly");
          }
          UploadSession created = open(sessionId, request);
          UploadSession raced = sessions.putIfAbsent(sessionId, created);
          if (raced != null) {
            closeQuietly(created);
            raced.touch();
            return raced;
          }
          log.info(
              "Chunked upload session {} started for '{}' (expected={} bytes)",
              sessionId,
              request.filename(),
              request.totalSizeBytes());
          return created;
        });
  }

  public UploadSession require(String sessionId) {
    UploadSession session = sessions.get(validateSessionId(sessionId));
    if (session == null) {
      throw new ApplicationException(
          Message.Application.ERROR, "Upload session not found or expired");
    }
    return session;
  }

  public void remove(String sessionId) {
    sessions.remove(sessionId);
  }

  public List<UploadSession> evictExpired(Duration idleTimeout) {
    Instant threshold = Instant.now().minus(idleTimeout);
    List<UploadSession> evicted = new ArrayList<>();
    sessions.forEach(
        (id, session) -> {
          if (session.isIdleSince(threshold) && sessions.remove(id, session)) {
            evicted.add(session);
          }
        });
    return evicted;
  }

  public int activeCount() {
    return sessions.size();
  }

  private UploadSession open(String sessionId, StartChunkedUploadRequest request) {
    String extension = getFileExtension(request.filename());
    Path partPath = Path.of(storageProperties.getFilesDir(), sessionId + ".part");
    try {
      AsynchronousFileChannel channel =
          AsynchronousFileChannel.open(
              partPath, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
      return new UploadSession(
          sessionId, request.filename(), extension, partPath, channel, request.totalSizeBytes());
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  /** Validates the sessionId is a well-formed UUID before it's ever used in a filesystem path. */
  private String validateSessionId(String sessionId) {
    try {
      UUID.fromString(sessionId);
    } catch (Exception e) {
      throw new ApplicationException(Message.Application.ERROR, "Invalid session id");
    }
    return sessionId;
  }

  private void closeQuietly(UploadSession session) {
    try {
      session.getChannel().close();
      Files.deleteIfExists(session.getPartPath());
    } catch (IOException e) {
      log.warn(
          "Failed to clean up duplicate session channel {}: {}",
          session.getSessionId(),
          e.getMessage());
    }
  }
}
