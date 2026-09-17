package com.takypok.chatservice.websocket;

import com.takypok.chatservice.service.ChatSessionRegistry;
import com.takypok.chatservice.service.PresenceService;
import java.util.concurrent.atomic.AtomicReference;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.CloseStatus;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

/**
 * Push-only WebSocket channel: clients authenticate by sending {@code Bearer <jwt>} as the first
 * text frame (mirrors workflow-service's SlaEventWebSocket handshake), then only receive server
 * events. Sending a message is always done via the REST API so persistence stays the source of
 * truth; the WS connection is purely for real-time fan-out.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ChatWebSocketHandler implements WebSocketHandler {
  @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
  private String jwkSetUri;

  private final ChatSessionRegistry sessionRegistry;
  private final PresenceService presenceService;

  @NonNull
  @Override
  public Mono<Void> handle(WebSocketSession session) {
    Sinks.Many<String> sink = Sinks.many().unicast().onBackpressureBuffer();
    AtomicReference<String> authenticatedSub = new AtomicReference<>();

    Mono<Void> inbound =
        session
            .receive()
            .doOnNext(
                message -> {
                  if (authenticatedSub.get() != null) {
                    return; // authenticated sessions don't send anything further
                  }
                  String sub = authenticate(message.getPayloadAsText());
                  if (sub == null) {
                    log.warn("Chat WS session {} authentication failed", session.getId());
                    session.close(CloseStatus.POLICY_VIOLATION).subscribe();
                    return;
                  }
                  authenticatedSub.set(sub);
                  sessionRegistry.register(sub, sink);
                  presenceService.markOnline(sub, session.getId()).subscribe();
                  log.info("Chat WS session {} authenticated as {}", session.getId(), sub);
                })
            .then();

    Mono<Void> outbound = session.send(sink.asFlux().map(session::textMessage));

    Mono<Void> cleanup =
        session
            .closeStatus()
            .doOnNext(
                status -> {
                  String sub = authenticatedSub.get();
                  if (sub != null) {
                    sessionRegistry.unregister(sub, sink);
                    presenceService.markOffline(sub, session.getId()).subscribe();
                  }
                })
            .then();

    return Mono.zip(inbound, outbound, cleanup).then();
  }

  private String authenticate(String payload) {
    try {
      String token = payload.startsWith("Bearer ") ? payload.substring(7) : payload;
      Jwt jwt = NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build().decode(token);
      return jwt.getSubject();
    } catch (JwtException e) {
      log.error("Chat WS JWT validation failed: {}", e.getMessage());
      return null;
    }
  }
}
