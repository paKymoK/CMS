package com.takypok.mediaservice.service;

import com.takypok.mediaservice.config.StorageProperties;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

/**
 * Copies the bundled seed images (src/main/resources/seed-images/&lt;id&gt;&lt;ext&gt;) into the
 * images storage directory at startup, so a fresh environment serves them. The matching upload_file
 * rows come from the init-data.sql changelog — same ids — which is what lets content-service's seed
 * reference them by origin-less URL.
 *
 * <p>Never overwrites: a file already present (the same id from an earlier start, or an editor's
 * own upload that happens to collide — practically impossible with uuid v5 ids) is left alone. Only
 * names matching a strict {@code <uuid>.<ext>} shape are installed, so nothing on the classpath can
 * steer a write outside the images directory. Disable with {@code media.seed-images.enabled=false}
 * in an environment that should not carry seed data.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
    prefix = "media.seed-images",
    name = "enabled",
    havingValue = "true",
    matchIfMissing = true)
public class SeedImageInstaller implements ApplicationRunner {
  private static final String LOCATION = "classpath:seed-images/*";
  private static final Pattern SAFE_NAME =
      Pattern.compile(
          "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}\\.[a-z0-9]{1,8}$");

  private final StorageProperties storageProperties;

  @Override
  public void run(ApplicationArguments args) throws IOException {
    int installed = install(new PathMatchingResourcePatternResolver().getResources(LOCATION));
    if (installed > 0) {
      log.info("Installed {} seed image(s) into {}", installed, storageProperties.getImagesDir());
    }
  }

  /** Returns how many files were newly written. */
  int install(Resource[] resources) throws IOException {
    Path imagesDir = Path.of(storageProperties.getImagesDir()).toAbsolutePath().normalize();
    Files.createDirectories(imagesDir);
    int installed = 0;
    for (Resource resource : resources) {
      String name = resource.getFilename();
      if (name == null || !SAFE_NAME.matcher(name).matches()) {
        log.warn("Skipping seed image with unexpected name: {}", name);
        continue;
      }
      Path target = imagesDir.resolve(name).normalize();
      if (!target.startsWith(imagesDir) || Files.exists(target)) {
        continue;
      }
      try (InputStream in = resource.getInputStream()) {
        Files.copy(in, target);
        installed++;
      }
    }
    return installed;
  }
}
