package com.takypok.chatservice.config;

import com.takypok.core.exception.ApplicationException;
import com.takypok.core.model.Message;
import com.takypok.core.util.AuthenticationUtil;
import java.util.List;
import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Same shape as media-service's and content-service's CmsAdminGuard — chat-service isn't the site
 * registry either, so this checks the caller's JWT claims against the caller-supplied site code
 * directly, with no Site-entity lookup.
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
