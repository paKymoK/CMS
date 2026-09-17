package com.takypok.authservice.model.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisteredClientRequest {

  @NotBlank private String clientId;

  private String clientName;

  /** Null means keep the existing secret (on update) or no secret (on create). */
  private String clientSecret;

  @NotEmpty private List<String> authenticationMethods;

  @NotEmpty private List<String> grantTypes;

  private List<String> redirectUris = List.of();

  private List<String> postLogoutRedirectUris = List.of();

  @NotEmpty private List<String> scopes;

  private boolean requireAuthorizationConsent;

  private boolean requireProofKey;

  @Min(1)
  private long accessTokenTtlMinutes = 10;

  @Min(1)
  private long refreshTokenTtlDays = 1;

  private boolean reuseRefreshTokens;

  private boolean singleTabSession;

  private boolean failOpen = true;
}
