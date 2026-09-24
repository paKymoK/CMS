package com.takypok.contentservice.config;

import com.takypok.contentservice.model.annotation.ContentTypeKey;
import com.takypok.contentservice.model.content.ContentFields;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.reflections.Reflections;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Classpath-scans for ContentFields implementations carrying @ContentTypeKey, once at startup —
 * same mechanism as Workflow's TicketConfig, but keyed by the annotation's stable string value
 * instead of exposed as a bare Set the caller has to filter every time. A content_type whose key
 * isn't in this map (including every type still on the flexible tier) falls back to
 * GenericContentFields in ContentDataReader/ContentItemMapper — this registry only needs entries
 * for the strict tier.
 */
@Configuration
public class ContentFieldsRegistryConfig {

  @Bean
  public Map<String, Class<? extends ContentFields>> contentFieldsRegistry(
      ApplicationContext context) {
    Map<String, Object> beans = context.getBeansWithAnnotation(SpringBootApplication.class);
    if (beans.isEmpty()) {
      return Map.of();
    }
    Class<?> mainClass = beans.values().toArray()[0].getClass();
    Reflections reflections = new Reflections(mainClass.getPackageName());
    Set<Class<? extends ContentFields>> discovered = reflections.getSubTypesOf(ContentFields.class);

    Map<String, Class<? extends ContentFields>> registry = new HashMap<>();
    for (Class<? extends ContentFields> clazz : discovered) {
      ContentTypeKey annotation = clazz.getAnnotation(ContentTypeKey.class);
      if (annotation == null) {
        continue;
      }
      Class<? extends ContentFields> existing = registry.putIfAbsent(annotation.value(), clazz);
      if (existing != null) {
        throw new IllegalStateException(
            "Duplicate @ContentTypeKey(\"%s\") on %s and %s"
                .formatted(annotation.value(), existing.getName(), clazz.getName()));
      }
    }
    return registry;
  }
}
