package com.takypok.authservice.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import org.springframework.stereotype.Component;

/**
 * Time-based one-time passwords (RFC 6238) with the settings every authenticator app understands:
 * HMAC-SHA1, 6 digits, 30-second steps. Google and Microsoft Authenticator ignore other settings,
 * so these must not be changed. Pure computation: no state, the caller passes the time.
 */
@Component
public class TotpService {

  public static final int PERIOD_SECONDS = 30;
  static final int DIGITS = 6;

  /** Steps accepted either side of "now", for a phone clock a little off. */
  static final int DRIFT_STEPS = 1;

  private static final int SECRET_BYTES = 20; // 160 bits, the RFC's recommendation
  private static final char[] BASE32 = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567".toCharArray();

  private final SecureRandom random = new SecureRandom();

  public byte[] newSecret() {
    byte[] secret = new byte[SECRET_BYTES];
    random.nextBytes(secret);
    return secret;
  }

  public static long stepAt(long epochSeconds) {
    return epochSeconds / PERIOD_SECONDS;
  }

  /** The code for one step. */
  public String code(byte[] secret, long step) {
    try {
      javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA1");
      mac.init(new javax.crypto.spec.SecretKeySpec(secret, "HmacSHA1"));
      byte[] counter = new byte[8];
      for (int i = 7; i >= 0; i--) {
        counter[i] = (byte) (step & 0xff);
        step >>= 8;
      }
      byte[] hash = mac.doFinal(counter);
      int offset = hash[hash.length - 1] & 0x0f;
      int binary =
          ((hash[offset] & 0x7f) << 24)
              | ((hash[offset + 1] & 0xff) << 16)
              | ((hash[offset + 2] & 0xff) << 8)
              | (hash[offset + 3] & 0xff);
      int otp = binary % (int) Math.pow(10, DIGITS);
      return String.format("%0" + DIGITS + "d", otp);
    } catch (GeneralSecurityException ex) {
      throw new IllegalStateException("HMAC-SHA1 is not available", ex);
    }
  }

  /**
   * The step that {@code input} is the code for, or -1. Only steps after {@code lastUsedStep}
   * count, so a code that was accepted once cannot be used again.
   */
  public long verify(byte[] secret, String input, long epochSeconds, long lastUsedStep) {
    if (input == null) return -1;
    String digits = input.replaceAll("\\s", "");
    if (digits.length() != DIGITS || !digits.chars().allMatch(c -> c >= '0' && c <= '9')) {
      return -1;
    }
    long now = stepAt(epochSeconds);
    long matched = -1;
    for (long step = now - DRIFT_STEPS; step <= now + DRIFT_STEPS; step++) {
      boolean same =
          MessageDigest.isEqual(
              code(secret, step).getBytes(StandardCharsets.US_ASCII),
              digits.getBytes(StandardCharsets.US_ASCII));
      if (same && step > lastUsedStep) {
        matched = step; // keep looping: the time taken must not depend on which step matched
      }
    }
    return matched;
  }

  /** What the QR code holds. {@code account} is shown under the issuer in the app. */
  public String otpauthUri(String issuer, String account, byte[] secret) {
    String label = enc(issuer) + ":" + enc(account);
    return "otpauth://totp/"
        + label
        + "?secret="
        + base32(secret)
        + "&issuer="
        + enc(issuer)
        + "&algorithm=SHA1&digits="
        + DIGITS
        + "&period="
        + PERIOD_SECONDS;
  }

  public static String base32(byte[] data) {
    StringBuilder out = new StringBuilder((data.length * 8 + 4) / 5);
    int buffer = 0;
    int bits = 0;
    for (byte b : data) {
      buffer = (buffer << 8) | (b & 0xff);
      bits += 8;
      while (bits >= 5) {
        out.append(BASE32[(buffer >> (bits - 5)) & 31]);
        bits -= 5;
      }
    }
    if (bits > 0) {
      out.append(BASE32[(buffer << (5 - bits)) & 31]);
    }
    return out.toString();
  }

  private static String enc(String value) {
    return URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20");
  }
}
