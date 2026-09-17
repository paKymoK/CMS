package com.takypok.authservice.config.auth;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class DomainAuthenticationToken extends UsernamePasswordAuthenticationToken {

  private final String domain;

  public DomainAuthenticationToken(String username, String password, String domain) {
    super(username, password);
    this.domain = domain;
  }

  public String getDomain() {
    return domain;
  }
}
