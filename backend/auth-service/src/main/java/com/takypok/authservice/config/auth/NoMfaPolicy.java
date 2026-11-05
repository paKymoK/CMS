package com.takypok.authservice.config.auth;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * Default: no second factor for anyone. Replace this class when MFA is built (see {@link
 * MfaPolicy}).
 */
@Component
public class NoMfaPolicy implements MfaPolicy {

  @Override
  public boolean isSecondFactorRequired(Authentication primary) {
    return false;
  }
}
