package com.takypok.authservice.config;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.util.Assert;

/**
 * Pairs with PublicClientRefreshTokenAuthenticationConverter. The built-in
 * PublicClientAuthenticationProvider always demands PKCE's code_verifier for
 * ClientAuthenticationMethod.NONE (see CodeVerifierAuthenticator.authenticateRequired, which throws
 * invalid_grant for any non-authorization_code request), so it can never authenticate a
 * refresh_token grant even for a client that supports NONE. ProviderManager swallows that failure
 * and tries the next provider, which is this one: it only asserts the registered client actually
 * supports NONE. Possession of a valid, unexpired, single-use refresh token
 * (TokenSettings.reuseRefreshTokens(false) — see AuthorizationServerConfig) is what proves the
 * caller is the one the token was issued to, the same trust model PKCE gives the authorization_code
 * exchange.
 */
public final class PublicClientRefreshTokenAuthenticationProvider
    implements AuthenticationProvider {

  private static final String ERROR_URI =
      "https://datatracker.ietf.org/doc/html/rfc6749#section-3.2.1";

  private final RegisteredClientRepository registeredClientRepository;

  public PublicClientRefreshTokenAuthenticationProvider(
      RegisteredClientRepository registeredClientRepository) {
    Assert.notNull(registeredClientRepository, "registeredClientRepository cannot be null");
    this.registeredClientRepository = registeredClientRepository;
  }

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    OAuth2ClientAuthenticationToken clientAuthentication =
        (OAuth2ClientAuthenticationToken) authentication;
    if (!ClientAuthenticationMethod.NONE.equals(
        clientAuthentication.getClientAuthenticationMethod())) {
      return null;
    }

    String clientId = clientAuthentication.getPrincipal().toString();
    RegisteredClient registeredClient = registeredClientRepository.findByClientId(clientId);
    if (registeredClient == null
        || !registeredClient
            .getClientAuthenticationMethods()
            .contains(ClientAuthenticationMethod.NONE)) {
      throw invalidClient();
    }

    return new OAuth2ClientAuthenticationToken(
        registeredClient, ClientAuthenticationMethod.NONE, null);
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return OAuth2ClientAuthenticationToken.class.isAssignableFrom(authentication);
  }

  private static OAuth2AuthenticationException invalidClient() {
    return new OAuth2AuthenticationException(
        new OAuth2Error(
            OAuth2ErrorCodes.INVALID_CLIENT, "Client authentication failed: client_id", ERROR_URI));
  }
}
