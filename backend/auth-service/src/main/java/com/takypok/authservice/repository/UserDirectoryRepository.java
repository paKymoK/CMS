package com.takypok.authservice.repository;

import com.takypok.authservice.service.SuperAdminService;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * Read side for the user list: joins Spring Security's {@code users} table (which has no entity)
 * with {@code userinfo} and the cms-admin role assignments.
 */
@Repository
@RequiredArgsConstructor
public class UserDirectoryRepository {

  public record Row(
      String sub,
      String name,
      String email,
      Long departmentId,
      Long unitId,
      String status,
      String accountType,
      String displayName,
      LocalDate dateOfBirth,
      ZonedDateTime lastLoginAt,
      ZonedDateTime createdAt) {}

  public record RoleRow(String sub, String projectId, String role) {}

  public record Result(List<Row> rows, long total) {}

  private static final String SELECT =
      "SELECT u.username AS sub, i.name, i.email, i.department_id, i.unit_id,"
          + " COALESCE(i.status, 'ACTIVE') AS status, i.account_type,"
          + " i.display_name, i.date_of_birth, i.last_login_at, i.created_at";
  private static final String FROM = " FROM users u LEFT JOIN userinfo i ON i.sub = u.username";

  private static final RowMapper<Row> ROW_MAPPER =
      (rs, n) ->
          new Row(
              rs.getString("sub"),
              rs.getString("name"),
              rs.getString("email"),
              rs.getObject("department_id", Long.class),
              rs.getObject("unit_id", Long.class),
              rs.getString("status"),
              rs.getString("account_type"),
              rs.getString("display_name"),
              rs.getObject("date_of_birth", LocalDate.class),
              utc(rs.getObject("last_login_at", OffsetDateTime.class)),
              utc(rs.getObject("created_at", OffsetDateTime.class)));

  /** The same instant, shown in UTC whatever zone the server or database session runs in. */
  private static ZonedDateTime utc(OffsetDateTime t) {
    return t == null ? null : t.atZoneSameInstant(ZoneOffset.UTC);
  }

  private final NamedParameterJdbcTemplate jdbc;

  /**
   * @param q matches username, name or email, case-insensitively, anywhere in the text
   * @param status ACTIVE or INACTIVE, or null for both
   * @param role a cms-admin role (e.g. ADMIN), or null
   * @param projectId a site code: keeps users who can reach that site, a global role included
   */
  public Result search(String q, String status, String role, String projectId, int page, int size) {
    StringBuilder where = new StringBuilder(" WHERE 1 = 1");
    MapSqlParameterSource p = new MapSqlParameterSource("clientId", SuperAdminService.CLIENT_ID);

    if (q != null && !q.isBlank()) {
      where.append(" AND (u.username ILIKE :like OR i.name ILIKE :like OR i.email ILIKE :like)");
      p.addValue("like", "%" + escapeLike(q.trim()) + "%");
    }
    if (status != null) {
      where.append(" AND COALESCE(i.status, 'ACTIVE') = :status");
      p.addValue("status", status);
    }
    if (role != null || projectId != null) {
      where.append(
          " AND EXISTS (SELECT 1 FROM client_role_assignment a"
              + " JOIN oauth2_registered_client c ON c.id = a.registered_client_id"
              + " WHERE c.client_id = :clientId");
      if (role != null) {
        where.append(" AND a.role = :role");
        p.addValue("role", role);
      }
      if (projectId != null) {
        where.append(" AND (a.project_id = :projectId OR a.project_id IS NULL)");
        p.addValue("projectId", projectId);
      }
      where.append(
          " AND (a.user_sub = u.username OR a.group_id IN"
              + " (SELECT m.group_id FROM user_group_member m WHERE m.user_sub = u.username)))");
    }

    Long total = jdbc.queryForObject("SELECT COUNT(*)" + FROM + where, p, Long.class);
    p.addValue("limit", size).addValue("offset", (long) page * size);
    List<Row> rows =
        jdbc.query(
            SELECT
                + FROM
                + where
                + " ORDER BY LOWER(COALESCE(i.name, u.username)), u.username"
                + " LIMIT :limit OFFSET :offset",
            p,
            ROW_MAPPER);
    return new Result(rows, total == null ? 0 : total);
  }

  public Optional<Row> find(String sub) {
    return jdbc
        .query(
            SELECT + FROM + " WHERE u.username = :sub",
            new MapSqlParameterSource("sub", sub),
            ROW_MAPPER)
        .stream()
        .findFirst();
  }

  /** cms-admin roles per user, direct and through groups, in one query. */
  public Map<String, List<RoleRow>> rolesFor(Collection<String> subs) {
    Map<String, List<RoleRow>> bySub = new HashMap<>();
    if (subs.isEmpty()) return bySub;
    jdbc.query(
        "SELECT u.username AS sub, a.project_id, a.role FROM users u"
            + " JOIN client_role_assignment a ON (a.user_sub = u.username OR a.group_id IN"
            + " (SELECT m.group_id FROM user_group_member m WHERE m.user_sub = u.username))"
            + " JOIN oauth2_registered_client c ON c.id = a.registered_client_id"
            + " WHERE c.client_id = :clientId AND u.username IN (:subs)"
            + " ORDER BY a.project_id NULLS FIRST, a.role",
        new MapSqlParameterSource("clientId", SuperAdminService.CLIENT_ID).addValue("subs", subs),
        rs -> {
          String sub = rs.getString("sub");
          bySub
              .computeIfAbsent(sub, k -> new java.util.ArrayList<>())
              .add(new RoleRow(sub, rs.getString("project_id"), rs.getString("role")));
        });
    return bySub;
  }

  /** Drops the user's refresh tokens so a deactivated account cannot renew its access token. */
  public void revokeAuthorizations(String sub) {
    jdbc.update(
        "DELETE FROM oauth2_authorization WHERE principal_name = :sub",
        new MapSqlParameterSource("sub", sub));
  }

  /** Stores an already-encoded password for a user. */
  public void setPassword(String sub, String encodedPassword) {
    jdbc.update(
        "UPDATE users SET password = :password WHERE username = :sub",
        new MapSqlParameterSource("sub", sub).addValue("password", encodedPassword));
  }

  /** True when a deleted account once held this sub (compared case-insensitively). */
  public boolean isTombstoned(String sub) {
    Integer n =
        jdbc.queryForObject(
            "SELECT COUNT(*) FROM deleted_user WHERE LOWER(sub) = LOWER(:sub)",
            new MapSqlParameterSource("sub", sub),
            Integer.class);
    return n != null && n > 0;
  }

  /**
   * Erases the account and reserves its sub in {@code deleted_user}. users cascades to userinfo,
   * group membership and role assignments; the rest has no foreign key to cascade through.
   */
  public void purge(String sub, String actor) {
    MapSqlParameterSource p = new MapSqlParameterSource("sub", sub).addValue("actor", actor);
    jdbc.update("UPDATE userinfo SET manager_sub = NULL WHERE manager_sub = :sub", p);
    jdbc.update("DELETE FROM oauth2_authorization WHERE principal_name = :sub", p);
    jdbc.update("DELETE FROM oauth2_authorization_consent WHERE principal_name = :sub", p);
    jdbc.update("DELETE FROM authorities WHERE username = :sub", p);
    jdbc.update("DELETE FROM users WHERE username = :sub", p);
    jdbc.update(
        "INSERT INTO deleted_user (sub, deleted_by) VALUES (:sub, :actor)"
            + " ON CONFLICT (sub) DO NOTHING",
        p);
  }

  private static String escapeLike(String s) {
    return s.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
  }
}
