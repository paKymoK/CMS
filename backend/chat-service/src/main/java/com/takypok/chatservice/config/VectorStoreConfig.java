package com.takypok.chatservice.config;

import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.qdrant.QdrantVectorStore;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Replaces Spring AI's single-collection QdrantVectorStoreAutoConfiguration (excluded in
 * application.yaml) with one QdrantVectorStore PER SITE — per the decision log, a separate
 * collection per site, not one collection filtered by a site/application field. That's what makes
 * cross-site leakage structurally impossible rather than dependent on every query remembering to
 * filter correctly.
 */
@Configuration
@ConditionalOnProperty(name = "ai.rag.enabled", havingValue = "true", matchIfMissing = false)
public class VectorStoreConfig {

  @Value("${spring.ai.vectorstore.qdrant.host}")
  private String host;

  @Value("${spring.ai.vectorstore.qdrant.port}")
  private int port;

  @Value("${spring.ai.vectorstore.qdrant.collection-name}")
  private String legacyCollectionName;

  @Bean
  public QdrantClient qdrantClient() {
    return new QdrantClient(QdrantGrpcClient.newBuilder(host, port, false).build());
  }

  // Keeps the pre-existing CRM assistant (AssistantService/IngestionService, both untouched)
  // working exactly as before — they depend on a single autowired VectorStore by type, which
  // used to come from the now-excluded QdrantVectorStoreAutoConfiguration.
  @Bean
  public VectorStore vectorStore(QdrantClient qdrantClient, EmbeddingModel embeddingModel) {
    return QdrantVectorStore.builder(qdrantClient, embeddingModel)
        .collectionName(legacyCollectionName)
        .initializeSchema(true)
        .build();
  }

  // Each per-site QdrantVectorStore lives inside this Map bean rather than being its own Spring
  // bean, so Spring's InitializingBean lifecycle (which is what initializeSchema(true) actually
  // relies on to create the collection) never fires for them automatically — only the singular
  // "vectorStore" bean above gets that for free by being returned directly from a @Bean method.
  // Call afterPropertiesSet() ourselves so each cms_<site> collection actually gets created,
  // instead of failing NOT_FOUND on first use.
  @Bean
  public Map<String, VectorStore> siteVectorStores(
      QdrantClient qdrantClient, EmbeddingModel embeddingModel) throws Exception {
    Map<String, VectorStore> stores = new LinkedHashMap<>();
    for (String site : Sites.CODES) {
      QdrantVectorStore store =
          QdrantVectorStore.builder(qdrantClient, embeddingModel)
              .collectionName("cms_" + site)
              .initializeSchema(true)
              .build();
      ((InitializingBean) store).afterPropertiesSet();
      stores.put(site, store);
    }
    return Map.copyOf(stores);
  }
}
