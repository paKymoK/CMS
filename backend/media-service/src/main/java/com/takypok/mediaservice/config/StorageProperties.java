package com.takypok.mediaservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "storage")
@Getter
@Setter
public class StorageProperties {
  private String baseDir = "./uploads";
  private String imagesDir = "./uploads/images";
  private String rawDir = "./uploads/raw";
  private String hlsDir = "./uploads/hls";
  private String filesDir = "./uploads/files";
}
