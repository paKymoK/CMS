package com.takypok.authservice.config.auth;

import org.springframework.security.core.Authentication;

/**
 * The seam for multi-factor authentication. The BRD (USR-05) asks for MFA but does not say which
 * factor (authenticator app, security key, e-mail code), so none is built yet and {@link
 * NoMfaPolicy} never asks for one.
 *
 * <p>Email is unique per account (decision: a recovery address and a possible MFA factor), so an
 * e-mail code can safely name exactly one person if it is chosen.
 *
 * <p>{@link DomainAuthenticationManager} calls this once, right after a correct password and before
 * the login completes. To add MFA: decide the factor, return true here for the users who need it,
 * and replace the "not implemented" refusal in the manager with a pending state (keep the
 * password-verified principal in the session, show a challenge page, finish the login only when the
 * challenge passes). Until that step exists, returning true makes every login for that user fail,
 * never pass: the refusal is deliberate.
 */
public interface MfaPolicy {

  /**
   * @param primary the principal whose password was just verified
   */
  boolean isSecondFactorRequired(Authentication primary);
}
