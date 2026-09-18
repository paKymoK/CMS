package com.takypok.chatservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.takypok.chatservice.config.Sites;
import com.takypok.chatservice.model.HomeContentDto;
import com.takypok.core.exception.ApplicationException;
import com.takypok.core.model.Message;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.grpc.Points.Filter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * Re-ingests one site's PUBLISHED content from content-service's public GET /v1/home into that
 * site's own Qdrant collection — replaces the old CRM version's classpath documents/ folder as the
 * knowledge source. content-service resolves the site from the request's Host header (there's no
 * gateway to preserve a real subdomain here), so this sets that header explicitly per site rather
 * than adding a second, admin-only content-service endpoint just for this.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "ai.rag.enabled", havingValue = "true", matchIfMissing = false)
public class SiteIngestionService {

  private final WebClient.Builder webClientBuilder;
  private final Map<String, VectorStore> siteVectorStores;
  private final QdrantClient qdrantClient;
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Value("${content-service.base-url}")
  private String contentServiceBaseUrl;

  public Mono<Integer> ingest(String site) {
    VectorStore vectorStore = siteVectorStores.get(site);
    String subdomain = Sites.SUBDOMAIN.get(site);
    if (vectorStore == null || subdomain == null) {
      return Mono.error(
          new ApplicationException(Message.Application.ERROR, "Unknown site: " + site));
    }

    return fetchHomeContent(subdomain)
        .map(this::toDocuments)
        .flatMap(
            docs ->
                Mono.fromRunnable(
                        () -> {
                          nukeCollection(site);
                          vectorStore.add(docs);
                        })
                    .subscribeOn(Schedulers.boundedElastic())
                    .thenReturn(docs.size()));
  }

  private Mono<HomeContentDto> fetchHomeContent(String subdomain) {
    return webClientBuilder
        .build()
        .get()
        .uri(contentServiceBaseUrl + "/v1/home")
        .header(HttpHeaders.HOST, subdomain)
        .retrieve()
        .bodyToMono(JsonNode.class)
        .map(
            envelope -> {
              JsonNode data = envelope.path("data");
              return objectMapper.convertValue(data, HomeContentDto.class);
            });
  }

  private void nukeCollection(String site) {
    try {
      qdrantClient.deleteAsync("cms_" + site, Filter.getDefaultInstance()).get();
    } catch (Exception e) {
      log.warn("No existing vectors to clear for site {}: {}", site, e.getMessage());
    }
  }

  private List<Document> toDocuments(HomeContentDto content) {
    List<Document> docs = new ArrayList<>();

    for (var s : nullSafe(content.stats())) {
      docs.add(
          doc(
              "stat:" + s.label(),
              "Company stat — %s: %s (%s)".formatted(s.label(), s.value(), nullSafeStr(s.note()))));
    }
    for (var s : nullSafe(content.services())) {
      docs.add(doc("service:" + s.name(), "CMC Global offers a service called: " + s.name()));
    }
    for (var o : nullSafe(content.offices())) {
      docs.add(
          doc(
              "office:" + o.city(),
              "CMC Global has an office in %s, located at %s.".formatted(o.city(), o.address())));
    }
    for (var c : nullSafe(content.caseStudies())) {
      docs.add(
          doc(
              "case-study:" + c.title(),
              "Case study \"%s\" (category: %s, date: %s)."
                  .formatted(c.title(), nullSafeStr(c.category()), nullSafeStr(c.date()))));
    }
    for (var i : nullSafe(content.insights())) {
      docs.add(doc("insight:" + i.title(), "Article/insight: " + i.title()));
    }
    for (var a : nullSafe(content.awards())) {
      docs.add(
          doc("award:" + a.name(), "CMC Global has received the award/recognition: " + a.name()));
    }
    for (var c : nullSafe(content.certifications())) {
      docs.add(doc("certification:" + c.name(), "CMC Global holds the certification: " + c.name()));
    }
    for (var p : nullSafe(content.partners())) {
      docs.add(doc("partner:" + p.name(), "CMC Global's partners include: " + p.name()));
    }
    for (var t : nullSafe(content.testimonials())) {
      docs.add(
          doc(
              "testimonial:" + t.name(),
              "Testimonial from %s, %s at %s: \"%s\""
                  .formatted(
                      t.name(), nullSafeStr(t.title()), nullSafeStr(t.company()), t.quote())));
    }
    return docs;
  }

  private Document doc(String source, String text) {
    return new Document(text, Map.of("source", source));
  }

  private <T> List<T> nullSafe(List<T> list) {
    return list == null ? List.of() : list;
  }

  private String nullSafeStr(String value) {
    return value == null ? "" : value;
  }
}
