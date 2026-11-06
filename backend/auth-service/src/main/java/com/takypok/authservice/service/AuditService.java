package com.takypok.authservice.service;

import com.takypok.authservice.model.entity.AuditAction;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.Consumer;
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
  private static final int EXPORT_BATCH = 1000;

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

  /** The screen's filters. Any field may be null. */
  public record Filter(String q, String action, LocalDate from, LocalDate to) {}

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

  /** Like {@link #record} but never throws: for a side effect that must not undo the main work. */
  public void recordQuietly(AuditAction action, String target, String detail) {
    recordQuietly(currentActor(), action, target, detail, currentIp());
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
    MapSqlParameterSource p = new MapSqlParameterSource();
    String where = where(new Filter(q, action, from, to), p);
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

  /**
   * Hands every matching entry to {@code sink}, newest first, at most {@code max}. Reads in short
   * keyset batches (no OFFSET, no cursor), so no connection stays open while a slow consumer
   * writes, and entries added meanwhile cannot shift the pages.
   *
   * @return how many entries were handed over
   */
  public int stream(Filter filter, int max, Consumer<Entry> sink) {
    int sent = 0;
    OffsetDateTime lastAt = null;
    long lastId = 0;
    while (sent < max) {
      MapSqlParameterSource p = new MapSqlParameterSource();
      StringBuilder where = new StringBuilder(where(filter, p));
      if (lastAt != null) {
        where.append(" AND (at, id) < (:lastAt, :lastId)");
        p.addValue("lastAt", lastAt).addValue("lastId", lastId);
      }
      p.addValue("limit", Math.min(EXPORT_BATCH, max - sent));
      List<Entry> batch =
          jdbc.query(
              "SELECT id, at, actor, action, target, detail, ip FROM account_audit"
                  + where
                  + " ORDER BY at DESC, id DESC LIMIT :limit",
              p,
              ROW_MAPPER);
      if (batch.isEmpty()) break;
      batch.forEach(sink);
      sent += batch.size();
      Entry last = batch.get(batch.size() - 1);
      lastAt = last.at().toOffsetDateTime();
      lastId = last.id();
      if (batch.size() < EXPORT_BATCH) break;
    }
    return sent;
  }

  /** Throws IllegalArgumentException for an unknown action, before anything is written. */
  public void validate(Filter f) {
    where(f, new MapSqlParameterSource());
  }

  /** Builds the WHERE clause (starting with " WHERE 1 = 1") and fills its parameters. */
  private static String where(Filter f, MapSqlParameterSource p) {
    StringBuilder where = new StringBuilder(" WHERE 1 = 1");
    if (f.q() != null && !f.q().isBlank()) {
      where.append(" AND (actor ILIKE :like OR target ILIKE :like)");
      p.addValue("like", "%" + escapeLike(f.q().trim()) + "%");
    }
    if (f.action() != null && !f.action().isBlank()) {
      where.append(" AND action = :action");
      p.addValue("action", AuditAction.valueOf(f.action().trim().toUpperCase()).name());
    }
    if (f.from() != null) {
      where.append(" AND at >= :from");
      p.addValue("from", f.from().atStartOfDay().atOffset(ZoneOffset.UTC));
    }
    if (f.to() != null) {
      where.append(" AND at < :to");
      p.addValue("to", f.to().plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC));
    }
    return where.toString();
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
