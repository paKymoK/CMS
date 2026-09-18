package com.takypok.mediaservice.model;

import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Path;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.LongAdder;
import lombok.Getter;

@Getter
public class UploadSession {
  private final String sessionId;
  private final String filename;
  private final String extension;
  private final String site;
  private final Path partPath;
  private final AsynchronousFileChannel channel;
  private final Long expectedTotalSizeBytes;
  private final ConcurrentSkipListSet<Integer> receivedIndexes = new ConcurrentSkipListSet<>();

  /**
   * Actual byte length received per chunk index — used at finish time to detect a short
   * (undersized) chunk that isn't the final one, which would otherwise leave a silent gap in the
   * assembled file under the positional-write scheme.
   */
  private final ConcurrentHashMap<Integer, Integer> chunkLengths = new ConcurrentHashMap<>();

  private final LongAdder totalBytesReceived = new LongAdder();
  private final Instant createdAt = Instant.now();
  private final AtomicBoolean finishing = new AtomicBoolean(false);
  private volatile Instant lastActivityAt = Instant.now();

  public UploadSession(
      String sessionId,
      String filename,
      String extension,
      String site,
      Path partPath,
      AsynchronousFileChannel channel,
      Long expectedTotalSizeBytes) {
    this.sessionId = sessionId;
    this.filename = filename;
    this.extension = extension;
    this.site = site;
    this.partPath = partPath;
    this.channel = channel;
    this.expectedTotalSizeBytes = expectedTotalSizeBytes;
  }

  public void touch() {
    lastActivityAt = Instant.now();
  }

  public boolean isIdleSince(Instant threshold) {
    // Not strict isBefore(): on a coarse-resolution clock, back-to-back Instant.now() calls can
    // land on the same instant, which would make a session created just before the threshold
    // check never count as idle.
    return !lastActivityAt.isAfter(threshold);
  }
}
