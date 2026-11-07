package com.takypok.authservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.takypok.authservice.model.entity.AuditAction;
import jakarta.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * The devices a person is signed in on (BRD USR-05 "session management"). A device is one OAuth
 * authorization (its refresh token) plus the browser session behind it ({@link WebSessions}).
 * Ending one deletes the refresh token, blacklists the access token (see below), and ends the
 * browser session so it cannot sign back in silently.
 *
 * <p>Limit: an access token already issued stays valid until it expires (15 minutes) for services
 * that do not check the blacklist. The gateway checks it only for clients with the single-tab
 * policy, which cms-admin does not have, so API calls made with an already issued access token can
 * continue for up to 15 minutes after a device is ended; it can no longer renew it.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SessionService {

  /** The Redis key prefix the gateway reads (see SingleTabOAuth2AuthorizationService). */
  static final String REVOKED_KEY_PREFIX = "revoked:jti:";

  public record SessionView(
      String id,
      String client,
      String device,
      String ip,
      String lastIp,
      ZonedDateTime signedInAt,
      ZonedDateTime lastSeenAt,
      ZonedDateTime expiresAt,
      boolean current) {}

  private final NamedParameterJdbcTemplate jdbc;
  private final WebSessions webSessions;
  private final AuditService audit;
  private final StringRedisTemplate redis;
  private final ObjectMapper objectMapper;

  // ── recording ──

  /**
   * Notes that this authorization was used just now, from this request: where, with what browser,
   * and in which browser session. Never throws: a failure here must not stop a token being issued.
   */
  public void touch(String authorizationId, String sub) {
    try {
      String ip = null;
      String ua = null;
      String hash = null;
      if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attrs) {
        HttpServletRequest request = attrs.getRequest();
        ip = request.getRemoteAddr();
        ua = cut(request.getHeader("User-Agent"), 300);
        hash = WebSessions.hashOf(request);
      }
      // The first sight sets where it began; later ones only move "last seen" and fill gaps (the
      // token calls come without the browser cookie, the authorize call without a refresh).
      jdbc.update(
          "INSERT INTO auth_session (authorization_id, sub, web_session_hash, ip, last_ip, user_agent)"
              + " VALUES (:id, :sub, :hash, :ip, :ip, :ua)"
              + " ON CONFLICT (authorization_id) DO UPDATE SET last_seen_at = now(),"
              + " last_ip = COALESCE(EXCLUDED.last_ip, auth_session.last_ip),"
              + " web_session_hash = COALESCE(auth_session.web_session_hash, EXCLUDED.web_session_hash),"
              + " user_agent = COALESCE(auth_session.user_agent, EXCLUDED.user_agent)",
          new MapSqlParameterSource("id", authorizationId)
              .addValue("sub", sub)
              .addValue("hash", hash)
              .addValue("ip", ip)
              .addValue("ua", ua));
    } catch (RuntimeException ex) {
      log.warn("Could not record the session of {}: {}", sub, ex.getMessage());
    }
  }

  // ── reading ──

  /** The devices {@code sub} is signed in on now, most recently used first. */
  public List<SessionView> list(String sub) {
    String currentHash = webSessions.currentHash();
    List<SessionView> out = new ArrayList<>();
    Instant now = Instant.now();
    jdbc.query(
        "SELECT s.authorization_id, s.web_session_hash, s.ip, s.last_ip, s.user_agent, s.created_at,"
            + " s.last_seen_at, a.refresh_token_expires_at, c.client_id"
            + " FROM auth_session s JOIN oauth2_authorization a ON a.id = s.authorization_id"
            + " JOIN oauth2_registered_client c ON c.id = a.registered_client_id"
            + " WHERE s.sub = :sub AND a.refresh_token_value IS NOT NULL"
            + " ORDER BY s.last_seen_at DESC",
        new MapSqlParameterSource("sub", sub),
        rs -> {
          Timestamp expires = rs.getTimestamp("refresh_token_expires_at");
          if (expires != null && !expires.toInstant().isAfter(now))
            return; // expired, not a session
          String hash = rs.getString("web_session_hash");
          out.add(
              new SessionView(
                  rs.getString("authorization_id"),
                  rs.getString("client_id"),
                  UserAgents.describe(rs.getString("user_agent")),
                  rs.getString("ip"),
                  rs.getString("last_ip"),
                  utc(rs.getObject("created_at", OffsetDateTime.class)),
                  utc(rs.getObject("last_seen_at", OffsetDateTime.class)),
                  expires == null ? null : expires.toInstant().atZone(ZoneOffset.UTC),
                  currentHash != null && currentHash.equals(hash)));
        });
    return out;
  }

  // ── ending ──

  /**
   * Ends one device of {@code sub}: all the authorizations of its browser session, and the browser
   * session itself. Throws IllegalArgumentException if it is not this person's.
   */
  public void end(String sub, String authorizationId) {
    List<Map<String, Object>> rows =
        jdbc.queryForList(
            "SELECT web_session_hash, user_agent, ip FROM auth_session"
                + " WHERE authorization_id = :id AND sub = :sub",
            new MapSqlParameterSource("id", authorizationId).addValue("sub", sub));
    if (rows.isEmpty()) {
      throw new IllegalArgumentException("That session does not exist or is already over.");
    }
    String hash = (String) rows.get(0).get("web_session_hash");
    String device =
        UserAgents.describe((String) rows.get(0).get("user_agent")) + ", " + rows.get(0).get("ip");
    List<String> ids =
        hash == null
            ? List.of(authorizationId)
            : jdbc.queryForList(
                "SELECT authorization_id FROM auth_session WHERE sub = :sub AND web_session_hash = :h",
                new MapSqlParameterSource("sub", sub).addValue("h", hash),
                String.class);
    revoke(ids);
    webSessions.endByHash(hash);
    audit.record(AuditAction.SESSION_ENDED, sub, device);
  }

  /**
   * Ends every device of {@code sub} except the browser session {@code keepHash} (null keeps none):
   * on deactivation, deletion, a password change or reset, or "sign out everywhere".
   *
   * @param reason what to write in the audit log
   * @return how many authorizations and browser sessions were ended
   */
  public int endAll(String sub, String keepHash, String reason) {
    MapSqlParameterSource p = new MapSqlParameterSource("sub", sub).addValue("keep", keepHash);
    List<String> ids =
        jdbc.queryForList(
            "SELECT id FROM oauth2_authorization WHERE principal_name = :sub AND id NOT IN"
                + " (SELECT authorization_id FROM auth_session WHERE sub = :sub"
                + " AND web_session_hash = :keep)",
            p,
            String.class);
    revoke(ids);
    int web = webSessions.endAllOf(sub, keepHash);
    int total = ids.size() + web;
    if (total > 0) {
      audit.recordQuietly(AuditAction.SESSION_ENDED, sub, reason + " (" + ids.size() + " devices)");
    }
    return total;
  }

  /** The current browser session of {@code keepHash}'s owner is the one to spare, if any. */
  public String currentWebHash() {
    return webSessions.currentHash();
  }

  // ── logout ──

  /** Signing out on the account pages: ends what that browser session had signed in. */
  public void onWebLogout(HttpServletRequest request, String sub) {
    String hash = WebSessions.hashOf(request);
    String ip = request.getRemoteAddr();
    String owner = sub;
    if (hash != null) {
      List<String> owners =
          jdbc.queryForList(
              "SELECT DISTINCT sub FROM auth_session WHERE web_session_hash = :h",
              new MapSqlParameterSource("h", hash),
              String.class);
      if (owner == null && owners.size() == 1) owner = owners.get(0);
      revoke(
          jdbc.queryForList(
              "SELECT authorization_id FROM auth_session WHERE web_session_hash = :h",
              new MapSqlParameterSource("h", hash),
              String.class));
    }
    if (owner != null) {
      audit.recordQuietly(owner, AuditAction.LOGOUT, owner, "account pages", ip);
    }
  }

  /**
   * Signing out of the CMS (the OIDC logout, identified by the ID token the app presents): ends
   * that device. Returns who it was, or null if the token matches nothing.
   */
  public String onOidcLogout(String idTokenValue, String ip) {
    if (idTokenValue == null || idTokenValue.isBlank()) return null;
    List<Map<String, Object>> rows =
        jdbc.queryForList(
            "SELECT a.id, a.principal_name, s.web_session_hash FROM oauth2_authorization a"
                + " LEFT JOIN auth_session s ON s.authorization_id = a.id"
                + " WHERE a.oidc_id_token_value = :t",
            new MapSqlParameterSource("t", idTokenValue));
    if (rows.isEmpty()) return null;
    String id = (String) rows.get(0).get("id");
    String sub = (String) rows.get(0).get("principal_name");
    String hash = (String) rows.get(0).get("web_session_hash");
    List<String> ids = new ArrayList<>(List.of(id));
    if (hash != null) {
      ids.addAll(
          jdbc.queryForList(
              "SELECT authorization_id FROM auth_session WHERE sub = :sub AND web_session_hash = :h"
                  + " AND authorization_id <> :id",
              new MapSqlParameterSource("sub", sub).addValue("h", hash).addValue("id", id),
              String.class));
    }
    revoke(ids);
    audit.recordQuietly(sub, AuditAction.LOGOUT, sub, "CMS", ip);
    return sub;
  }

  // ── internals ──

  /**
   * Blacklists the access tokens, then deletes the authorizations (and, by cascade, the rows here).
   */
  private void revoke(List<String> ids) {
    if (ids.isEmpty()) return;
    MapSqlParameterSource p = new MapSqlParameterSource("ids", ids);
    jdbc.query(
        "SELECT access_token_value, access_token_expires_at FROM oauth2_authorization"
            + " WHERE id IN (:ids) AND access_token_value IS NOT NULL",
        p,
        rs -> {
          Timestamp expires = rs.getTimestamp("access_token_expires_at");
          String jti = jtiOf(rs.getString("access_token_value"));
          if (jti == null || expires == null) return;
          long remaining = expires.toInstant().toEpochMilli() - System.currentTimeMillis();
          if (remaining <= 0) return;
          try {
            redis.opsForValue().set(REVOKED_KEY_PREFIX + jti, "1", Duration.ofMillis(remaining));
          } catch (RuntimeException ex) {
            log.warn("Redis unavailable, access token not blacklisted: {}", ex.getMessage());
          }
        });
    jdbc.update("DELETE FROM oauth2_authorization WHERE id IN (:ids)", p);
  }

  private String jtiOf(String jwt) {
    try {
      String[] parts = jwt.split("\\.");
      if (parts.length < 2) return null;
      JsonNode node = objectMapper.readTree(Base64.getUrlDecoder().decode(parts[1]));
      JsonNode jti = node.get("jti");
      return jti == null ? null : jti.asText();
    } catch (Exception ex) {
      return null;
    }
  }

  private static ZonedDateTime utc(OffsetDateTime t) {
    return t == null ? null : t.atZoneSameInstant(ZoneOffset.UTC);
  }

  private static String cut(String s, int max) {
    if (s == null) return null;
    return s.length() <= max ? s : s.substring(0, max);
  }
}
