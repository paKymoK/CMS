package com.takypok.authservice.config.auth;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class DomainAuthenticationToken extends UsernamePasswordAuthenticationToken {

  private final String domain;
  private final String remoteAddress;

  public DomainAuthenticationToken(
      String username, String password, String domain, String remoteAddress) {
    super(username, password);
    this.domain = domain;
    this.remoteAddress = remoteAddress;
  }

  public String getDomain() {
    return domain;
  }

  /** Server-observed IP the login attempt came from — never a client-supplied header. */
  public String getRemoteAddress() {
    return remoteAddress;
  }
}
