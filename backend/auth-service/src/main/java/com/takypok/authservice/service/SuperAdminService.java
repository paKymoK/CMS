package com.takypok.authservice.service;

import com.takypok.core.exception.ApplicationException;
import com.takypok.core.model.Message;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

/**
 * "Super Admin" is the global (not project-scoped) {@value #ROLE} role on the {@value #CLIENT_ID}
 * client, held directly or through a group, by an account that is not INACTIVE. A role limited to
 * one site does not count. Decided from the database on every call, never from a token claim, so a
 * deactivated or demoted account loses access at once rather than when its token expires.
 */
@Service
@RequiredArgsConstructor
public class SuperAdminService {

  public static final String CLIENT_ID = "cms-admin";
  public static final String ROLE = "ADMIN";

  private static final String ACTIVE_SUPER_ADMINS =
      """
      SELECT u.username FROM users u
      LEFT JOIN userinfo i ON i.sub = u.username
      WHERE COALESCE(i.status, 'ACTIVE') = 'ACTIVE'
        AND EXISTS (
          SELECT 1 FROM client_role_assignment a
          JOIN oauth2_registered_client c ON c.id = a.registered_client_id
          WHERE c.client_id = :clientId AND a.role = :role AND a.project_id IS NULL
            AND (a.user_sub = u.username
                OR a.group_id IN (SELECT m.group_id FROM user_group_member m
                                  WHERE m.user_sub = u.username)))
      """;

  private final NamedParameterJdbcTemplate jdbc;

  @PersistenceContext private EntityManager entityManager;

  /** For a request's principal: signed in (not anonymous) and an active Super Admin. */
  public boolean isActiveSuperAdmin(Authentication auth) {
    return auth != null
        && auth.isAuthenticated()
        && !(auth instanceof AnonymousAuthenticationToken)
        && isActiveSuperAdmin(auth.getName());
  }

  public boolean isActiveSuperAdmin(String sub) {
    if (sub == null || sub.isBlank()) return false;
    Boolean found =
        jdbc.queryForObject(
            "SELECT EXISTS (" + ACTIVE_SUPER_ADMINS + " AND u.username = :sub)",
            params().addValue("sub", sub),
            Boolean.class);
    return Boolean.TRUE.equals(found);
  }

  public long countActiveSuperAdmins() {
    Long count =
        jdbc.queryForObject(
            "SELECT COUNT(*) FROM (" + ACTIVE_SUPER_ADMINS + ") s", params(), Long.class);
    return count == null ? 0 : count;
  }

  /**
   * Runs {@code change} and refuses it (the caller's transaction rolls back) if it takes away the
   * last active Super Admin. A system that has none to begin with is left alone, so unrelated
   * changes are never blocked. Call inside a transaction.
   */
  public void guardLastSuperAdmin(Runnable change) {
    long before = countActiveSuperAdmins();
    change.run();
    entityManager.flush(); // the count below runs on JDBC and must see the change
    if (before > 0 && countActiveSuperAdmins() == 0) {
      throw new ApplicationException(
          Message.Application.ERROR, "At least one active Super Admin must remain");
    }
  }

  private static MapSqlParameterSource params() {
    return new MapSqlParameterSource("clientId", CLIENT_ID).addValue("role", ROLE);
  }
}
