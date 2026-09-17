package com.takypok.chatservice.service;

import com.takypok.chatservice.model.CodeReviewResponse;
import com.takypok.chatservice.util.TextUtils;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "ai.rag.enabled", havingValue = "true", matchIfMissing = false)
public class CodeReviewService {

  // Full-context load of the rule set rather than a vector-store similarity search: with a
  // handful of short anti-pattern docs, everything comfortably fits in num-ctx (8192) alongside a
  // diff, so retrieval would add ingestion/embedding machinery for no accuracy benefit yet.
  // Revisit (real similaritySearch, like AssistantService does against crm_vi) once the rule
  // corpus grows past what fits in context.
  //
  // Loaded via the classpath*: resolver (not ClassPathResource#getFile) because this directory
  // lives inside the packaged jar in every deployed environment, where getFile() has no real
  // filesystem path to return.
  private static final String RULES_LOCATION_PATTERN =
      "classpath:documents/code-review/anti-patterns/*.md";

  private static final String SYSTEM_PROMPT =
      """
      /no_think
      You are a senior Java/Spring Boot code reviewer for a reactive (WebFlux/Project Reactor)
      microservices platform.

      You are given a set of anti-pattern rule documents and a git diff. Review ONLY the lines
      the diff actually adds or changes (lines starting with '+'), using the rule documents as
      your sole source of truth — never invent a rule that isn't listed.

      Output ONLY the final findings below, in the exact format given — no reasoning, no
      deliberation, no "let's check rule X" or "let's re-read rule Y" narration, no weighing of
      alternatives out loud. Decide silently; the response contains nothing but the answer.

      For each rule that is clearly violated, output a finding with:
      - rule: the rule's id (from its front matter)
      - file: the file path from the diff, if identifiable
      - explanation: one or two sentences tied to the specific diff content (not the rule's
        generic description), naming exactly which added line(s) or symbol trigger it

      This is a flag-only review, not a fix-it review — never include a suggested fix, refactor,
      or code change. Only report what's wrong and why; the reader decides how to address it.

      Only cite a rule when the diff's actual code matches that rule's specific detection
      heuristic — a thematic or superficial resemblance (e.g. "this file touches reactive code")
      is not enough on its own. If you are not confident a rule is violated, omit it entirely
      rather than including it with a hedge ("might be", "if this were", "assuming").

      Skip any rule that does not apply — do not list rules the diff doesn't violate. If no
      provided rule is violated, respond with exactly: "No anti-patterns found in the provided
      rule set for this diff." Do not comment on formatting/style (already enforced by Spotless).
      """;

  private final ChatClient chatClient;

  // Rule docs are static for the lifetime of the running jar (they only change on redeploy), so
  // they're read once at startup instead of on every request.
  private List<RuleDoc> rules = List.of();

  @PostConstruct
  void loadRules() {
    rules = readRuleDocs();
  }

  public Mono<CodeReviewResponse> review(String diffText) {
    if (!StringUtils.hasText(diffText)) {
      return Mono.just(
          CodeReviewResponse.builder()
              .review("No diff provided.")
              .rulesConsidered(List.of())
              .build());
    }

    return Mono.fromCallable(
            () -> {
              String rulesContext =
                  rules.stream().map(RuleDoc::content).collect(Collectors.joining("\n\n---\n\n"));

              String userMessage =
                  "## Anti-pattern rules\n"
                      + rulesContext
                      + "\n\n## Diff under review\n```diff\n"
                      + diffText
                      + "\n```";

              String raw =
                  chatClient
                      .prompt()
                      .system(SYSTEM_PROMPT)
                      .options(OllamaChatOptions.builder().disableThinking().build())
                      .user(userMessage)
                      .call()
                      .content();

              return CodeReviewResponse.builder()
                  .review(TextUtils.stripThinkingTokens(raw))
                  .rulesConsidered(rules.stream().map(RuleDoc::name).toList())
                  .build();
            })
        .subscribeOn(Schedulers.boundedElastic());
  }

  private record RuleDoc(String name, String content) {}

  private List<RuleDoc> readRuleDocs() {
    try {
      ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
      Resource[] resources = resolver.getResources(RULES_LOCATION_PATTERN);
      return Arrays.stream(resources)
          .filter(r -> !"README.md".equalsIgnoreCase(r.getFilename()))
          .sorted(Comparator.comparing(Resource::getFilename))
          .map(this::readResourceQuietly)
          .filter(doc -> StringUtils.hasText(doc.content()))
          .toList();
    } catch (IOException e) {
      log.error("Failed to list code-review rule docs: {}", e.getMessage(), e);
      return List.of();
    }
  }

  private RuleDoc readResourceQuietly(Resource resource) {
    try (var in = resource.getInputStream()) {
      return new RuleDoc(
          resource.getFilename(), new String(in.readAllBytes(), StandardCharsets.UTF_8));
    } catch (IOException e) {
      log.warn("Failed to read rule doc {}: {}", resource.getFilename(), e.getMessage());
      return new RuleDoc(resource.getFilename(), "");
    }
  }
}
