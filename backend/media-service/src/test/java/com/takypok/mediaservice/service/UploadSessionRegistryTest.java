package com.takypok.mediaservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.takypok.core.exception.ApplicationException;
import com.takypok.mediaservice.config.ChunkedUploadProperties;
import com.takypok.mediaservice.config.StorageProperties;
import com.takypok.mediaservice.model.UploadSession;
import com.takypok.mediaservice.model.dto.StartChunkedUploadRequest;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class UploadSessionRegistryTest {

  @TempDir Path tempDir;

  private UploadSessionRegistry registry;

  @BeforeEach
  void setUp() {
    StorageProperties storageProperties = new StorageProperties();
    storageProperties.setFilesDir(tempDir.toString());
    ChunkedUploadProperties properties = new ChunkedUploadProperties();
    properties.setMaxActiveSessions(2);
    registry = new UploadSessionRegistry(storageProperties, properties);
  }

  @Test
  void getOrCreate_rejectsNonUuidSessionId() {
    StartChunkedUploadRequest request =
        new StartChunkedUploadRequest("../../etc/passwd", "f.txt", 10L, "vn");

    assertThatThrownBy(() -> registry.getOrCreate(request).block())
        .isInstanceOf(ApplicationException.class);
    assertThat(Files.exists(Path.of("etc"))).isFalse();
  }

  @Test
  void getOrCreate_isIdempotentForARetriedStartCall() {
    String sessionId = UUID.randomUUID().toString();
    StartChunkedUploadRequest request =
        new StartChunkedUploadRequest(sessionId, "f.txt", 10L, "vn");

    UploadSession first = registry.getOrCreate(request).block();
    UploadSession second = registry.getOrCreate(request).block();

    assertThat(first).isSameAs(second);
    assertThat(registry.activeCount()).isEqualTo(1);
  }

  @Test
  void require_throwsForUnknownSession() {
    assertThatThrownBy(() -> registry.require(UUID.randomUUID().toString()))
        .isInstanceOf(ApplicationException.class);
  }

  @Test
  void getOrCreate_rejectsBeyondMaxActiveSessions() {
    registry
        .getOrCreate(new StartChunkedUploadRequest(UUID.randomUUID().toString(), "a.txt", 1L, "vn"))
        .block();
    registry
        .getOrCreate(new StartChunkedUploadRequest(UUID.randomUUID().toString(), "b.txt", 1L, "vn"))
        .block();

    StartChunkedUploadRequest third =
        new StartChunkedUploadRequest(UUID.randomUUID().toString(), "c.txt", 1L, "vn");
    assertThatThrownBy(() -> registry.getOrCreate(third).block())
        .isInstanceOf(ApplicationException.class);
  }

  @Test
  void evictExpired_removesIdleSessionsAndReportsThem() {
    String sessionId = UUID.randomUUID().toString();
    registry.getOrCreate(new StartChunkedUploadRequest(sessionId, "f.txt", 10L, "vn")).block();

    // Any elapsed wall-clock time after creation makes the session idle relative to a
    // zero-duration threshold — no need to sleep or fake the clock.
    List<UploadSession> evicted = registry.evictExpired(Duration.ZERO);

    assertThat(evicted).hasSize(1);
    assertThat(evicted.get(0).getSessionId()).isEqualTo(sessionId);
    assertThat(registry.activeCount()).isZero();
    assertThatThrownBy(() -> registry.require(sessionId)).isInstanceOf(ApplicationException.class);
  }
}
