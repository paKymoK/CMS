package com.takypok.authservice.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;

/**
 * Saves the request as usual and also notes which client an OAuth authorization request came from,
 * so a later sign-in with nothing to resume can return the user to that client.
 */
public class RememberingRequestCache extends HttpSessionRequestCache {

  private final ClientLanding clientLanding;

  public RememberingRequestCache(ClientLanding clientLanding) {
    this.clientLanding = clientLanding;
  }

  @Override
  public void saveRequest(HttpServletRequest request, HttpServletResponse response) {
    super.saveRequest(request, response);
    String clientId = request.getParameter("client_id");
    if (clientId != null && request.getRequestURI().endsWith("/oauth2/authorize")) {
      clientLanding.remember(request, response, clientId);
    }
  }
}
