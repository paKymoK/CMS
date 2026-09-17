package com.takypok.mediaservice.ffmpeg;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FfmpegResolver {

  @Value("${ffmpeg.path:}")
  private String ffmpegSystemPath;

  @Value("${ffprobe.path:}")
  private String ffprobeSystemPath;

  @Value("${ffmpeg.cache-dir:~/.cache/media-service/ffmpeg}")
  private String cacheDir;

  private String resolvedPath;
  private String resolvedProbePath;

  @PostConstruct
  public void init() throws IOException {
    if (!ffmpegSystemPath.isBlank() && !ffprobeSystemPath.isBlank()) {
      resolvedPath = ffmpegSystemPath;
      resolvedProbePath = ffprobeSystemPath;
      log.info(
          "[FFmpeg] Using system binaries — ffmpeg: {}, ffprobe: {}",
          resolvedPath,
          resolvedProbePath);
      return;
    }

    boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");
    String ffmpeg = isWindows ? "ffmpeg.exe" : "ffmpeg";
    String ffprobe = isWindows ? "ffprobe.exe" : "ffprobe";

    Path destDir = expandedCacheDir();
    Files.createDirectories(destDir);

    extractPlatformDir(destDir, ffmpeg, ffprobe, isWindows);

    resolvedPath = destDir.resolve(ffmpeg).toString();
    resolvedProbePath = destDir.resolve(ffprobe).toString();
    log.info("[FFmpeg] Resolved ffmpeg at: {}", resolvedPath);
    log.info("[FFmpeg] Resolved ffprobe at: {}", resolvedProbePath);
  }

  public String resolve() {
    return resolvedPath;
  }

  public String resolveProbe() {
    return resolvedProbePath;
  }

  private void extractPlatformDir(Path destDir, String ffmpeg, String ffprobe, boolean isWindows)
      throws IOException {
    String platformPrefix = "org/bytedeco/ffmpeg/" + getPlatformDir() + "/";

    // Use the ffmpeg binary as the anchor entry to locate the jar
    URL entryUrl = FfmpegResolver.class.getClassLoader().getResource(platformPrefix + ffmpeg);
    if (entryUrl == null) {
      throw new IllegalStateException(
          "FFmpeg binary not found in classpath under: " + platformPrefix);
    }

    String protocol = entryUrl.getProtocol();
    if ("jar".equals(protocol)) {
      JarURLConnection conn = (JarURLConnection) entryUrl.openConnection();
      conn.setUseCaches(false);
      try (JarFile jar = conn.getJarFile()) {
        int extracted = 0;
        var entries = jar.entries();
        while (entries.hasMoreElements()) {
          var entry = entries.nextElement();
          String name = entry.getName();
          if (!name.startsWith(platformPrefix) || entry.isDirectory()) {
            continue;
          }
          String fileName = name.substring(platformPrefix.length());
          if (fileName.isEmpty()) {
            continue;
          }
          Path dest = destDir.resolve(fileName);
          try (InputStream in = jar.getInputStream(entry)) {
            Files.copy(in, dest, StandardCopyOption.REPLACE_EXISTING);
          }
          extracted++;
        }
        log.info("[FFmpeg] Extracted {} files to {}", extracted, destDir);
      }
    } else {
      // Exploded classpath (IDE / bootRun with exploded deps) — extract just the two binaries
      copyResource(platformPrefix + ffmpeg, destDir.resolve(ffmpeg));
      copyResource(platformPrefix + ffprobe, destDir.resolve(ffprobe));
      log.info("[FFmpeg] Extracted binaries (exploded classpath) to {}", destDir);
    }

    if (!isWindows) {
      setExecutable(destDir.resolve(ffmpeg));
      setExecutable(destDir.resolve(ffprobe));
    }
  }

  private void copyResource(String resourcePath, Path dest) throws IOException {
    try (InputStream in = FfmpegResolver.class.getClassLoader().getResourceAsStream(resourcePath)) {
      if (in == null) {
        throw new IllegalStateException("Resource not found in classpath: " + resourcePath);
      }
      Files.copy(in, dest, StandardCopyOption.REPLACE_EXISTING);
    }
  }

  private void setExecutable(Path path) throws IOException {
    Set<PosixFilePermission> perms = new HashSet<>(Files.getPosixFilePermissions(path));
    perms.add(PosixFilePermission.OWNER_EXECUTE);
    perms.add(PosixFilePermission.GROUP_EXECUTE);
    perms.add(PosixFilePermission.OTHERS_EXECUTE);
    Files.setPosixFilePermissions(path, perms);
  }

  private String getPlatformDir() {
    String os = System.getProperty("os.name").toLowerCase();
    String arch = System.getProperty("os.arch").toLowerCase();
    boolean isArm = arch.contains("aarch64") || arch.contains("arm");

    if (os.contains("win")) return "windows-x86_64";
    if (os.contains("mac")) return isArm ? "macosx-arm64" : "macosx-x86_64";
    return isArm ? "linux-arm64" : "linux-x86_64";
  }

  private Path expandedCacheDir() {
    return Path.of(cacheDir.replace("~", System.getProperty("user.home")));
  }
}
