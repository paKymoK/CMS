package com.takypok.authservice.config.auth;

import jakarta.servlet.http.HttpSession;
import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import org.springframework.security.core.Authentication;

/**
 * A sign-in whose password was right but whose code is still to come. Lives only in the browser
 * session, for a few minutes. It is NOT an authenticated context: until the code passes nobody is
 * signed in.
 */
public record PendingMfa(Authentication principal, String remoteAddress, Instant expires)
    implements Serializable {

  static final String ATTRIBUTE = "pendingMfa";
  static final Duration TTL = Duration.ofMinutes(5);

  public static void store(HttpSession session, MfaRequiredException ex) {
    session.setAttribute(
        ATTRIBUTE,
        new PendingMfa(ex.getPrincipal(), ex.getRemoteAddress(), Instant.now().plus(TTL)));
  }

  /** The pending sign-in of this session, or null when there is none or it ran out. */
  public static PendingMfa current(HttpSession session) {
    if (session == null) {
      return null;
    }
    if (session.getAttribute(ATTRIBUTE) instanceof PendingMfa pending) {
      if (Instant.now().isBefore(pending.expires())) {
        return pending;
      }
      session.removeAttribute(ATTRIBUTE);
    }
    return null;
  }

  public static void clear(HttpSession session) {
    if (session != null) {
      session.removeAttribute(ATTRIBUTE);
    }
  }
}
