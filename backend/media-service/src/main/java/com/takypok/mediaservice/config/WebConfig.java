package com.takypok.mediaservice.config;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.reactive.config.ResourceHandlerRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebFluxConfigurer {

  private final StorageProperties storageProperties;

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    String imagesLocation = "file:" + storageProperties.getImagesDir() + "/";
    // Uploaded images are content-addressed by a generated UUID id and never modified
    // in place, so a long, immutable cache is safe — every repeat view in a chat feed
    // (scrolling back through history) previously re-read the file from disk every time.
    registry
        .addResourceHandler("/images/**")
        .addResourceLocations(imagesLocation)
        .setCacheControl(CacheControl.maxAge(Duration.ofDays(365)).cachePublic().immutable());

    String filesLocation = "file:" + storageProperties.getFilesDir() + "/";
    // Same content-addressed/immutable reasoning as /images/** above, for chunked uploads.
    registry
        .addResourceHandler("/files/**")
        .addResourceLocations(filesLocation)
        .setCacheControl(CacheControl.maxAge(Duration.ofDays(365)).cachePublic().immutable());
  }
}
