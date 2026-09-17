package com.takypok.contentservice.config;

import com.takypok.core.exception.ApplicationException;
import com.takypok.core.model.Message;
import com.takypok.core.util.AuthenticationUtil;
import java.util.List;
import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * No @PreAuthorize precedent exists anywhere in this codebase — role checks are manual, e.g.
 * employee-service's/chat-service's per-controller requireAdmin(Authentication) helpers (empty Mono
 * if allowed, else an ApplicationException). This is that same pattern, centralized since every one
 * of the 11 admin controllers here needs the identical check. A global role (no project scope) on
 * the cms-admin client grants access to every site; a project-scoped role only grants access to
 * that one site — mirrors exactly how CustomOAuth2TokenCustomizer already buckets roles at token
 * issuance, no new claim shape needed.
 */
@Component
public class CmsAdminGuard {
  private static final String ADMIN_ROLE = "ADMIN";

  public Mono<Void> requireSiteAccess(Authentication authentication, String siteCode) {
    List<String> globalRoles = AuthenticationUtil.getRoles(authentication);
    if (globalRoles.contains(ADMIN_ROLE)) return Mono.empty();

    Map<String, List<String>> projectRoles = AuthenticationUtil.getProjectRoles(authentication);
    List<String> rolesForSite = projectRoles.get(siteCode);
    if (rolesForSite != null && rolesForSite.contains(ADMIN_ROLE)) return Mono.empty();

    return Mono.error(
        new ApplicationException(
            Message.Application.ERROR, "No admin access to site: " + siteCode));
  }
}
