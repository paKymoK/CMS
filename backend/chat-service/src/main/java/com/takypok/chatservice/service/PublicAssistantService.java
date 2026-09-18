package com.takypok.chatservice.service;

import com.takypok.chatservice.config.Sites;
import com.takypok.chatservice.model.PublicAnswerResponse;
import com.takypok.chatservice.model.assistant.AssistantTurn;
import com.takypok.core.exception.ApplicationException;
import com.takypok.core.model.Message;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * The public, unauthenticated, per-site marketing assistant — distinct from (and shares no state
 * with) AssistantService, which still backs the pre-existing employee-facing CRM assistant. See
 * VectorStoreConfig for the one-collection-per-site rationale.
 */
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "ai.rag.enabled", havingValue = "true", matchIfMissing = false)
public class PublicAssistantService {

  private static final int TOP_K = 4;

  private final ChatClient publicAssistantChatClient;
  private final Map<String, VectorStore> siteVectorStores;

  public Mono<PublicAnswerResponse> ask(String site, String question, List<AssistantTurn> history) {
    VectorStore vectorStore = siteVectorStores.get(site);
    if (vectorStore == null) {
      return Mono.error(
          new ApplicationException(Message.Application.ERROR, "Unknown site: " + site));
    }

    return Mono.fromCallable(
            () -> {
              SearchRequest searchRequest =
                  SearchRequest.builder().query(question).topK(TOP_K).build();

              List<Document> docs = vectorStore.similaritySearch(searchRequest);
              List<String> sources =
                  docs.stream()
                      .map(d -> String.valueOf(d.getMetadata().getOrDefault("source", "content")))
                      .distinct()
                      .toList();

              var prompt =
                  publicAssistantChatClient
                      .prompt()
                      .system(systemPromptFor(site))
                      .messages(toSpringAiMessages(history))
                      .user(question);

              String answer =
                  docs.isEmpty()
                      ? prompt.call().content()
                      : prompt
                          .advisors(
                              QuestionAnswerAdvisor.builder(vectorStore)
                                  .searchRequest(searchRequest)
                                  .build())
                          .call()
                          .content();

              return PublicAnswerResponse.builder().answer(answer).sources(sources).build();
            })
        .subscribeOn(Schedulers.boundedElastic());
  }

  private String systemPromptFor(String site) {
    String language = Sites.LANGUAGE.getOrDefault(site, "English");
    return """
        You are the public marketing assistant on CMC Global's %s-language website.
        Answer ONLY using the context provided about CMC Global's services, case studies,
        offices, and other published content for this site — never invent information that
        isn't in the provided context.
        Always reply in %s, regardless of what language the visitor writes in.
        If the answer isn't in the provided context, say you don't have that information rather
        than guessing. Keep answers concise and helpful for a prospective client or visitor.
        """
        .formatted(language, language);
  }

  private List<org.springframework.ai.chat.messages.Message> toSpringAiMessages(
      List<AssistantTurn> history) {
    if (history == null) return List.of();
    return history.stream()
        .map(
            turn ->
                turn.getRole() == AssistantTurn.Role.USER
                    ? (org.springframework.ai.chat.messages.Message)
                        new UserMessage(turn.getContent())
                    : new AssistantMessage(turn.getContent()))
        .toList();
  }
}
