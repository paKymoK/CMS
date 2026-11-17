package com.takypok.authservice.config.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/**
 * Not a failure: the password was right and the second step is still to do. Carries the verified
 * principal to the failure handler, which parks it in the session (never in the security context)
 * until the code is checked.
 */
public class MfaRequiredException extends AuthenticationException {

  private final transient Authentication principal;
  private final String remoteAddress;

  public MfaRequiredException(Authentication principal, String remoteAddress) {
    super("A second factor is required");
    this.principal = principal;
    this.remoteAddress = remoteAddress;
  }

  public Authentication getPrincipal() {
    return principal;
  }

  public String getRemoteAddress() {
    return remoteAddress;
  }
}
