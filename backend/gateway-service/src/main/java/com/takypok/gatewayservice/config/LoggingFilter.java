package com.takypok.gatewayservice.config;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.*;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class LoggingFilter implements GlobalFilter {

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    Set<URI> uris =
        exchange.getAttributeOrDefault(GATEWAY_ORIGINAL_REQUEST_URL_ATTR, Collections.emptySet());
    String originalUri = (uris.isEmpty()) ? "Unknown" : uris.iterator().next().toString();
    Route route = exchange.getAttribute(GATEWAY_ROUTE_ATTR);
    URI routeUri = exchange.getAttribute(GATEWAY_REQUEST_URL_ATTR);
    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .defaultIfEmpty(
            new AnonymousAuthenticationToken(
                "anonymous", "anonymous", List.of(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))))
        .flatMap(
            authentication -> {
              String username = authentication.getName();

              log.info(
                  "Incoming request {} | User: {} | Routed to id: {}, uri: {}",
                  originalUri,
                  username,
                  route.getId(),
                  routeUri);

              return chain.filter(exchange);
            });
  }
}
