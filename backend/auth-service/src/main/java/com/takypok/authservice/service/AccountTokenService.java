package com.takypok.authservice.service;

import com.takypok.authservice.model.entity.AccountTokenPurpose;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * One-time links for invitation and password reset. The raw token (256 random bits) exists only in
 * the email; the table keeps its SHA-256, so a leaked copy of the table opens nothing. A token can
 * be used once (the check and the marking are one statement, so two clicks at once cannot both win)
 * and only before it expires. Issuing a new one for the same account and purpose replaces the old.
 */
@Service
@RequiredArgsConstructor
public class AccountTokenService {

  private static final SecureRandom RANDOM = new SecureRandom();

  private final NamedParameterJdbcTemplate jdbc;

  /** Creates a link token for {@code sub}, replacing earlier ones, and returns the raw value. */
  public String issue(String sub, AccountTokenPurpose purpose, Duration ttl) {
    byte[] bytes = new byte[32];
    RANDOM.nextBytes(bytes);
    String raw = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    jdbc.update(
        "DELETE FROM account_token WHERE (sub = :sub AND purpose = :purpose) OR expires_at < now()",
        new MapSqlParameterSource("sub", sub).addValue("purpose", purpose.name()));
    jdbc.update(
        "INSERT INTO account_token (token_hash, sub, purpose, expires_at)"
            + " VALUES (:hash, :sub, :purpose, :expires)",
        new MapSqlParameterSource("hash", hash(raw))
            .addValue("sub", sub)
            .addValue("purpose", purpose.name())
            .addValue("expires", OffsetDateTime.now(ZoneOffset.UTC).plus(ttl)));
    return raw;
  }

  /** The account this token is for, if it is still usable. Does not use it up. */
  public Optional<String> peek(String raw, AccountTokenPurpose purpose) {
    if (raw == null || raw.isBlank()) return Optional.empty();
    return jdbc
        .query(
            "SELECT sub FROM account_token WHERE token_hash = :hash AND purpose = :purpose"
                + " AND used_at IS NULL AND expires_at > now()",
            new MapSqlParameterSource("hash", hash(raw)).addValue("purpose", purpose.name()),
            (rs, n) -> rs.getString("sub"))
        .stream()
        .findFirst();
  }

  /** Uses the token up and returns its account, or empty if it is unknown, used or expired. */
  public Optional<String> consume(String raw, AccountTokenPurpose purpose) {
    if (raw == null || raw.isBlank()) return Optional.empty();
    return jdbc
        .query(
            "UPDATE account_token SET used_at = now() WHERE token_hash = :hash AND purpose = :purpose"
                + " AND used_at IS NULL AND expires_at > now() RETURNING sub",
            new MapSqlParameterSource("hash", hash(raw)).addValue("purpose", purpose.name()),
            (rs, n) -> rs.getString("sub"))
        .stream()
        .findFirst();
  }

  static String hash(String raw) {
    try {
      byte[] d = MessageDigest.getInstance("SHA-256").digest(raw.getBytes(StandardCharsets.UTF_8));
      return HexFormat.of().formatHex(d);
    } catch (NoSuchAlgorithmException ex) {
      throw new IllegalStateException(ex);
    }
  }
}
