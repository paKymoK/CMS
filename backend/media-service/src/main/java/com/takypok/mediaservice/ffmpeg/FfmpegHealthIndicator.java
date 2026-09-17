package com.takypok.mediaservice.ffmpeg;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component("ffmpeg")
@RequiredArgsConstructor
public class FfmpegHealthIndicator implements ReactiveHealthIndicator {

  private final FfmpegResolver resolver;

  @Override
  public Mono<Health> health() {
    return Mono.fromCallable(this::check).subscribeOn(Schedulers.boundedElastic());
  }

  private Health check() {
    String path = resolver.resolve();
    if (path == null) {
      return Health.down().withDetail("error", "FFmpeg not resolved").build();
    }
    try {
      ProcessBuilder pb = new ProcessBuilder(path, "-version");
      pb.redirectErrorStream(true);
      Process p = pb.start();
      String output = new String(p.getInputStream().readAllBytes());
      boolean finished = p.waitFor(5, TimeUnit.SECONDS);
      if (!finished) {
        p.destroyForcibly();
        return Health.down().withDetail("path", path).withDetail("error", "timed out").build();
      }
      if (p.exitValue() != 0) {
        return Health.down()
            .withDetail("path", path)
            .withDetail("error", "exit code " + p.exitValue())
            .build();
      }
      String version = output.lines().findFirst().orElse("unknown");
      return Health.up().withDetail("path", path).withDetail("version", version).build();
    } catch (Exception e) {
      return Health.down().withDetail("path", path).withDetail("error", e.getMessage()).build();
    }
  }
}
