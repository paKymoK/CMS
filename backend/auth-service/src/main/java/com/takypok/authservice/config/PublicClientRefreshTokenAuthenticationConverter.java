package com.takypok.authservice.config;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.util.StringUtils;

/**
 * Authenticates the refresh_token grant for PKCE-only public clients (cms-admin:
 * ClientAuthenticationMethod.NONE, no secret). The built-in PublicClientAuthenticationConverter/
 * Provider pair only ever applies to the authorization_code+PKCE exchange — they unconditionally
 * require a code_verifier — so a client that never holds a secret has no way to authenticate a
 * refresh request without this. Only matches when the request carries neither an Authorization
 * header nor a client_secret parameter, so confidential-style calls (e.g. Postman, using
 * cms-admin's CLIENT_SECRET_BASIC registration) still go through the standard converters untouched.
 */
public final class PublicClientRefreshTokenAuthenticationConverter
    implements AuthenticationConverter {

  @Nullable
  @Override
  public Authentication convert(HttpServletRequest request) {
    String grantType = request.getParameter(OAuth2ParameterNames.GRANT_TYPE);
    if (!AuthorizationGrantType.REFRESH_TOKEN.getValue().equals(grantType)) {
      return null;
    }
    if (StringUtils.hasText(request.getHeader(HttpHeaders.AUTHORIZATION))) {
      return null;
    }
    if (StringUtils.hasText(request.getParameter(OAuth2ParameterNames.CLIENT_SECRET))) {
      return null;
    }

    String[] clientIds = request.getParameterValues(OAuth2ParameterNames.CLIENT_ID);
    if (clientIds == null || clientIds.length != 1 || !StringUtils.hasText(clientIds[0])) {
      throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_REQUEST);
    }

    return new OAuth2ClientAuthenticationToken(
        clientIds[0], ClientAuthenticationMethod.NONE, null, Collections.emptyMap());
  }
}
