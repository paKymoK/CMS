package com.takypok.authservice.service;

import com.takypok.authservice.model.entity.AuditAction;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Optional two-step verification with an authenticator app (TOTP) and one-time recovery codes. It
 * is per account and off until the person turns it on from their profile. A wrong code (from any
 * screen) counts toward one counter, {@code mfa-fail:<sub>}, that a correct password does NOT
 * reset: otherwise someone who knew the password could guess six-digit codes without limit.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MfaService {

  static final String FAIL_PREFIX = "mfa-fail:";
  static final int MAX_ATTEMPTS = 5;
  private static final Duration LOCKOUT = Duration.ofMinutes(10);
  public static final int RECOVERY_CODES = 10;
  private static final int RECOVERY_LENGTH = 10;
  // No 0/o/1/l/i: a code read off paper must not be ambiguous.
  private static final String RECOVERY_ALPHABET = "abcdefghjkmnpqrstuvwxyz23456789";

  public enum Outcome {
    OK,
    RECOVERY_USED,
    WRONG,
    LOCKED;

    public boolean accepted() {
      return this == OK || this == RECOVERY_USED;
    }
  }

  /**
   * A secret waiting for its first code: the text key and the {@code otpauth://} link for the QR.
   */
  public record Enrollment(String secret, String uri) {}

  /** {@code codes} is only filled when {@code outcome} is accepted. */
  public record Codes(Outcome outcome, List<String> codes) {}

  public record Status(boolean enabled, ZonedDateTime since, int recoveryLeft) {}

  private record Row(String secretEnc, boolean enabled, long lastUsedStep) {}

  private final NamedParameterJdbcTemplate jdbc;
  private final TotpService totp;
  private final MfaSecretCipher cipher;
  private final StringRedisTemplate redis;
  private final AuditService audit;

  @Value("${auth.mfa.issuer:CMC Global CMS}")
  private String issuer;

  private final SecureRandom random = new SecureRandom();

  /** Test seam. */
  Clock clock = Clock.systemUTC();

  /** Whether the service can be used at all (an encryption key is configured). */
  public boolean isAvailable() {
    return cipher.isConfigured();
  }

  public boolean isEnabled(String sub) {
    Boolean enabled =
        jdbc.queryForObject(
            "SELECT EXISTS (SELECT 1 FROM user_mfa WHERE sub = :sub AND enabled)",
            new MapSqlParameterSource("sub", sub),
            Boolean.class);
    return Boolean.TRUE.equals(enabled);
  }

  public Status status(String sub) {
    List<ZonedDateTime> since =
        jdbc.query(
            "SELECT confirmed_at FROM user_mfa WHERE sub = :sub AND enabled",
            new MapSqlParameterSource("sub", sub),
            (rs, n) -> {
              OffsetDateTime at = rs.getObject("confirmed_at", OffsetDateTime.class);
              return at == null ? null : at.atZoneSameInstant(ZoneOffset.UTC);
            });
    if (since.isEmpty()) {
      return new Status(false, null, 0);
    }
    Integer left =
        jdbc.queryForObject(
            "SELECT count(*) FROM user_mfa_recovery WHERE sub = :sub AND used_at IS NULL",
            new MapSqlParameterSource("sub", sub),
            Integer.class);
    return new Status(true, since.get(0), left == null ? 0 : left);
  }

  // ── setting up ──

  /**
   * The secret to show for setup. An unconfirmed setup is reused (so reloading the page does not
   * change the key the person just scanned); an account that is already on cannot start again.
   */
  @Transactional
  public Enrollment beginOrResume(String sub) {
    Row row = find(sub);
    if (row != null && row.enabled()) {
      throw new IllegalStateException("Two-step verification is already on");
    }
    byte[] secret = null;
    if (row != null) {
      try {
        secret = cipher.decrypt(sub, row.secretEnc());
      } catch (IllegalStateException ex) {
        log.warn("Discarding an unreadable unconfirmed secret for {}", sub);
      }
    }
    if (secret == null) {
      secret = totp.newSecret();
      jdbc.update(
          "INSERT INTO user_mfa (sub, secret_enc, enabled, last_used_step)"
              + " VALUES (:sub, :enc, FALSE, 0)"
              + " ON CONFLICT (sub) DO UPDATE SET secret_enc = :enc, enabled = FALSE,"
              + " last_used_step = 0, created_at = now()",
          new MapSqlParameterSource("sub", sub).addValue("enc", cipher.encrypt(sub, secret)));
    }
    return new Enrollment(TotpService.base32(secret), totp.otpauthUri(issuer, sub, secret));
  }

  /** Turns it on if {@code code} (from the app) is right, and returns the recovery codes. */
  @Transactional
  public Codes confirm(String sub, String code) {
    Row row = find(sub);
    if (row == null || row.enabled()) {
      return new Codes(Outcome.WRONG, List.of());
    }
    Outcome outcome = check(sub, code, false, false, null, "setup");
    if (!outcome.accepted()) {
      return new Codes(outcome, List.of());
    }
    jdbc.update(
        "UPDATE user_mfa SET enabled = TRUE, confirmed_at = now() WHERE sub = :sub",
        new MapSqlParameterSource("sub", sub));
    List<String> codes = newRecoveryCodes(sub);
    audit.record(AuditAction.MFA_ENABLED, sub, "authenticator app");
    return new Codes(Outcome.OK, codes);
  }

  /** Drops a setup that was not confirmed. A turned-on account is not touched. */
  public void cancelSetup(String sub) {
    jdbc.update(
        "DELETE FROM user_mfa WHERE sub = :sub AND NOT enabled",
        new MapSqlParameterSource("sub", sub));
  }

  // ── using it ──

  /** The second step of a sign-in: an app code, or a recovery code (used up by this call). */
  public Outcome verifyAtLogin(String sub, String input, String ip) {
    return check(sub, input, true, true, ip, "sign-in");
  }

  /** Turns it off after proof (an app code or a recovery code). */
  @Transactional
  public Outcome disable(String sub, String input) {
    Outcome outcome = check(sub, input, true, true, null, "turning it off");
    if (outcome.accepted()) {
      removeAll(sub);
      audit.record(AuditAction.MFA_DISABLED, sub, null);
    }
    return outcome;
  }

  /** A new set of recovery codes (the old ones stop working), after proof. */
  @Transactional
  public Codes renewRecoveryCodes(String sub, String input) {
    Outcome outcome = check(sub, input, true, true, null, "new recovery codes");
    if (!outcome.accepted()) {
      return new Codes(outcome, List.of());
    }
    List<String> codes = newRecoveryCodes(sub);
    audit.record(AuditAction.MFA_RECOVERY_RENEWED, sub, null);
    return new Codes(Outcome.OK, codes);
  }

  /** An administrator's reset for a lost phone. @return whether there was anything to reset */
  @Transactional
  public boolean reset(String sub) {
    boolean had = find(sub) != null;
    removeAll(sub);
    if (had) {
      audit.record(AuditAction.MFA_RESET, sub, null);
    }
    clearFailures(sub);
    return had;
  }

  // ── internals ──

  private Outcome check(
      String sub,
      String input,
      boolean requireEnabled,
      boolean allowRecovery,
      String ip,
      String context) {
    if (isLocked(sub)) {
      audit.recordQuietly(sub, AuditAction.MFA_FAILED, sub, "locked, " + context, ip);
      return Outcome.LOCKED;
    }
    Row row = find(sub);
    if (row != null && (row.enabled() || !requireEnabled)) {
      try {
        byte[] secret = cipher.decrypt(sub, row.secretEnc());
        long step =
            totp.verify(secret, input, clock.instant().getEpochSecond(), row.lastUsedStep());
        if (step > 0 && markUsed(sub, step)) {
          clearFailures(sub);
          return Outcome.OK;
        }
      } catch (IllegalStateException ex) {
        log.error("Two-step secret of {} cannot be read: {}", sub, ex.getMessage());
      }
      if (allowRecovery && useRecoveryCode(sub, input)) {
        clearFailures(sub);
        audit.recordQuietly(sub, AuditAction.MFA_RECOVERY_USED, sub, context, ip);
        return Outcome.RECOVERY_USED;
      }
    }
    recordFailure(sub);
    audit.recordQuietly(sub, AuditAction.MFA_FAILED, sub, context, ip);
    return Outcome.WRONG;
  }

  /** Atomic, so two requests with the same code cannot both pass. */
  private boolean markUsed(String sub, long step) {
    return jdbc.update(
            "UPDATE user_mfa SET last_used_step = :step WHERE sub = :sub AND last_used_step < :step",
            new MapSqlParameterSource("sub", sub).addValue("step", step))
        == 1;
  }

  private boolean useRecoveryCode(String sub, String input) {
    String normalized = normalizeRecovery(input);
    if (normalized.length() != RECOVERY_LENGTH) {
      return false;
    }
    return jdbc.update(
            "UPDATE user_mfa_recovery SET used_at = now()"
                + " WHERE id = (SELECT id FROM user_mfa_recovery WHERE sub = :sub"
                + " AND code_hash = :hash AND used_at IS NULL LIMIT 1)",
            new MapSqlParameterSource("sub", sub).addValue("hash", sha256(normalized)))
        == 1;
  }

  private List<String> newRecoveryCodes(String sub) {
    jdbc.update(
        "DELETE FROM user_mfa_recovery WHERE sub = :sub", new MapSqlParameterSource("sub", sub));
    List<String> shown = new ArrayList<>();
    for (int i = 0; i < RECOVERY_CODES; i++) {
      StringBuilder code = new StringBuilder();
      for (int c = 0; c < RECOVERY_LENGTH; c++) {
        code.append(RECOVERY_ALPHABET.charAt(random.nextInt(RECOVERY_ALPHABET.length())));
      }
      jdbc.update(
          "INSERT INTO user_mfa_recovery (sub, code_hash) VALUES (:sub, :hash)",
          new MapSqlParameterSource("sub", sub).addValue("hash", sha256(code.toString())));
      shown.add(code.substring(0, 5) + "-" + code.substring(5));
    }
    return shown;
  }

  private void removeAll(String sub) {
    MapSqlParameterSource p = new MapSqlParameterSource("sub", sub);
    jdbc.update("DELETE FROM user_mfa_recovery WHERE sub = :sub", p);
    jdbc.update("DELETE FROM user_mfa WHERE sub = :sub", p);
  }

  private Row find(String sub) {
    List<Row> rows =
        jdbc.query(
            "SELECT secret_enc, enabled, last_used_step FROM user_mfa WHERE sub = :sub",
            new MapSqlParameterSource("sub", sub),
            (rs, n) ->
                new Row(
                    rs.getString("secret_enc"),
                    rs.getBoolean("enabled"),
                    rs.getLong("last_used_step")));
    return rows.isEmpty() ? null : rows.get(0);
  }

  static String normalizeRecovery(String input) {
    return input == null ? "" : input.toLowerCase().replaceAll("[^a-z0-9]", "");
  }

  private static String sha256(String value) {
    try {
      return HexFormat.of()
          .formatHex(
              MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8)));
    } catch (NoSuchAlgorithmException ex) {
      throw new IllegalStateException(ex);
    }
  }

  // Redis down = no limit, like the password counters (a Redis outage must not stop sign-ins).

  private boolean isLocked(String sub) {
    try {
      String value = redis.opsForValue().get(FAIL_PREFIX + sub);
      return value != null && Integer.parseInt(value) >= MAX_ATTEMPTS;
    } catch (Exception ex) {
      log.warn("Redis unavailable, two-step lock check skipped for {}: {}", sub, ex.getMessage());
      return false;
    }
  }

  private void recordFailure(String sub) {
    try {
      String key = FAIL_PREFIX + sub;
      Long count = redis.opsForValue().increment(key);
      if (count != null && count == 1) {
        redis.expire(key, LOCKOUT);
      }
    } catch (Exception ex) {
      log.warn("Redis unavailable, two-step failure not counted for {}: {}", sub, ex.getMessage());
    }
  }

  private void clearFailures(String sub) {
    try {
      redis.delete(FAIL_PREFIX + sub);
    } catch (Exception ex) {
      log.warn("Redis unavailable, two-step failures not cleared for {}: {}", sub, ex.getMessage());
    }
  }
}
