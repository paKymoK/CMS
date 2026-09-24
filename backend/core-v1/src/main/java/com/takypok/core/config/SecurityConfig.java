package com.takypok.core.config;

import static com.takypok.core.util.AuthenticationUtil.rejectAccess;

import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Mono;

@EnableWebFluxSecurity
@Configuration
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

  // The 5 site subdomains + admin-app's own origin, per CLAUDE.md's "CORS allowlist is
  // maintained manually, never wildcard it" — this is what content-service/media-service/
  // chat-service's browser-facing endpoints (GET /v1/home, media upload/library, the public
  // assistant) actually need it for, since there's no gateway in front of them to do it instead.
  @Value("${cors.allowed-origins}")
  private String[] allowedOrigins;

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.asList(allowedOrigins));
    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(List.of("*"));
    configuration.setAllowCredentials(true);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

  @Bean
  public SecurityWebFilterChain securityFilterChain(
      ServerHttpSecurity http, CorsConfigurationSource corsConfigurationSource) {
    return http.csrf(ServerHttpSecurity.CsrfSpec::disable)
        .cors(cors -> cors.configurationSource(corsConfigurationSource))
        .authorizeExchange(
            exchange ->
                exchange
                    .pathMatchers(
                        "/web-socket/**",
                        "/actuator/**",
                        "/swagger-ui.html",
                        "/images/**",
                        "/files/**",
                        "/v1/doc/swagger-ui.html",
                        "/v1/doc/swagger-ui/**",
                        "/docs/api-docs",
                        "/docs/api-docs/**",
                        "/webjars/**")
                    .permitAll()
                    // HLS playback only (master.m3u8/playlist/segment GETs) — players can't attach
                    // an Authorization header, so this must stay public. Upload/delete on the same
                    // /v1/videos prefix are POST/DELETE and fall through to .authenticated() below.
                    .pathMatchers(HttpMethod.GET, "/v1/videos/**")
                    .permitAll()
                    // content-service's public homepage read API — anonymous visitors, site
                    // resolved from their own Host header. /v1/admin/** is untouched by this and
                    // still falls through to .authenticated() below.
                    .pathMatchers(HttpMethod.GET, "/v1/home/**")
                    .permitAll()
                    // chat-service's public marketing assistant — anonymous site visitors,
                    // IP-rate-limited in PublicAssistantController itself. /v1/assistant/ingest
                    // is untouched by this and still falls through to .authenticated() below.
                    .pathMatchers(HttpMethod.POST, "/v1/assistant/ask")
                    .permitAll()
                    // content-service's token-gated draft preview — anonymous at this layer (no
                    // JWT presented), but every request is validated against a minted,
                    // site-scoped, time-limited preview_token by PreviewTokenGuard/
                    // PreviewTokenService inside the controllers themselves; not a general
                    // bypass. /v1/admin/preview-tokens (minting) is untouched by this and still
                    // falls through to .authenticated() below.
                    .pathMatchers("/v1/preview/**")
                    .permitAll()
                    .anyExchange()
                    .authenticated())
        .oauth2ResourceServer(
            oauth2ResourceServer ->
                oauth2ResourceServer
                    .authenticationFailureHandler(
                        (webFilterExchange, exception) -> {
                          log.error("Authentication Failure", exception);
                          return rejectAccess(webFilterExchange);
                        })
                    .accessDeniedHandler(
                        (exchange, denied) -> {
                          log.error("Access Denied", denied);
                          return rejectAccess(exchange);
                        })
                    .jwt(Customizer.withDefaults()))
        .exceptionHandling(ex -> ex.accessDeniedHandler(accessDeniedHandler()))
        .build();
  }

  private ServerAccessDeniedHandler accessDeniedHandler() {
    return (exchange, denied) ->
        Mono.fromRunnable(() -> exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN));
  }
}
