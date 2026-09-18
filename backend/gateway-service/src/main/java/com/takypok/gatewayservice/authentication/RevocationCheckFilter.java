package com.takypok.gatewayservice.authentication;

import com.takypok.core.Constants;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class RevocationCheckFilter implements GlobalFilter, Ordered {

  private static final String REVOKED_KEY_PREFIX = "revoked:jti:";

  private final ReactiveStringRedisTemplate redisTemplate;

  // Fallback used when a token has no session policy claim (e.g. issued before this feature)
  @Value("${single-tab.redis-on-failure:FAIL_OPEN}")
  private String globalRedisOnFailure;

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    return exchange
        .getPrincipal()
        .flatMap(
            principal -> {
              if (!(principal instanceof JwtAuthenticationToken jwtAuth)) {
                return chain.filter(exchange);
              }
              String jti = jwtAuth.getToken().getClaimAsString("jti");
              if (jti == null) return chain.filter(exchange);

              // Only single-tab clients carry the policy claim — others pass through immediately
              String policyClaim =
                  jwtAuth.getToken().getClaimAsString(Constants.SESSION_POLICY_CLAIM);
              if (policyClaim == null) return chain.filter(exchange);

              boolean failOpen = resolveFailOpen(policyClaim);

              return redisTemplate
                  .hasKey(REVOKED_KEY_PREFIX + jti)
                  .onErrorResume(
                      e -> {
                        if (failOpen) {
                          log.warn(
                              "Redis unavailable — revocation check skipped for jti={}: {}",
                              jti,
                              e.getMessage());
                          return Mono.just(false);
                        }
                        log.error(
                            "Redis unavailable (fail-closed) — blocking request for jti={}: {}",
                            jti,
                            e.getMessage());
                        return Mono.error(
                            new ResponseStatusException(
                                HttpStatus.SERVICE_UNAVAILABLE,
                                "Token validation service unavailable"));
                      })
                  .flatMap(
                      revoked -> {
                        if (Boolean.TRUE.equals(revoked)) {
                          log.debug("Rejected revoked token jti={}", jti);
                          return writeUnauthorized(exchange);
                        }
                        return chain.filter(exchange);
                      });
            })
        .switchIfEmpty(chain.filter(exchange));
  }

  private Mono<Void> writeUnauthorized(ServerWebExchange exchange) {
    var response = exchange.getResponse();
    response.setStatusCode(HttpStatus.UNAUTHORIZED);
    response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
    response.getHeaders().set(HttpHeaders.WWW_AUTHENTICATE, "Bearer error=\"invalid_token\"");
    byte[] body = "{\"error\":\"unauthorized\"}".getBytes(StandardCharsets.UTF_8);
    DataBuffer buffer = response.bufferFactory().wrap(body);
    return response.writeWith(Mono.just(buffer));
  }

  private boolean resolveFailOpen(String policyClaim) {
    if (Constants.SESSION_POLICY_OPEN.equals(policyClaim)) return true;
    if (Constants.SESSION_POLICY_CLOSED.equals(policyClaim)) return false;
    // Unrecognised value — fall back to global setting
    return !"FAIL_CLOSED".equalsIgnoreCase(globalRedisOnFailure);
  }

  @Override
  public int getOrder() {
    return 0;
  }
}
