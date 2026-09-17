package com.takypok.mediaservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "media.chunked-upload")
@Getter
@Setter
public class ChunkedUploadProperties {
  /**
   * The fixed size every chunk except the last must be. This isn't just a validation ceiling — the
   * server uses it to compute each chunk's write offset ({@code index * chunkSizeBytes}), so
   * clients must slice to exactly this size (the /start response echoes it back for that reason).
   */
  private long chunkSizeBytes = 8L * 1024; // 8 KB

  private long maxTotalSizeBytes = 200L * 1024 * 1024; // 200 MB
  private long sessionIdleTimeoutSeconds = 900; // 15 min
  private long sweepIntervalSeconds = 60;
  private int maxActiveSessions = 200;

  /**
   * Symmetric AES-256 key (base64), shared with the frontend, used to wrap chunk bytes in AES-GCM
   * before they cross the wire. This exists only to defeat content-signature inspection by network
   * proxies that block recognizable binary file headers (e.g. the ZIP signature on .xlsx/.docx) —
   * it is not a confidentiality boundary against anyone who has the client bundle, so it's fine for
   * this default to ship in source. Override via config for a different value.
   */
  private String encryptionKeyBase64 = "UUF1mzp0rKSFTi8GEiS9P4kVJN6VaFfwZuZ5EPevtLA=";
}
