package com.takypok.authservice.model.response;

import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;

@Getter
@Builder
public class RegisteredClientResponse {

  private String id;
  private String clientId;
  private String clientName;
  private String clientIdIssuedAt;
  private boolean hasSecret;
  private List<String> authenticationMethods;
  private List<String> grantTypes;
  private List<String> redirectUris;
  private List<String> postLogoutRedirectUris;
  private List<String> scopes;
  private boolean requireAuthorizationConsent;
  private boolean requireProofKey;
  private long accessTokenTtlMinutes;
  private long refreshTokenTtlDays;
  private boolean reuseRefreshTokens;
  private boolean singleTabSession;
  private boolean failOpen;

  public static RegisteredClientResponse from(
      RegisteredClient c, boolean singleTabSession, boolean failOpen) {
    return RegisteredClientResponse.builder()
        .id(c.getId())
        .clientId(c.getClientId())
        .clientName(c.getClientName())
        .clientIdIssuedAt(
            c.getClientIdIssuedAt() != null ? c.getClientIdIssuedAt().toString() : null)
        .hasSecret(c.getClientSecret() != null)
        .authenticationMethods(
            c.getClientAuthenticationMethods().stream()
                .map(ClientAuthenticationMethod::getValue)
                .collect(Collectors.toList()))
        .grantTypes(
            c.getAuthorizationGrantTypes().stream()
                .map(AuthorizationGrantType::getValue)
                .collect(Collectors.toList()))
        .redirectUris(List.copyOf(c.getRedirectUris()))
        .postLogoutRedirectUris(List.copyOf(c.getPostLogoutRedirectUris()))
        .scopes(List.copyOf(c.getScopes()))
        .requireAuthorizationConsent(c.getClientSettings().isRequireAuthorizationConsent())
        .requireProofKey(c.getClientSettings().isRequireProofKey())
        .accessTokenTtlMinutes(c.getTokenSettings().getAccessTokenTimeToLive().toMinutes())
        .refreshTokenTtlDays(c.getTokenSettings().getRefreshTokenTimeToLive().toDays())
        .reuseRefreshTokens(c.getTokenSettings().isReuseRefreshTokens())
        .singleTabSession(singleTabSession)
        .failOpen(failOpen)
        .build();
  }
}
