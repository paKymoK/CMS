package com.takypok.gatewayservice.config;

import java.net.InetSocketAddress;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

/**
 * Backs the RequestRateLimiter default-filter applied to every route (application.yaml). Keyed by
 * the server-observed remote address — never a client-supplied header like X-Forwarded-For, which
 * would let a caller spoof a fresh identity on every request and bypass the limit entirely. Same
 * rule chat-service's own AssistantRateLimiter and auth-service's login lockout follow.
 */
@Configuration
public class RateLimiterConfig {

  @Bean
  public KeyResolver ipKeyResolver() {
    return exchange -> {
      InetSocketAddress remoteAddress = exchange.getRequest().getRemoteAddress();
      String ip = remoteAddress != null ? remoteAddress.getAddress().getHostAddress() : "unknown";
      return Mono.just(ip);
    };
  }
}
