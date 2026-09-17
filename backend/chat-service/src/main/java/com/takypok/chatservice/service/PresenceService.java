package com.takypok.chatservice.service;

import com.takypok.chatservice.model.response.PresenceStatusResponse;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import reactor.core.publisher.Mono;

public interface PresenceService {

  /** No-op past the first live session for {@code sub} — only that transition broadcasts. */
  Mono<Void> markOnline(String sub, String sessionId);

  /** No-op unless {@code sessionId} was {@code sub}'s last live session. */
  Mono<Void> markOffline(String sub, String sessionId);

  Mono<Map<String, PresenceStatusResponse>> statusOf(Collection<String> subs);

  /** Subset of {@code subs} that are currently online. */
  Mono<Set<String>> onlineSubsOf(Collection<String> subs);
}
