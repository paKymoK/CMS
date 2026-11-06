package com.takypok.authservice.config;

import com.takypok.authservice.model.entity.ClientRoleAssignment;
import com.takypok.authservice.model.entity.ClientSessionPolicy;
import com.takypok.authservice.model.entity.Userinfo;
import com.takypok.authservice.repository.ClientRoleAssignmentRepository;
import com.takypok.authservice.repository.ClientSessionPolicyRepository;
import com.takypok.authservice.repository.UserinfoRepository;
import com.takypok.core.Constants;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomOAuth2TokenCustomizer implements OAuth2TokenCustomizer<JwtEncodingContext> {

  private final ClientRoleAssignmentRepository clientRoleAssignmentRepository;
  private final ClientSessionPolicyRepository clientSessionPolicyRepository;
  private final UserinfoRepository userinfoRepository;

  /** The sign-in name always, and the name to show when there is one. */
  static Map<String, Object> infoClaim(String sub, Userinfo userinfo) {
    Map<String, Object> info = new LinkedHashMap<>();
    info.put("sub", sub);
    String name = shownName(userinfo);
    if (name != null) {
      info.put("name", name);
    }
    return info;
  }

  /**
   * The display name if the person set one, otherwise their full name, otherwise null (then only
   * the sign-in name identifies them). The display name can change; the sub never does.
   */
  static String shownName(Userinfo userinfo) {
    if (userinfo == null) return null;
    if (userinfo.getDisplayName() != null && !userinfo.getDisplayName().isBlank()) {
      return userinfo.getDisplayName().trim();
    }
    if (userinfo.getName() != null && !userinfo.getName().isBlank()) {
      return userinfo.getName().trim();
    }
    return null;
  }

  @Override
  public void customize(JwtEncodingContext context) {
    if (!OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())) return;
    Authentication principal = context.getPrincipal();
    if (principal == null) return;
    String subject = principal.getName();

    if (principal instanceof AbstractAuthenticationToken aat
        && aat.getDetails() instanceof String domain) {
      context.getClaims().claim("domain", domain);
    }

    Userinfo userinfo = userinfoRepository.getBySub(subject);
    if (userinfo != null) {
      if (userinfo.getName() != null) {
        context.getClaims().claim("name", userinfo.getName());
      }
      if (userinfo.getEmail() != null) {
        context.getClaims().claim("email", userinfo.getEmail());
      }
    }

    // The "info" claim is what resource services stamp on what a person changes (core-v1 reads it
    // as
    // User): the sign-in name, and the name to show. Deliberately nothing else personal.
    context.getClaims().claim("info", infoClaim(subject, userinfo));

    // Resolve all role assignments for this user on the requesting client (direct + group).
    // Split them into global roles (no project) and per-project roles so the resource service
    // can enforce project-scoped access without a round-trip.
    String clientId = context.getRegisteredClient().getId();
    List<ClientRoleAssignment> assignments =
        clientRoleAssignmentRepository.findAssignmentsForUserOnClient(clientId, subject);

    Set<String> globalRoles = new LinkedHashSet<>();
    Map<String, Set<String>> projectRoles = new LinkedHashMap<>();
    for (ClientRoleAssignment a : assignments) {
      if (a.getProjectId() == null) {
        globalRoles.add(a.getRole());
      } else {
        projectRoles.computeIfAbsent(a.getProjectId(), k -> new LinkedHashSet<>()).add(a.getRole());
      }
    }
    if (!globalRoles.isEmpty()) {
      context.getClaims().claim(Constants.ROLES_CLAIM, new ArrayList<>(globalRoles));
    }
    if (!projectRoles.isEmpty()) {
      Map<String, List<String>> claim = new LinkedHashMap<>();
      projectRoles.forEach((k, v) -> claim.put(k, new ArrayList<>(v)));
      context.getClaims().claim(Constants.PROJECT_ROLES_CLAIM, claim);
    }

    // Embed per-client session policy as an opaque claim — only present for single-tab clients
    clientSessionPolicyRepository
        .findById(clientId)
        .filter(ClientSessionPolicy::isSingleTab)
        .ifPresent(
            p -> {
              String encoded =
                  p.isFailOpen() ? Constants.SESSION_POLICY_OPEN : Constants.SESSION_POLICY_CLOSED;
              context.getClaims().claim(Constants.SESSION_POLICY_CLAIM, encoded);
            });
  }
}
