package com.takypok.mediaservice.util;

import com.takypok.core.exception.ApplicationException;
import com.takypok.core.model.Message;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import reactor.core.publisher.Flux;

/**
 * Caps a streamed multipart upload at {@code maxBytes}, erroring out mid-stream instead of
 * buffering the whole file before checking — a client can't exhaust disk by uploading whatever size
 * it wants.
 */
public class UploadSizeLimiter {
  private UploadSizeLimiter() {}

  public static Flux<DataBuffer> enforce(Flux<DataBuffer> content, long maxBytes, String label) {
    AtomicLong total = new AtomicLong();
    return content.handle(
        (buffer, sink) -> {
          try {
            total.set(checkWithinLimit(total.get(), buffer.readableByteCount(), maxBytes, label));
          } catch (ApplicationException e) {
            DataBufferUtils.release(buffer);
            sink.error(e);
            return;
          }
          sink.next(buffer);
        });
  }

  /**
   * Adds {@code incoming} to {@code totalSoFar} and throws if the result exceeds {@code maxBytes},
   * otherwise returns the new running total. Shared by {@link #enforce} (one continuous stream) and
   * callers tracking a running total across separate requests (e.g. chunked uploads).
   */
  public static long checkWithinLimit(long totalSoFar, long incoming, long maxBytes, String label) {
    long newTotal = totalSoFar + incoming;
    if (newTotal > maxBytes) {
      throw new ApplicationException(
          Message.Application.ERROR,
          label + " exceeds maximum allowed size of " + (maxBytes / (1024 * 1024)) + "MB");
    }
    return newTotal;
  }
}
