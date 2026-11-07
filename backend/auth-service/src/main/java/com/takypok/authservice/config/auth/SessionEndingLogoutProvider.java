package com.takypok.authservice.config.auth;

import com.takypok.authservice.service.SessionService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.authorization.oidc.authentication.OidcLogoutAuthenticationToken;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Wraps the standard OIDC logout check. The standard logout only ends the browser session; it
 * leaves the device's refresh token valid for its full day. After the request is verified (so only
 * a valid ID token can trigger this), this ends the device too: refresh token deleted, access token
 * blacklisted, and the logout written to the audit log.
 */
public class SessionEndingLogoutProvider implements AuthenticationProvider {

  private final AuthenticationProvider delegate;
  private final SessionService sessions;

  public SessionEndingLogoutProvider(AuthenticationProvider delegate, SessionService sessions) {
    this.delegate = delegate;
    this.sessions = sessions;
  }

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    Authentication result = delegate.authenticate(authentication); // throws if not valid
    if (authentication instanceof OidcLogoutAuthenticationToken request) {
      String ip =
          RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attrs
              ? attrs.getRequest().getRemoteAddr()
              : null;
      sessions.onOidcLogout(request.getIdTokenHint(), ip);
    }
    return result;
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return delegate.supports(authentication);
  }
}
