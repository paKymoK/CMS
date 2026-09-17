package com.takypok.chatservice.controller;

import static com.takypok.core.util.AuthenticationUtil.getUserInfo;

import com.takypok.chatservice.model.AnswerResponse;
import com.takypok.chatservice.model.CreateSessionRequest;
import com.takypok.chatservice.model.IngestRequest;
import com.takypok.chatservice.model.IngestResponse;
import com.takypok.chatservice.model.QuestionRequest;
import com.takypok.chatservice.model.assistant.AssistantTurn;
import com.takypok.chatservice.model.response.AssistantSessionResponse;
import com.takypok.chatservice.service.AssistantService;
import com.takypok.chatservice.service.AssistantSessionService;
import com.takypok.chatservice.service.IngestionService;
import com.takypok.core.exception.ApplicationException;
import com.takypok.core.model.Message;
import com.takypok.core.model.authentication.Roles;
import com.takypok.core.util.AuthenticationUtil;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/assist")
@RequiredArgsConstructor
@ConditionalOnProperty(name = "ai.rag.enabled", havingValue = "true", matchIfMissing = false)
public class AssistantController {

  // Turns fed to the model per question — separate from AssistantSessionServiceImpl's larger
  // storage cap, to bound tokens against num-ctx alongside the RAG chunks.
  private static final int PROMPT_WINDOW_MESSAGES = 12;

  private final AssistantService assistantService;
  private final AssistantSessionService assistantSessionService;
  private final IngestionService ingestionService;

  @PostMapping("/sessions")
  public Mono<ResponseEntity<AssistantSessionResponse>> createSession(
      @RequestBody CreateSessionRequest request, Authentication authentication) {
    String application =
        request == null ? null : StringUtils.trimWhitespace(request.getApplication());

    return validateApplication(application)
        .then(
            assistantSessionService
                .createSession(getUserInfo(authentication).getSub(), application)
                .map(ResponseEntity::ok));
  }

  @GetMapping("/applications")
  public Mono<ResponseEntity<List<String>>> listApplications() {
    return ingestionService.listApplications().map(ResponseEntity::ok);
  }

  @GetMapping("/sessions")
  public Mono<ResponseEntity<List<AssistantSessionResponse>>> listSessions(
      Authentication authentication) {
    return assistantSessionService
        .listSessions(getUserInfo(authentication).getSub())
        .map(ResponseEntity::ok);
  }

  @GetMapping("/sessions/{sessionId}/messages")
  public Mono<ResponseEntity<List<AssistantTurn>>> messages(
      @PathVariable String sessionId, Authentication authentication) {
    return assistantSessionService
        .loadTurns(sessionId, getUserInfo(authentication).getSub())
        .map(ResponseEntity::ok);
  }

  @PostMapping("/sessions/{sessionId}/ask")
  public Mono<ResponseEntity<AnswerResponse>> ask(
      @PathVariable String sessionId,
      @RequestBody QuestionRequest request,
      Authentication authentication) {
    String sub = getUserInfo(authentication).getSub();
    String question = request.getQuestion();

    return assistantSessionService
        .getApplication(sessionId, sub)
        .flatMap(
            application ->
                assistantSessionService
                    .loadRecentTurns(sessionId, sub, PROMPT_WINDOW_MESSAGES)
                    .flatMap(history -> assistantService.ask(question, history, application)))
        .flatMap(answer -> persistTurns(sessionId, sub, question, answer).thenReturn(answer))
        .map(ResponseEntity::ok);
  }

  @DeleteMapping("/sessions/{sessionId}")
  public Mono<ResponseEntity<Void>> deleteSession(
      @PathVariable String sessionId, Authentication authentication) {
    return assistantSessionService
        .deleteSession(sessionId, getUserInfo(authentication).getSub())
        .thenReturn(ResponseEntity.noContent().build());
  }

  @PostMapping("/ingest")
  public Mono<ResponseEntity<IngestResponse>> ingest(
      @RequestBody(required = false) IngestRequest request, Authentication authentication) {
    return requireAdmin(authentication)
        .then(ingestionService.ingest(request))
        .map(ResponseEntity::ok);
  }

  @GetMapping("/health")
  public Mono<ResponseEntity<String>> health() {
    return Mono.just(ResponseEntity.ok("OK"));
  }

  /**
   * Ingest wipes/rewrites the shared knowledge base — restricted to admins, unlike ask/sessions.
   */
  private Mono<Void> requireAdmin(Authentication authentication) {
    if (AuthenticationUtil.getRoles(authentication).contains(Roles.ADMIN)) {
      return Mono.empty();
    }
    return Mono.error(new ApplicationException(Message.Application.ERROR, "Admin role required"));
  }

  private Mono<Void> validateApplication(String application) {
    if (!StringUtils.hasText(application)) {
      return Mono.error(
          new ApplicationException(Message.Application.ERROR, "Application is required"));
    }
    return ingestionService
        .listApplications()
        .flatMap(
            applications ->
                applications.contains(application)
                    ? Mono.<Void>empty()
                    : Mono.error(
                        new ApplicationException(
                            Message.Application.ERROR, "Unknown application: " + application)));
  }

  private Mono<Void> persistTurns(
      String sessionId, String sub, String question, AnswerResponse answer) {
    AssistantTurn userTurn =
        new AssistantTurn(AssistantTurn.Role.USER, question, null, null, ZonedDateTime.now());
    AssistantTurn assistantTurn =
        new AssistantTurn(
            AssistantTurn.Role.ASSISTANT,
            answer.getAnswer(),
            answer.getSources(),
            answer.getImages(),
            ZonedDateTime.now());

    return assistantSessionService
        .appendTurn(sessionId, sub, userTurn)
        .then(assistantSessionService.appendTurn(sessionId, sub, assistantTurn));
  }
}
