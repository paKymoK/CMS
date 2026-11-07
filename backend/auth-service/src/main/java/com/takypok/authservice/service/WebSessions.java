package com.takypok.authservice.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionIdListener;
import jakarta.servlet.http.HttpSessionListener;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * The browser sessions of this service (the cookie behind /login, /profile and the single sign-on
 * that silently re-issues tokens). Ending a device must end its session too: deleting only the
 * refresh token would leave the browser able to sign back in without a password.
 *
 * <p>The container keeps sessions in memory, so this does too: with several instances, or after a
 * restart, only this instance's current sessions are known (a restart ends them all anyway).
 * Sessions are referred to by a hash of their id, never by the id itself, so the id (which is the
 * cookie) never leaves the server or lands in the database.
 */
@Slf4j
@Component
public class WebSessions implements HttpSessionListener, HttpSessionIdListener {

  private final Map<String, HttpSession> sessions = new ConcurrentHashMap<>();

  @Override
  public void sessionCreated(HttpSessionEvent event) {
    sessions.put(event.getSession().getId(), event.getSession());
  }

  @Override
  public void sessionDestroyed(HttpSessionEvent event) {
    sessions.remove(event.getSession().getId());
  }

  @Override
  public void sessionIdChanged(HttpSessionEvent event, String oldSessionId) {
    sessions.remove(oldSessionId);
    sessions.put(event.getSession().getId(), event.getSession());
  }

  /** The hash of the signed-in browser session of the current request, or null if there is none. */
  public String currentHash() {
    if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attrs) {
      return hashOf(attrs.getRequest());
    }
    return null;
  }

  public static String hashOf(HttpServletRequest request) {
    HttpSession session = request.getSession(false);
    return session == null ? null : hash(session.getId());
  }

  public static String hash(String sessionId) {
    try {
      byte[] d =
          MessageDigest.getInstance("SHA-256").digest(sessionId.getBytes(StandardCharsets.UTF_8));
      return Base64.getUrlEncoder().withoutPadding().encodeToString(d);
    } catch (NoSuchAlgorithmException ex) {
      throw new IllegalStateException(ex);
    }
  }

  /** Ends the browser session with this hash. Returns how many were ended (0 or 1). */
  public int endByHash(String hash) {
    if (hash == null) return 0;
    int ended = 0;
    for (HttpSession session : new ArrayList<>(sessions.values())) {
      if (hash.equals(hash(session.getId())) && invalidate(session)) ended++;
    }
    return ended;
  }

  /**
   * Ends every browser session signed in as {@code sub}, except the one with {@code keepHash} (null
   * keeps none). Covers sessions that never got a token too (someone on the account pages only).
   */
  public int endAllOf(String sub, String keepHash) {
    int ended = 0;
    for (HttpSession session : new ArrayList<>(sessions.values())) {
      String owner = ownerOf(session);
      if (!sub.equals(owner)) continue;
      if (keepHash != null && keepHash.equals(hash(session.getId()))) continue;
      if (invalidate(session)) ended++;
    }
    return ended;
  }

  private static String ownerOf(HttpSession session) {
    try {
      Object context =
          session.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
      if (context instanceof SecurityContext sc) {
        Authentication auth = sc.getAuthentication();
        return auth == null ? null : auth.getName();
      }
    } catch (IllegalStateException gone) {
      // already invalidated
    }
    return null;
  }

  private static boolean invalidate(HttpSession session) {
    try {
      session.invalidate();
      return true;
    } catch (IllegalStateException alreadyGone) {
      return false;
    }
  }
}
