package com.takypok.mediaservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@Configuration
@RequiredArgsConstructor
public class TranscodeSchedulerConfig {
  private final TranscodeProperties properties;

  /**
   * A bounded pool caps how many FFmpeg processes can run at once — previously transcode work ran
   * on the shared, effectively-unbounded Schedulers.boundedElastic(), so a burst of concurrent
   * video uploads (much more likely from chat than from ticket attachments) could saturate host CPU
   * with no backpressure. Once maxConcurrentJobs + queueCapacity is exceeded, new submissions fail
   * fast instead of piling up indefinitely.
   */
  @Bean
  public Scheduler transcodeScheduler() {
    return Schedulers.newBoundedElastic(
        properties.getMaxConcurrentJobs(), properties.getQueueCapacity(), "video-transcode");
  }
}
