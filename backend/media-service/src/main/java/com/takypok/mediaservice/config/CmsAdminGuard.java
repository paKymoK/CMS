package com.takypok.mediaservice.config;

import com.takypok.core.exception.ApplicationException;
import com.takypok.core.model.Message;
import com.takypok.core.util.AuthenticationUtil;
import java.util.List;
import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Same check as content-service's CmsAdminGuard, minus a Site-entity lookup — media-service has no
 * site registry of its own (it's not the source of truth for which site codes are real;
 * auth-service already only ever puts a real site code into a user's project_roles claim), so this
 * just checks the caller's JWT claims against the caller-supplied site code directly.
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
