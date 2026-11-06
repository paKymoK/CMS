package com.takypok.authservice.service;

import com.takypok.authservice.model.entity.AuditAction;
import java.time.OffsetDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Profile pictures (BRD USR-07). The type is decided by the file's own first bytes, never by the
 * name or the type the browser claims, and only PNG, JPEG and WebP are accepted: no SVG, which can
 * carry script. {@code userinfo.avatar_url} holds the relative path so a future token claim or
 * screen can point at it.
 */
@Service
@RequiredArgsConstructor
public class AvatarService {

  public static final int MAX_BYTES = 512 * 1024;

  public record Avatar(String contentType, byte[] data, OffsetDateTime updatedAt) {}

  private final NamedParameterJdbcTemplate jdbc;
  private final AuditService auditService;

  /** Stores the picture for {@code sub}, replacing any earlier one. */
  @Transactional
  public void save(String sub, byte[] data) {
    if (data == null || data.length == 0) {
      throw new IllegalArgumentException("Choose an image file.");
    }
    if (data.length > MAX_BYTES) {
      throw new IllegalArgumentException("The picture must be 512 KB or smaller.");
    }
    String type = sniff(data);
    if (type == null) {
      throw new IllegalArgumentException("The picture must be a PNG, JPEG or WebP image.");
    }
    MapSqlParameterSource p =
        new MapSqlParameterSource("sub", sub).addValue("type", type).addValue("data", data);
    jdbc.update(
        "INSERT INTO user_avatar (sub, content_type, data) VALUES (:sub, :type, :data)"
            + " ON CONFLICT (sub) DO UPDATE SET content_type = EXCLUDED.content_type,"
            + " data = EXCLUDED.data, updated_at = now()",
        p);
    jdbc.update(
        "UPDATE userinfo SET avatar_url = :url WHERE sub = :sub",
        new MapSqlParameterSource("sub", sub).addValue("url", "/avatar/" + sub));
    auditService.record(AuditAction.PROFILE_UPDATED, sub, "fields: avatar");
  }

  @Transactional
  public void remove(String sub) {
    int n =
        jdbc.update(
            "DELETE FROM user_avatar WHERE sub = :sub", new MapSqlParameterSource("sub", sub));
    jdbc.update(
        "UPDATE userinfo SET avatar_url = NULL WHERE sub = :sub",
        new MapSqlParameterSource("sub", sub));
    if (n > 0) {
      auditService.record(AuditAction.PROFILE_UPDATED, sub, "fields: avatar (removed)");
    }
  }

  public Optional<Avatar> find(String sub) {
    return jdbc
        .query(
            "SELECT content_type, data, updated_at FROM user_avatar WHERE sub = :sub",
            new MapSqlParameterSource("sub", sub),
            (rs, n) ->
                new Avatar(
                    rs.getString("content_type"),
                    rs.getBytes("data"),
                    rs.getObject("updated_at", OffsetDateTime.class)))
        .stream()
        .findFirst();
  }

  /** The image type from the file signature, or null if it is not one we accept. */
  static String sniff(byte[] d) {
    if (d.length >= 8
        && (d[0] & 0xFF) == 0x89
        && d[1] == 'P'
        && d[2] == 'N'
        && d[3] == 'G'
        && d[4] == 0x0D
        && d[5] == 0x0A
        && d[6] == 0x1A
        && d[7] == 0x0A) {
      return "image/png";
    }
    if (d.length >= 3 && (d[0] & 0xFF) == 0xFF && (d[1] & 0xFF) == 0xD8 && (d[2] & 0xFF) == 0xFF) {
      return "image/jpeg";
    }
    if (d.length >= 12
        && d[0] == 'R'
        && d[1] == 'I'
        && d[2] == 'F'
        && d[3] == 'F'
        && d[8] == 'W'
        && d[9] == 'E'
        && d[10] == 'B'
        && d[11] == 'P') {
      return "image/webp";
    }
    return null;
  }
}
