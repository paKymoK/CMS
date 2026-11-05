package com.takypok.authservice.service;

import com.takypok.authservice.model.entity.AuditAction;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * The account audit trail (BRD USR-03 / USR-06): changes to accounts, roles and groups, and sign-in
 * history. Rows are only ever added.
 *
 * <p>{@link #record} joins the caller's transaction, so an admin change and its audit row commit or
 * roll back together: no change goes unrecorded. {@link #recordQuietly} is for sign-in events,
 * where a failing audit write must never stop someone from signing in.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuditService {

  private static final int MAX_FIELD = 100;
  private static final int MAX_DETAIL = 500;
  private static final int MAX_PAGE_SIZE = 200;

  public record Entry(
      long id,
      ZonedDateTime at,
      String actor,
      String action,
      String actionLabel,
      String target,
      String detail,
      String ip) {}

  public record Result(List<Entry> rows, long total) {}

  private static final RowMapper<Entry> ROW_MAPPER =
      (rs, n) -> {
        String action = rs.getString("action");
        OffsetDateTime at = rs.getObject("at", OffsetDateTime.class);
        return new Entry(
            rs.getLong("id"),
            at == null ? null : at.atZoneSameInstant(ZoneOffset.UTC),
            rs.getString("actor"),
            action,
            labelOf(action),
            rs.getString("target"),
            rs.getString("detail"),
            rs.getString("ip"));
      };

  private final NamedParameterJdbcTemplate jdbc;

  /** Records an action by whoever is signed in on this request (a Super Admin, or "system"). */
  public void record(AuditAction action, String target, String detail) {
    insert(currentActor(), action, target, detail, currentIp());
  }

  /** Records an action whose actor is known explicitly, e.g. a sign-in attempt. */
  public void recordAs(String actor, AuditAction action, String target, String detail, String ip) {
    insert(actor, action, target, detail, ip);
  }

  public void recordQuietly(
      String actor, AuditAction action, String target, String detail, String ip) {
    try {
      insert(actor, action, target, detail, ip);
    } catch (RuntimeException ex) {
      log.error("Audit write failed ({} for {}): {}", action, target, ex.getMessage());
    }
  }

  /**
   * @param q matches actor or target, case-insensitively, anywhere in the text
   * @param action an {@link AuditAction} name, or null for all
   * @param from first day to include (UTC), or null
   * @param to last day to include (UTC), or null
   */
  public Result search(String q, String action, LocalDate from, LocalDate to, int page, int size) {
    int safeSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
    StringBuilder where = new StringBuilder(" WHERE 1 = 1");
    MapSqlParameterSource p = new MapSqlParameterSource();
    if (q != null && !q.isBlank()) {
      where.append(" AND (actor ILIKE :like OR target ILIKE :like)");
      p.addValue("like", "%" + escapeLike(q.trim()) + "%");
    }
    if (action != null && !action.isBlank()) {
      where.append(" AND action = :action");
      p.addValue("action", AuditAction.valueOf(action.trim().toUpperCase()).name());
    }
    if (from != null) {
      where.append(" AND at >= :from");
      p.addValue("from", from.atStartOfDay().atOffset(ZoneOffset.UTC));
    }
    if (to != null) {
      where.append(" AND at < :to");
      p.addValue("to", to.plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC));
    }
    Long total = jdbc.queryForObject("SELECT COUNT(*) FROM account_audit" + where, p, Long.class);
    p.addValue("limit", safeSize).addValue("offset", (long) Math.max(page, 0) * safeSize);
    List<Entry> rows =
        jdbc.query(
            "SELECT id, at, actor, action, target, detail, ip FROM account_audit"
                + where
                + " ORDER BY at DESC, id DESC LIMIT :limit OFFSET :offset",
            p,
            ROW_MAPPER);
    return new Result(rows, total == null ? 0 : total);
  }

  private void insert(String actor, AuditAction action, String target, String detail, String ip) {
    jdbc.update(
        "INSERT INTO account_audit (actor, action, target, detail, ip)"
            + " VALUES (:actor, :action, :target, :detail, :ip)",
        new MapSqlParameterSource()
            .addValue("actor", cut(actor, MAX_FIELD))
            .addValue("action", action.name())
            .addValue("target", cut(target, MAX_FIELD))
            .addValue("detail", cut(detail, MAX_DETAIL))
            .addValue("ip", cut(ip, 64)));
  }

  private static String currentActor() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || auth instanceof AnonymousAuthenticationToken || !auth.isAuthenticated()) {
      return "system";
    }
    return auth.getName();
  }

  private static String currentIp() {
    if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attrs) {
      HttpServletRequest request = attrs.getRequest();
      return request.getRemoteAddr();
    }
    return null;
  }

  private static String labelOf(String action) {
    try {
      return AuditAction.valueOf(action).label();
    } catch (IllegalArgumentException ex) {
      return action;
    }
  }

  private static String cut(String s, int max) {
    if (s == null) return null;
    return s.length() <= max ? s : s.substring(0, max);
  }

  private static String escapeLike(String s) {
    return s.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
  }
}
