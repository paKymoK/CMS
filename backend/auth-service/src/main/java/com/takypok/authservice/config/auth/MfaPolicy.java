package com.takypok.authservice.config.auth;

import org.springframework.security.core.Authentication;

/**
 * Decides whether a sign-in needs a second factor. {@link DomainAuthenticationManager} asks once,
 * right after a correct password. When the answer is true it throws {@link MfaRequiredException},
 * the browser is sent to the code page (see {@code MfaChallengeController}), and the sign-in only
 * completes when the code passes. {@link TotpMfaPolicy} says yes for people who turned on
 * authenticator-app verification.
 */
public interface MfaPolicy {

  /**
   * @param primary the principal whose password was just verified
   */
  boolean isSecondFactorRequired(Authentication primary);
}
