package com.takypok.chatservice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.takypok.chatservice.model.assistant.AssistantTurn;
import com.takypok.chatservice.model.response.AssistantSessionResponse;
import com.takypok.chatservice.service.AssistantSessionService;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Assistant chat sessions live entirely in Redis under the {@code assistant:*} namespace —
 * deliberately separate from the human chat system's Postgres {@code Conversation}/{@code Message}
 * entities. History here is a TTL'd scratch-context, not a durable record.
 *
 * <p>{@code assistant:sessions:{sub}} is a ZSET of sessionIds scored by last-activity, so a user's
 * threads list most-recent-first. It carries no TTL of its own; instead {@link #listSessions}
 * lazily {@code ZREM}s any sessionId whose {@code meta} hash has already expired. This mirrors the
 * "known gap, no cron sweep" pragmatism already used in {@code PresenceServiceImpl} rather than
 * adding a scheduled cleanup job for what's ultimately a rare, self-healing case.
 */
@RequiredArgsConstructor
@Service
@ConditionalOnProperty(name = "ai.rag.enabled", havingValue = "true", matchIfMissing = false)
public class AssistantSessionServiceImpl implements AssistantSessionService {

  private static final String DEFAULT_TITLE = "New chat";
  private static final int TITLE_MAX_LEN = 40;
  private static final int STORAGE_CAP_MESSAGES = 60;
  private static final Duration SESSION_TTL = Duration.ofDays(30);

  private final ReactiveStringRedisTemplate redisTemplate;
  private final ObjectMapper objectMapper;

  @Override
  public Mono<AssistantSessionResponse> createSession(String sub, String application) {
    String id = UUID.randomUUID().toString();
    ZonedDateTime now = ZonedDateTime.now();
    Map<String, String> meta =
        Map.of(
            "ownerSub",
            sub,
            "application",
            application,
            "title",
            DEFAULT_TITLE,
            "createdAt",
            now.toString(),
            "updatedAt",
            now.toString());

    return hashOps()
        .putAll(metaKey(id), meta)
        .then(redisTemplate.expire(metaKey(id), SESSION_TTL))
        .then(redisTemplate.opsForZSet().add(indexKey(sub), id, now.toInstant().toEpochMilli()))
        .thenReturn(new AssistantSessionResponse(id, application, DEFAULT_TITLE, now, now));
  }

  @Override
  public Mono<String> getApplication(String sessionId, String sub) {
    return requireOwnedSession(sessionId, sub).map(meta -> meta.get("application"));
  }

  @Override
  public Mono<List<AssistantSessionResponse>> listSessions(String sub) {
    return redisTemplate
        .opsForZSet()
        .reverseRangeWithScores(indexKey(sub), Range.closed(0L, -1L))
        .concatMap(tuple -> loadSessionOrPrune(sub, tuple.getValue()))
        .collectList();
  }

  private Mono<AssistantSessionResponse> loadSessionOrPrune(String sub, String sessionId) {
    return readMeta(sessionId)
        .flatMap(
            meta -> {
              if (meta.isEmpty()) {
                return redisTemplate
                    .opsForZSet()
                    .remove(indexKey(sub), sessionId)
                    .then(Mono.<AssistantSessionResponse>empty());
              }
              return Mono.just(toResponse(sessionId, meta));
            });
  }

  @Override
  public Mono<List<AssistantTurn>> loadTurns(String sessionId, String sub) {
    return requireOwnedSession(sessionId, sub).then(readTurns(sessionId, 0, -1));
  }

  @Override
  public Mono<List<AssistantTurn>> loadRecentTurns(String sessionId, String sub, int limit) {
    return requireOwnedSession(sessionId, sub).then(readTurns(sessionId, -limit, -1));
  }

  @Override
  public Mono<Void> appendTurn(String sessionId, String sub, AssistantTurn turn) {
    return requireOwnedSession(sessionId, sub)
        .flatMap(
            meta -> {
              String listKey = messagesKey(sessionId);
              ZonedDateTime now = ZonedDateTime.now();

              Mono<Void> push =
                  redisTemplate
                      .opsForList()
                      .rightPush(listKey, serialize(turn))
                      .then(redisTemplate.opsForList().trim(listKey, -STORAGE_CAP_MESSAGES, -1))
                      .then(redisTemplate.expire(listKey, SESSION_TTL))
                      .then();

              boolean isFirstUserTurn =
                  turn.getRole() == AssistantTurn.Role.USER
                      && DEFAULT_TITLE.equals(meta.get("title"));

              Map<String, String> updatedMeta = new HashMap<>(meta);
              if (isFirstUserTurn) {
                updatedMeta.put("title", deriveTitle(turn.getContent()));
              }
              updatedMeta.put("updatedAt", now.toString());

              Mono<Void> touchMeta =
                  hashOps()
                      .putAll(metaKey(sessionId), updatedMeta)
                      .then(redisTemplate.expire(metaKey(sessionId), SESSION_TTL))
                      .then();

              Mono<Void> bumpIndex =
                  redisTemplate
                      .opsForZSet()
                      .add(indexKey(sub), sessionId, now.toInstant().toEpochMilli())
                      .then();

              return Mono.when(push, touchMeta, bumpIndex);
            });
  }

  @Override
  public Mono<Void> deleteSession(String sessionId, String sub) {
    return requireOwnedSession(sessionId, sub)
        .flatMap(
            meta ->
                Mono.when(
                    redisTemplate.delete(metaKey(sessionId), messagesKey(sessionId)),
                    redisTemplate.opsForZSet().remove(indexKey(sub), sessionId)));
  }

  private Mono<List<AssistantTurn>> readTurns(String sessionId, long start, long end) {
    return redisTemplate
        .opsForList()
        .range(messagesKey(sessionId), start, end)
        .map(this::deserialize)
        .collectList();
  }

  private Mono<Map<String, String>> requireOwnedSession(String sessionId, String sub) {
    return readMeta(sessionId)
        .filter(meta -> !meta.isEmpty() && sub.equals(meta.get("ownerSub")))
        .switchIfEmpty(Mono.error(new IllegalStateException("Assistant session not found")));
  }

  private Mono<Map<String, String>> readMeta(String sessionId) {
    return hashOps().entries(metaKey(sessionId)).collectMap(Map.Entry::getKey, Map.Entry::getValue);
  }

  private ReactiveHashOperations<String, String, String> hashOps() {
    return redisTemplate.opsForHash();
  }

  private AssistantSessionResponse toResponse(String id, Map<String, String> meta) {
    return new AssistantSessionResponse(
        id,
        meta.get("application"),
        meta.get("title"),
        ZonedDateTime.parse(meta.get("createdAt")),
        ZonedDateTime.parse(meta.get("updatedAt")));
  }

  private String deriveTitle(String question) {
    String trimmed = question.strip();
    return trimmed.length() <= TITLE_MAX_LEN
        ? trimmed
        : trimmed.substring(0, TITLE_MAX_LEN).stripTrailing() + "…";
  }

  private String serialize(AssistantTurn turn) {
    try {
      return objectMapper.writeValueAsString(turn);
    } catch (JsonProcessingException e) {
      throw new IllegalStateException("Failed to serialize assistant turn", e);
    }
  }

  private AssistantTurn deserialize(String json) {
    try {
      return objectMapper.readValue(json, AssistantTurn.class);
    } catch (JsonProcessingException e) {
      throw new IllegalStateException("Failed to deserialize assistant turn", e);
    }
  }

  private String indexKey(String sub) {
    return "assistant:sessions:" + sub;
  }

  private String metaKey(String sessionId) {
    return "assistant:session:" + sessionId + ":meta";
  }

  private String messagesKey(String sessionId) {
    return "assistant:session:" + sessionId + ":messages";
  }
}
