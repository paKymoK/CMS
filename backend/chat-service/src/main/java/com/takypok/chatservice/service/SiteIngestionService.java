package com.takypok.chatservice.service;

import com.takypok.chatservice.config.Sites;
import com.takypok.core.exception.ApplicationException;
import com.takypok.core.model.Message;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.grpc.Points.Filter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * Re-ingests one site's curated knowledge — classpath:documents/<site>/*.md|*.txt only — into that
 * site's own Qdrant collection, wipe-and-reload. content-service's /v1/home was tried as a second
 * source (structured homepage sections auto-turned into short one-liner Documents) but dropped:
 * that auto-generated chunking wasn't good RAG material, so curated per-site files are the only
 * source for now. Replaces the old CRM version's classpath documents/ folder (a single flat tree,
 * no per-site split) as the knowledge source.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "ai.rag.enabled", havingValue = "true", matchIfMissing = false)
public class SiteIngestionService {

  private final Map<String, VectorStore> siteVectorStores;
  private final QdrantClient qdrantClient;

  // Same shape as the old IngestionService's splitter — chunk boundaries good enough for
  // similarity search without cutting a sentence mid-thought too often.
  private final TokenTextSplitter splitter =
      TokenTextSplitter.builder()
          .withChunkSize(400)
          .withMinChunkSizeChars(60)
          .withMinChunkLengthToEmbed(5)
          .withMaxNumChunks(10000)
          .withKeepSeparator(true)
          .build();

  public Mono<Integer> ingest(String site) {
    VectorStore vectorStore = siteVectorStores.get(site);
    if (vectorStore == null || !Sites.CODES.contains(site)) {
      return Mono.error(
          new ApplicationException(Message.Application.ERROR, "Unknown site: " + site));
    }

    return Mono.fromCallable(
            () -> {
              List<Document> docs = loadStaticDocuments(site);
              nukeCollection(site);
              vectorStore.add(docs);
              return docs.size();
            })
        .subscribeOn(Schedulers.boundedElastic());
  }

  /**
   * Curated per-site markdown/text files under src/main/resources/documents/<site>/ — packaged into
   * the jar, so updating these requires a redeploy, same trade-off the old CRM knowledge base had.
   * Loaded via the classpath*: resolver (not ClassPathResource#getFile) because this directory
   * lives inside the packaged jar in every deployed environment, where getFile() has no real
   * filesystem path to return. A site with no documents/<site>/ folder yet just ingests nothing.
   */
  private List<Document> loadStaticDocuments(String site) {
    try {
      ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
      Resource[] mdResources = resolver.getResources("classpath*:documents/" + site + "/*.md");
      Resource[] txtResources = resolver.getResources("classpath*:documents/" + site + "/*.txt");

      List<Resource> resources =
          Stream.concat(Arrays.stream(mdResources), Arrays.stream(txtResources)).toList();

      List<Document> docs = new ArrayList<>();
      for (Resource resource : resources) {
        String content = readResource(resource);
        if (!StringUtils.hasText(content)) continue;
        Document doc = new Document(content, Map.of("source", resource.getFilename()));
        docs.addAll(splitter.apply(List.of(doc)));
      }
      return docs;
    } catch (IOException e) {
      log.warn("Failed to list static documents for site {}: {}", site, e.getMessage());
      return List.of();
    }
  }

  private String readResource(Resource resource) {
    try (var in = resource.getInputStream()) {
      return new String(in.readAllBytes(), StandardCharsets.UTF_8);
    } catch (IOException e) {
      log.warn("Failed to read document {}: {}", resource.getFilename(), e.getMessage());
      return null;
    }
  }

  private void nukeCollection(String site) {
    try {
      qdrantClient.deleteAsync("cms_" + site, Filter.getDefaultInstance()).get();
    } catch (Exception e) {
      log.warn("No existing vectors to clear for site {}: {}", site, e.getMessage());
    }
  }
}
