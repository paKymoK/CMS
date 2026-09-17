package com.takypok.chatservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.takypok.chatservice.model.event.ChatBroadcast;
import com.takypok.chatservice.model.event.ChatEvent;
import jakarta.annotation.PostConstruct;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.ReactiveSubscription;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.ReactiveRedisMessageListenerContainer;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Sinks;

/**
 * Tracks live WebSocket connections per authenticated user (sub) on THIS instance and fans out
 * {@link ChatEvent}s to them. Cross-instance delivery goes through Redis pub/sub: {@link #publish}
 * sends the event plus its recipient subs to every chat-service instance, and this instance's own
 * subscription ({@link #subscribeToRemoteEvents}) pushes to whichever of those subs it happens to
 * hold a live session for — instances never touch sessions they don't own. Redis pub/sub is
 * fire-and-forget (no persistence, no queue); that's fine because Postgres remains the source of
 * truth and clients catch up via REST on reconnect if a publish is missed.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ChatSessionRegistry {
  private static final String CHANNEL = "chat:events";

  private final ObjectMapper objectMapper;
  private final ReactiveStringRedisTemplate redisTemplate;
  private final ReactiveRedisMessageListenerContainer listenerContainer;
  private final Map<String, Set<Sinks.Many<String>>> sinksBySub = new ConcurrentHashMap<>();

  @PostConstruct
  private void subscribeToRemoteEvents() {
    listenerContainer
        .receive(ChannelTopic.of(CHANNEL))
        .map(ReactiveSubscription.Message::getMessage)
        .subscribe(
            this::dispatchLocally,
            error -> log.error("Chat Redis subscription failed: {}", error.getMessage(), error));
  }

  public void register(String sub, Sinks.Many<String> sink) {
    sinksBySub.computeIfAbsent(sub, key -> new CopyOnWriteArraySet<>()).add(sink);
  }

  public void unregister(String sub, Sinks.Many<String> sink) {
    sinksBySub.computeIfPresent(
        sub,
        (key, sinks) -> {
          sinks.remove(sink);
          return sinks.isEmpty() ? null : sinks;
        });
  }

  /**
   * Fans an event out to every chat-service instance via Redis; never dispatches locally itself.
   */
  public void publish(Collection<String> subs, ChatEvent event) {
    String payload = serialize(new ChatBroadcast(List.copyOf(subs), event));
    if (payload == null) {
      return;
    }
    redisTemplate
        .convertAndSend(CHANNEL, payload)
        .subscribe(
            count -> {},
            error ->
                log.error(
                    "Failed to publish chat event {}: {}",
                    event.type(),
                    error.getMessage(),
                    error));
  }

  private void dispatchLocally(String payload) {
    ChatBroadcast broadcast = deserialize(payload);
    if (broadcast == null) {
      return;
    }
    String eventPayload = serialize(broadcast.event());
    if (eventPayload == null) {
      return;
    }
    broadcast.subs().forEach(sub -> sendToLocalSessions(sub, eventPayload));
  }

  private void sendToLocalSessions(String sub, String eventPayload) {
    Set<Sinks.Many<String>> sinks = sinksBySub.get(sub);
    if (sinks == null || sinks.isEmpty()) {
      return;
    }
    sinks.forEach(sink -> sink.tryEmitNext(eventPayload));
  }

  private String serialize(Object value) {
    try {
      return objectMapper.writeValueAsString(value);
    } catch (Exception e) {
      log.error("Unable to serialize chat payload: {}", e.getMessage(), e);
      return null;
    }
  }

  private ChatBroadcast deserialize(String payload) {
    try {
      return objectMapper.readValue(payload, ChatBroadcast.class);
    } catch (Exception e) {
      log.error("Unable to deserialize chat broadcast: {}", e.getMessage(), e);
      return null;
    }
  }
}
