package com.takypok.authservice.config.auth;

import com.takypok.authservice.service.MfaService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * A second factor is asked for exactly when the person turned two-step verification on. It is
 * optional for everyone, administrators included. To require it for some people (a role, say), add
 * that rule here.
 */
@Component
@RequiredArgsConstructor
public class TotpMfaPolicy implements MfaPolicy {

  private final MfaService mfaService;

  @Override
  public boolean isSecondFactorRequired(Authentication primary) {
    return mfaService.isEnabled(primary.getName());
  }
}
