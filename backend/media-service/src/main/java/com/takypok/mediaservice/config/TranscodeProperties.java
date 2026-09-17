package com.takypok.mediaservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "media.transcode")
@Getter
@Setter
public class TranscodeProperties {
  /** Max number of videos FFmpeg is actively transcoding at the same time. */
  private int maxConcurrentJobs = 2;

  /**
   * Extra submissions allowed to wait once maxConcurrentJobs is busy before new ones are rejected.
   */
  private int queueCapacity = 50;
}
