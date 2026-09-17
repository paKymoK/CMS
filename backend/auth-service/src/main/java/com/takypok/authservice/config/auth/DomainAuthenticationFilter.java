package com.takypok.authservice.config.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class DomainAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

  public DomainAuthenticationFilter(AuthenticationManager authenticationManager) {
    super(authenticationManager);
  }

  @Override
  protected boolean requiresAuthentication(
      HttpServletRequest request, HttpServletResponse response) {

    if (!"POST".equalsIgnoreCase(request.getMethod())) {
      return false;
    }

    return "/login".equals(request.getServletPath());
  }

  @Override
  public Authentication attemptAuthentication(
      HttpServletRequest request, HttpServletResponse response) {

    String username = request.getParameter("username");
    String password = request.getParameter("password");
    String domain = request.getParameter("domain");

    if (domain == null || domain.isBlank()) {
      domain = "INTERNAL";
    }

    DomainAuthenticationToken token = new DomainAuthenticationToken(username, password, domain);

    return getAuthenticationManager().authenticate(token);
  }
}
