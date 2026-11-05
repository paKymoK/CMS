package com.takypok.authservice.config;

import com.takypok.authservice.service.SuperAdminService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * The management API ({@code /v1/**}: users, roles, groups, clients, departments, units). It takes
 * a bearer token and nothing else (no session, no CSRF surface), and only an active Super Admin may
 * call it. Callers reach it through the gateway with the token admin-app already holds.
 */
@Configuration(proxyBeanMethods = false)
public class ApiSecurityConfig {

  @Bean
  @Order(Ordered.HIGHEST_PRECEDENCE + 1)
  public SecurityFilterChain apiSecurityFilterChain(
      HttpSecurity http, SuperAdminService superAdminService) throws Exception {
    return http.securityMatcher("/v1/**")
        .csrf(AbstractHttpConfigurer::disable)
        .cors(AbstractHttpConfigurer::disable)
        .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(
            authorize ->
                authorize
                    .anyRequest()
                    .access(
                        (authentication, context) ->
                            new AuthorizationDecision(
                                superAdminService.isActiveSuperAdmin(authentication.get()))))
        .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
        .build();
  }
}
