package com.takypok.chatservice.service.impl;

import com.takypok.chatservice.model.entity.ConversationParticipant;
import com.takypok.chatservice.model.entity.UserPresence;
import com.takypok.chatservice.model.event.ChatEvent;
import com.takypok.chatservice.model.response.PresenceStatusResponse;
import com.takypok.chatservice.repository.ConversationParticipantRepository;
import com.takypok.chatservice.repository.UserPresenceRepository;
import com.takypok.chatservice.service.ChatSessionRegistry;
import com.takypok.chatservice.service.PresenceService;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Online status is derived entirely from two Redis sets, never from {@link ChatSessionRegistry}'s
 * local (per-instance) session map — that's what makes it correct across multiple chat-service
 * instances. {@code presence:sessions:<sub>} holds that user's live session ids (one per open
 * tab/device/instance); {@code presence:online} holds just the subs with at least one live session.
 * Only 0-to-1 and 1-to-0 transitions on the sessions set touch the online set and broadcast — five
 * open tabs still produce exactly one ONLINE and one OFFLINE event.
 *
 * <p>Known gap: an instance killed ungracefully (crash/OOM) never runs the WS close handler, so its
 * sessions' entries leak in {@code presence:sessions:<sub>} and that sub can look stuck online. Not
 * handled here (would need a heartbeat + TTL sweep) — add only if this bites in practice.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PresenceServiceImpl implements PresenceService {
  private static final String ONLINE_SET_KEY = "presence:online";

  private final ReactiveStringRedisTemplate redisTemplate;
  private final ConversationParticipantRepository participantRepository;
  private final UserPresenceRepository userPresenceRepository;
  private final ChatSessionRegistry sessionRegistry;

  @Override
  public Mono<Void> markOnline(String sub, String sessionId) {
    String sessionsKey = sessionsKey(sub);
    return redisTemplate
        .opsForSet()
        .add(sessionsKey, sessionId)
        .then(redisTemplate.opsForSet().size(sessionsKey))
        .flatMap(size -> size != null && size == 1 ? goOnline(sub) : Mono.<Void>empty())
        .onErrorResume(
            error -> {
              log.error("Failed to mark {} online: {}", sub, error.getMessage(), error);
              return Mono.empty();
            });
  }

  @Override
  public Mono<Void> markOffline(String sub, String sessionId) {
    String sessionsKey = sessionsKey(sub);
    return redisTemplate
        .opsForSet()
        .remove(sessionsKey, sessionId)
        .then(redisTemplate.opsForSet().size(sessionsKey))
        .flatMap(size -> size != null && size == 0 ? goOffline(sub) : Mono.<Void>empty())
        .onErrorResume(
            error -> {
              log.error("Failed to mark {} offline: {}", sub, error.getMessage(), error);
              return Mono.empty();
            });
  }

  private Mono<Void> goOnline(String sub) {
    return redisTemplate.opsForSet().add(ONLINE_SET_KEY, sub).then(broadcastToContacts(sub, true));
  }

  private Mono<Void> goOffline(String sub) {
    ZonedDateTime now = ZonedDateTime.now();
    return redisTemplate
        .opsForSet()
        .remove(ONLINE_SET_KEY, sub)
        .then(redisTemplate.delete(sessionsKey(sub)))
        .then(userPresenceRepository.upsertLastSeen(sub, now))
        .then(broadcastToContacts(sub, false));
  }

  private Mono<Void> broadcastToContacts(String sub, boolean online) {
    return participantRepository
        .findByParticipantSub(sub)
        .map(ConversationParticipant::getConversationId)
        .collectList()
        .flatMapMany(
            conversationIds ->
                conversationIds.isEmpty()
                    ? Flux.<ConversationParticipant>empty()
                    : participantRepository.findByConversationIdIn(conversationIds))
        .map(ConversationParticipant::getParticipantSub)
        .filter(other -> !other.equals(sub))
        .distinct()
        .collectList()
        .doOnSuccess(
            contacts -> {
              if (!contacts.isEmpty()) {
                sessionRegistry.publish(
                    contacts,
                    new ChatEvent(
                        ChatEvent.ChatEventType.PRESENCE_CHANGED,
                        Map.of("sub", sub, "online", online)));
              }
            })
        .then();
  }

  @Override
  public Mono<Map<String, PresenceStatusResponse>> statusOf(Collection<String> subs) {
    if (subs.isEmpty()) {
      return Mono.just(Map.of());
    }
    List<String> distinctSubs = subs.stream().distinct().toList();

    Mono<Set<String>> onlineSubs =
        redisTemplate.opsForSet().members(ONLINE_SET_KEY).collect(Collectors.toSet());
    Mono<Map<String, ZonedDateTime>> lastSeenBySub =
        userPresenceRepository
            .findAllById(distinctSubs)
            .collectMap(UserPresence::getSub, UserPresence::getLastSeenAt);

    return Mono.zip(onlineSubs, lastSeenBySub)
        .map(
            tuple -> {
              Set<String> online = tuple.getT1();
              Map<String, ZonedDateTime> lastSeen = tuple.getT2();
              return distinctSubs.stream()
                  .collect(
                      Collectors.toMap(
                          sub -> sub,
                          sub ->
                              new PresenceStatusResponse(online.contains(sub), lastSeen.get(sub))));
            });
  }

  @Override
  public Mono<Set<String>> onlineSubsOf(Collection<String> subs) {
    if (subs.isEmpty()) {
      return Mono.just(Set.of());
    }
    Set<String> wanted = Set.copyOf(subs);
    return redisTemplate
        .opsForSet()
        .members(ONLINE_SET_KEY)
        .filter(wanted::contains)
        .collect(Collectors.toSet());
  }

  private String sessionsKey(String sub) {
    return "presence:sessions:" + sub;
  }
}
