package com.takypok.chatservice.service;

import com.takypok.chatservice.model.AnswerResponse;
import com.takypok.chatservice.model.ImageRef;
import com.takypok.chatservice.model.assistant.AssistantTurn;
import com.takypok.chatservice.util.TextUtils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.document.Document;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "ai.rag.enabled", havingValue = "true", matchIfMissing = false)
public class AssistantService {

  // Matches IngestionService's bucket name for files sitting directly under documents/.
  private static final String UNCATEGORIZED = "uncategorized";

  private final ChatClient chatClient;
  private final VectorStore vectorStore;
  private final FilterExpressionBuilder filterBuilder = new FilterExpressionBuilder();

  private static final Pattern MARKDOWN_IMAGE_PATTERN =
      Pattern.compile("!\\[([^\\]]*)\\]\\(([^)\\s]+)\\)");

  // Markdown docs are authored as one "##" section per topic, and an image always sits under
  // the heading it illustrates — so a section is the natural unit for "was this image relevant".
  private static final Pattern HEADING_PATTERN = Pattern.compile("(?m)^##\\s");

  // Trimmed off both ends of a chunk's text before using it to locate the chunk in the original
  // file: TokenTextSplitter's chunk boundaries don't respect markdown syntax, and can even drop a
  // character during its token encode/decode round-trip right at the split point, so the first/
  // last few characters of a chunk can't be trusted to match the source file verbatim.
  private static final int CHUNK_EDGE_MARGIN = 15;

  // similaritySearch results are ordered best-match-first; only these count toward images.
  private static final int IMAGE_CANDIDATE_LIMIT = 1;

  /**
   * Pulls markdown image references (`![alt](url)`) out of the sections of each retrieved source's
   * full text that were actually retrieved, deduped by URL. Reads each source file's complete,
   * unsplit text from disk rather than matching against the retrieved chunk text directly (see
   * CHUNK_EDGE_MARGIN), then narrows back down to just the "##" section(s) a retrieved chunk's text
   * falls within — otherwise every image in the file would be returned for every question about
   * that file, however unrelated to the actual answer.
   */
  private List<ImageRef> extractImages(List<Document> docs, String application) {
    Map<String, List<Document>> docsBySource =
        docs.stream()
            .collect(
                Collectors.groupingBy(
                    d -> String.valueOf(d.getMetadata().getOrDefault("source", "unknown"))));

    Map<String, ImageRef> images = new LinkedHashMap<>();
    for (Map.Entry<String, List<Document>> entry : docsBySource.entrySet()) {
      String content = readSourceFile(application, entry.getKey());
      if (content == null) continue;

      List<Section> sections = splitIntoSections(content);
      Set<Section> relevantSections = new LinkedHashSet<>();
      for (Document doc : entry.getValue()) {
        String anchor = chunkAnchor(doc);
        if (anchor.isBlank()) continue;
        int start = content.indexOf(anchor);
        if (start < 0) continue;
        int end = start + anchor.length();
        // A ~400-token chunk is often longer than one of this doc's "##" sections, so it can
        // overlap several of them — mark every section the chunk's character range touches.
        for (Section section : sections) {
          if (section.start() < end && start < section.end()) {
            relevantSections.add(section);
          }
        }
      }

      for (Section section : relevantSections) {
        Matcher matcher = MARKDOWN_IMAGE_PATTERN.matcher(section.text());
        while (matcher.find()) {
          String url = matcher.group(2);
          images.putIfAbsent(url, new ImageRef(url, matcher.group(1)));
        }
      }
    }
    return List.copyOf(images.values());
  }

  private record Section(int start, int end, String text) {}

  /** Splits on "##" headings, keeping each heading with the body text that follows it. */
  private List<Section> splitIntoSections(String content) {
    List<Integer> starts = new ArrayList<>();
    Matcher matcher = HEADING_PATTERN.matcher(content);
    while (matcher.find()) {
      starts.add(matcher.start());
    }
    if (starts.isEmpty()) {
      return List.of(new Section(0, content.length(), content));
    }

    List<Section> sections = new ArrayList<>();
    if (starts.get(0) > 0) {
      sections.add(new Section(0, starts.get(0), content.substring(0, starts.get(0))));
    }
    for (int i = 0; i < starts.size(); i++) {
      int start = starts.get(i);
      int end = i + 1 < starts.size() ? starts.get(i + 1) : content.length();
      sections.add(new Section(start, end, content.substring(start, end)));
    }
    return sections;
  }

  private String chunkAnchor(Document doc) {
    String text = doc.getText() == null ? "" : doc.getText().strip();
    if (text.length() <= CHUNK_EDGE_MARGIN * 2) {
      return text;
    }
    return text.substring(CHUNK_EDGE_MARGIN, text.length() - CHUNK_EDGE_MARGIN);
  }

  private String readSourceFile(String application, String fileName) {
    try {
      File root = new ClassPathResource("documents").getFile();
      File searchRoot = UNCATEGORIZED.equals(application) ? root : new File(root, application);
      if (!searchRoot.isDirectory()) return null;

      try (Stream<Path> paths = Files.walk(searchRoot.toPath())) {
        Optional<Path> match =
            paths
                .filter(Files::isRegularFile)
                .filter(path -> path.getFileName().toString().equals(fileName))
                .findFirst();
        if (match.isEmpty()) return null;
        return Files.readString(match.get());
      }
    } catch (IOException e) {
      log.warn(
          "Could not read source file {}/{} for image extraction: {}",
          application,
          fileName,
          e.getMessage());
      return null;
    }
  }

  private List<Message> toSpringAiMessages(List<AssistantTurn> history) {
    return history.stream()
        .map(
            turn ->
                turn.getRole() == AssistantTurn.Role.USER
                    ? (Message) new UserMessage(turn.getContent())
                    : new AssistantMessage(turn.getContent()))
        .toList();
  }

  public Mono<AnswerResponse> ask(
      String question, List<AssistantTurn> history, String application) {
    return Mono.fromCallable(
            () -> {
              var filterExpression = filterBuilder.eq("application", application).build();

              List<Document> docs =
                  vectorStore.similaritySearch(
                      SearchRequest.builder()
                          .query(question)
                          .topK(4)
                          .filterExpression(filterExpression)
                          .build());

              List<String> sources =
                  docs.stream()
                      .map(d -> (String) d.getMetadata().getOrDefault("source", "unknown"))
                      .distinct()
                      .toList();

              // Only the closest matches, not the full topK — similaritySearch's tail entries are
              // often only loosely related, and this doc's sections are short enough that a
              // handful of weakly-relevant chunks would otherwise drag in unrelated images.
              List<ImageRef> images =
                  extractImages(
                      docs.subList(0, Math.min(IMAGE_CANDIDATE_LIMIT, docs.size())), application);

              var prompt =
                  chatClient
                      .prompt()
                      .options(OllamaChatOptions.builder().disableThinking().build())
                      .messages(toSpringAiMessages(history))
                      .user(question);

              String raw =
                  docs.isEmpty()
                      ? prompt.call().content()
                      : prompt
                          .advisors(
                              QuestionAnswerAdvisor.builder(vectorStore)
                                  .searchRequest(
                                      SearchRequest.builder()
                                          .query(question)
                                          .topK(4)
                                          .filterExpression(filterExpression)
                                          .build())
                                  .build())
                          .call()
                          .content();

              return AnswerResponse.builder()
                  .answer(TextUtils.stripThinkingTokens(raw))
                  .sources(sources)
                  .images(images)
                  .build();
            })
        .subscribeOn(Schedulers.boundedElastic());
  }
}
