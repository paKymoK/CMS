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
   * A bounded pool caps how many FFmpeg processes can run at once, so a burst of concurrent
   * video uploads can't saturate host CPU. Once maxConcurrentJobs + queueCapacity is exceeded, new submissions fail
   * fast instead of piling up indefinitely.
   */
  @Bean
  public Scheduler transcodeScheduler() {
    return Schedulers.newBoundedElastic(
        properties.getMaxConcurrentJobs(), properties.getQueueCapacity(), "video-transcode");
  }
}
