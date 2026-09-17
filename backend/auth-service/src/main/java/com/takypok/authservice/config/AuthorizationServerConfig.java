package com.takypok.authservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.takypok.authservice.model.entity.ClientRoleAssignment;
import com.takypok.authservice.repository.ClientRoleAssignmentRepository;
import com.takypok.authservice.repository.ClientSessionPolicyRepository;
import com.takypok.authservice.util.jose.Jwks;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.oidc.authentication.OidcUserInfoAuthenticationContext;
import org.springframework.security.oauth2.server.authorization.oidc.authentication.OidcUserInfoAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.oauth2.server.authorization.token.DelegatingOAuth2TokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.JwtGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
public class AuthorizationServerConfig {
  @Value("${auth.issuer-uri:http://127.0.0.1:9000}")
  private String issuerUri;

  @Value("${auth.workflow-client.redirect-uris}")
  private String[] workflowRedirectUris;

  @Value("${auth.workflow-client.post-logout-redirect-uris}")
  private String[] workflowPostLogoutRedirectUris;

  private static final String CUSTOM_CONSENT_PAGE_URI = "/oauth2/consent";
  private static final String WORKFLOW_CLIENT_ID = "workflow";
  private static final String WORKFLOW_CLIENT_SECRET = "{noop}workflow-secret";

  @Bean
  @Order(Ordered.HIGHEST_PRECEDENCE)
  public SecurityFilterChain authorizationServerSecurityFilterChain(
      HttpSecurity http,
      CorsConfigurationSource corsConfigurationSource,
      SecurityContextRepository securityContextRepository)
      throws Exception {
    Function<OidcUserInfoAuthenticationContext, OidcUserInfo> userInfoMapper =
        (context) -> {
          OidcUserInfoAuthenticationToken authentication = context.getAuthentication();
          JwtAuthenticationToken principal = (JwtAuthenticationToken) authentication.getPrincipal();
          return new OidcUserInfo(principal.getToken().getClaims());
        };

    OAuth2AuthorizationServerConfigurer authorizationServerConfigurer =
        OAuth2AuthorizationServerConfigurer.authorizationServer();

    http.csrf(AbstractHttpConfigurer::disable)
        .cors(cors -> cors.configurationSource(corsConfigurationSource))
        .securityMatcher(authorizationServerConfigurer.getEndpointsMatcher())
        .securityContext(
            context ->
                context
                    .securityContextRepository(securityContextRepository)
                    .requireExplicitSave(true))
        .with(
            authorizationServerConfigurer,
            (authorizationServer) ->
                authorizationServer
                    .authorizationEndpoint(
                        authorizationEndpoint ->
                            authorizationEndpoint.consentPage(CUSTOM_CONSENT_PAGE_URI))
                    .oidc(
                        (oidc) ->
                            oidc.userInfoEndpoint(
                                (userInfo) -> userInfo.userInfoMapper(userInfoMapper))))
        .authorizeHttpRequests(
            (authorize) ->
                authorize
                    .requestMatchers(
                        "/oauth2/token",
                        "/oauth2/jwks",
                        "/oauth2/revoke",
                        "/oauth2/introspect",
                        "/.well-known/openid-configuration",
                        "/connect/register")
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .exceptionHandling(
            (exceptions) -> {
              MediaTypeRequestMatcher htmlMatcher =
                  new MediaTypeRequestMatcher(MediaType.TEXT_HTML);
              htmlMatcher.setIgnoredMediaTypes(Collections.singleton(MediaType.ALL));
              exceptions
                  .defaultAuthenticationEntryPointFor(
                      new LoginUrlAuthenticationEntryPoint("/login"), htmlMatcher)
                  .defaultAuthenticationEntryPointFor(
                      (request, response, ex) -> {
                        response.setStatus(HttpStatus.UNAUTHORIZED.value());
                        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                        response.getWriter().write("{\"error\":\"unauthorized\"}");
                      },
                      request -> true);
            });
    return http.build();
  }

  @Bean
  public JdbcRegisteredClientRepository registeredClientRepository(
      JdbcTemplate jdbcTemplate, ClientRoleAssignmentRepository clientRoleAssignmentRepository) {
    JdbcRegisteredClientRepository registeredClientRepository =
        new JdbcRegisteredClientRepository(jdbcTemplate);

    TokenSettings tokenSettings =
        TokenSettings.builder()
            .accessTokenTimeToLive(Duration.ofMinutes(15))
            .refreshTokenTimeToLive(Duration.ofDays(1))
            .reuseRefreshTokens(false)
            .build();

    RegisteredClient workflow =
        RegisteredClient.withId(UUID.randomUUID().toString())
            .clientId(WORKFLOW_CLIENT_ID)
            .clientSecret(WORKFLOW_CLIENT_SECRET)
            .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
            .tokenSettings(tokenSettings)
            .redirectUris(uris -> uris.addAll(Arrays.asList(workflowRedirectUris)))
            .postLogoutRedirectUris(
                uris -> uris.addAll(Arrays.asList(workflowPostLogoutRedirectUris)))
            .scope(OidcScopes.OPENID)
            .scope(OidcScopes.PROFILE)
            .scope("offline_access")
            .clientSettings(
                ClientSettings.builder()
                    .requireAuthorizationConsent(false)
                    .requireProofKey(true)
                    .build())
            .build();

    upsertClient(registeredClientRepository, workflow);
    seedAdminRole(registeredClientRepository, clientRoleAssignmentRepository, WORKFLOW_CLIENT_ID);
    seedGroupRoles(registeredClientRepository, clientRoleAssignmentRepository, WORKFLOW_CLIENT_ID);
    return registeredClientRepository;
  }

  private void upsertClient(
      JdbcRegisteredClientRepository repository, RegisteredClient desiredClient) {
    RegisteredClient existing = repository.findByClientId(desiredClient.getClientId());
    if (existing != null) {
      repository.save(RegisteredClient.from(desiredClient).id(existing.getId()).build());
      return;
    }
    repository.save(desiredClient);
  }

  private void seedGroupRoles(
      JdbcRegisteredClientRepository repository,
      ClientRoleAssignmentRepository clientRoleAssignmentRepository,
      String clientId) {
    RegisteredClient client = repository.findByClientId(clientId);
    if (client == null) return;
    Map.of(
            "grp-l1-support", "AGENT",
            "grp-l2-support", "AGENT",
            "grp-l3-support", "AGENT",
            "grp-service-manager", "APPROVER")
        .forEach(
            (groupId, role) -> {
              if (clientRoleAssignmentRepository.existsByRegisteredClientIdAndGroupIdAndProjectId(
                  client.getId(), groupId, null)) return;
              clientRoleAssignmentRepository.save(
                  ClientRoleAssignment.builder()
                      .id(UUID.randomUUID().toString())
                      .registeredClientId(client.getId())
                      .groupId(groupId)
                      .projectId(null)
                      .role(role)
                      .build());
            });
  }

  private void seedAdminRole(
      JdbcRegisteredClientRepository repository,
      ClientRoleAssignmentRepository clientRoleAssignmentRepository,
      String clientId) {
    RegisteredClient client = repository.findByClientId(clientId);
    if (client == null) return;
    if (clientRoleAssignmentRepository.existsByRegisteredClientIdAndUserSubAndProjectId(
        client.getId(), "admin", null)) return;
    clientRoleAssignmentRepository.save(
        ClientRoleAssignment.builder()
            .id(UUID.randomUUID().toString())
            .registeredClientId(client.getId())
            .userSub("admin")
            .projectId(null)
            .role("ADMIN")
            .build());
  }

  @Bean
  public OAuth2AuthorizationService authorizationService(
      JdbcTemplate jdbcTemplate,
      RegisteredClientRepository registeredClientRepository,
      ClientSessionPolicyRepository policyRepository,
      StringRedisTemplate redisTemplate,
      ObjectMapper objectMapper) {
    JdbcOAuth2AuthorizationService jdbcService =
        new JdbcOAuth2AuthorizationService(jdbcTemplate, registeredClientRepository);
    return new SingleTabOAuth2AuthorizationService(
        jdbcService, policyRepository, jdbcTemplate, redisTemplate, objectMapper);
  }

  @Bean
  public JdbcOAuth2AuthorizationConsentService authorizationConsentService(
      JdbcTemplate jdbcTemplate, RegisteredClientRepository registeredClientRepository) {

    return new JdbcOAuth2AuthorizationConsentService(jdbcTemplate, registeredClientRepository);
  }

  @Bean
  public OAuth2TokenGenerator<OAuth2Token> tokenGenerator(
      JWKSource<SecurityContext> jwkSource,
      OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer) {
    JwtGenerator jwtGenerator = new JwtGenerator(new NimbusJwtEncoder(jwkSource));
    jwtGenerator.setJwtCustomizer(tokenCustomizer);

    OAuth2TokenGenerator<OAuth2RefreshToken> refreshTokenGenerator =
        (OAuth2TokenContext context) -> {
          if (!OAuth2TokenType.REFRESH_TOKEN.equals(context.getTokenType())) return null;
          if (!context
              .getRegisteredClient()
              .getAuthorizationGrantTypes()
              .contains(AuthorizationGrantType.REFRESH_TOKEN)) return null;
          Instant now = Instant.now();
          Instant expiresAt =
              now.plus(
                  context.getRegisteredClient().getTokenSettings().getRefreshTokenTimeToLive());
          return new OAuth2RefreshToken(UUID.randomUUID().toString(), now, expiresAt);
        };

    return new DelegatingOAuth2TokenGenerator(jwtGenerator, refreshTokenGenerator);
  }

  @Bean
  public JWKSource<SecurityContext> jwkSource() {
    RSAKey rsaKey = Jwks.generateRsa();
    JWKSet jwkSet = new JWKSet(rsaKey);
    return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
  }

  @Bean
  public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
    return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
  }

  @Bean
  public AuthorizationServerSettings authorizationServerSettings() {
    return AuthorizationServerSettings.builder().issuer(issuerUri).build();
  }

  @Bean
  JdbcUserDetailsManager jdbcUserDetailsManager(DataSource dataSource) {
    return new JdbcUserDetailsManager(dataSource);
  }
}
