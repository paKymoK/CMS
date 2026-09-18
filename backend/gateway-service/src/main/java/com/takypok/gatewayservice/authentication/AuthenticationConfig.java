package com.takypok.gatewayservice.authentication;

import java.util.Arrays;
import java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.security.reactive.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebFluxSecurity
@Slf4j
public class AuthenticationConfig {

  // Same allowlist convention as core-v1's SecurityConfig (used by the 4 backend services) —
  // unlike the source, this is NOT a wildcard, per CLAUDE.md's "never wildcard it" CORS rule.
  @Value("${cors.allowed-origins}")
  private String[] corsAllowedOrigins;

  @Bean
  @Order(0)
  public SecurityWebFilterChain actuatorSecurityWebFilterChain(ServerHttpSecurity http) {
    return http.securityMatcher(EndpointRequest.toAnyEndpoint())
        .authorizeExchange(exchanges -> exchanges.anyExchange().permitAll())
        .csrf(ServerHttpSecurity.CsrfSpec::disable)
        .build();
  }

  @Bean
  @Order(1)
  public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
    return http.csrf(ServerHttpSecurity.CsrfSpec::disable)
        .cors(corsSpec -> corsSpec.configurationSource(corsFilter()))
        .authorizeExchange(
            exchanges ->
                exchanges
                    // The pre-existing, untouched CRM chat feature's websocket — auth happens at
                    // the app layer for it, same as it did in the source this was forked from.
                    .pathMatchers("/chat-service/web-socket/**")
                    .permitAll()
                    // content-service's public homepage read API — site resolved from the
                    // request's own Host header, anonymous visitors. /content-service/v1/admin/**
                    // is untouched by this and still falls through to .authenticated() below.
                    .pathMatchers("/content-service/v1/home/**")
                    .permitAll()
                    // media-service's content-addressed static serving — public once a site's
                    // published page embeds the URL.
                    .pathMatchers("/media-service/images/**", "/media-service/files/**")
                    .permitAll()
                    // chat-service's public marketing assistant — anonymous site visitors,
                    // IP-rate-limited both here (see application.yaml's RequestRateLimiter) and
                    // again in chat-service itself. /v1/assistant/ingest is untouched by this.
                    .pathMatchers(HttpMethod.POST, "/chat-service/v1/assistant/ask")
                    .permitAll()
                    .pathMatchers(
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/webjars/**",
                        "/v3/api-docs/**",
                        "/*/docs/api-docs",
                        "/api/health",
                        "/api/health/**")
                    .permitAll()
                    // HLS playback only (master.m3u8/playlist/segment GETs) — players can't attach
                    // an Authorization header. Upload/delete on the same
                    // /media-service/v1/videos prefix are POST/DELETE and must fall through to
                    // .authenticated() below.
                    .pathMatchers(HttpMethod.GET, "/media-service/v1/videos/**")
                    .permitAll()
                    .anyExchange()
                    .authenticated())
        .oauth2ResourceServer(oauth2 -> oauth2.jwt(withDefaults -> {}))
        .build();
  }

  @Bean
  public CustomGlobalFilter customGlobalFilter() {
    return new CustomGlobalFilter();
  }

  public UrlBasedCorsConfigurationSource corsFilter() {
    final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    final CorsConfiguration config = new CorsConfiguration();
    config.setAllowedHeaders(Collections.singletonList("*"));
    config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "OPTIONS", "DELETE", "PATCH"));
    config.setAllowedOrigins(Arrays.asList(corsAllowedOrigins));
    config.setAllowCredentials(true);
    config.setMaxAge(3600L);
    source.registerCorsConfiguration("/**", config);
    return source;
  }
}
