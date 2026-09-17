package com.takypok.chatservice.service;

import com.takypok.chatservice.model.assistant.AssistantTurn;
import com.takypok.chatservice.model.response.AssistantSessionResponse;
import java.util.List;
import reactor.core.publisher.Mono;

public interface AssistantSessionService {

  Mono<AssistantSessionResponse> createSession(String sub, String application);

  Mono<List<AssistantSessionResponse>> listSessions(String sub);

  /** The application a session was scoped to at creation — used to filter retrieval on ask. */
  Mono<String> getApplication(String sessionId, String sub);

  Mono<List<AssistantTurn>> loadTurns(String sessionId, String sub);

  /** Last {@code limit} turns, oldest first — the window actually fed to the model. */
  Mono<List<AssistantTurn>> loadRecentTurns(String sessionId, String sub, int limit);

  Mono<Void> appendTurn(String sessionId, String sub, AssistantTurn turn);

  Mono<Void> deleteSession(String sessionId, String sub);
}
