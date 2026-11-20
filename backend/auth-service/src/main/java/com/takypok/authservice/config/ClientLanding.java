package com.takypok.authservice.config;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.net.URI;
import java.time.Duration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Component;

/**
 * Where to send a user who signs in with no OAuth request in progress (straight on /login, or after
 * a logout), so they are not left on an auth-service page. It is the client they last came from,
 * remembered in a cookie that holds only a client id, or the default client when there is none.
 * The cookie can only choose among registered clients, so it can never point anywhere else.
 *
 * <p>The target is the origin of the client's first registered redirect URI: the redirect URI
 * itself is the client's callback, which fails without an authorization code.
 */
@Component
public class ClientLanding {

  static final String COOKIE = "last_client";
  private static final String DEFAULT_CLIENT_ID = AuthorizationServerConfig.CMS_ADMIN_CLIENT_ID;
  private static final Duration COOKIE_TTL = Duration.ofDays(30);

  private final RegisteredClientRepository clients;

  public ClientLanding(@Lazy RegisteredClientRepository clients) {
    this.clients = clients;
  }

  public String targetFor(HttpServletRequest request) {
    String target = originOf(cookieValue(request));
    if (target == null) {
      target = originOf(DEFAULT_CLIENT_ID);
    }
    return target != null ? target : "/";
  }

  public void remember(HttpServletRequest request, HttpServletResponse response, String clientId) {
    if (clients.findByClientId(clientId) == null) {
      return;
    }
    Cookie cookie = new Cookie(COOKIE, clientId);
    cookie.setPath("/");
    cookie.setHttpOnly(true);
    cookie.setSecure(request.isSecure());
    cookie.setMaxAge((int) COOKIE_TTL.toSeconds());
    cookie.setAttribute("SameSite", "Lax");
    response.addCookie(cookie);
  }

  private String cookieValue(HttpServletRequest request) {
    if (request.getCookies() != null) {
      for (Cookie cookie : request.getCookies()) {
        if (COOKIE.equals(cookie.getName())) {
          return cookie.getValue();
        }
      }
    }
    return null;
  }

  /** The origin of the client's first redirect URI, or null for an unknown client or none. */
  private String originOf(String clientId) {
    if (clientId == null) {
      return null;
    }
    RegisteredClient client = clients.findByClientId(clientId);
    if (client == null || client.getRedirectUris().isEmpty()) {
      return null;
    }
    try {
      URI uri = URI.create(client.getRedirectUris().iterator().next());
      return uri.getScheme() + "://" + uri.getAuthority();
    } catch (IllegalArgumentException e) {
      return null;
    }
  }
}
