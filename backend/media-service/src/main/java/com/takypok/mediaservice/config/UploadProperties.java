package com.takypok.mediaservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "media.upload")
@Getter
@Setter
public class UploadProperties {
  private long maxImageSize = 10L * 1024 * 1024; // 10 MB
  private long maxVideoSize = 512L * 1024 * 1024; // 512 MB
}
