package com.takypok.core.config;

import static com.takypok.core.util.AuthenticationUtil.rejectAccess;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import reactor.core.publisher.Mono;

@EnableWebFluxSecurity
@Configuration
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

  @Bean
  public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
    return http.csrf(ServerHttpSecurity.CsrfSpec::disable)
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
