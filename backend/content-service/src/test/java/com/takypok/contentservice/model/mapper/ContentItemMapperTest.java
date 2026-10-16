package com.takypok.contentservice.model.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.takypok.contentservice.model.content.ContentFields;
import com.takypok.contentservice.model.content.types.FaqFields;
import com.takypok.core.config.ConfigObjectMapper;
import com.takypok.core.exception.ApplicationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;
import reactor.test.StepVerifier;

/**
 * No Spring context needed — ContentFieldsRegistryConfig's classpath scan and
 * jakarta.validation.Validator both work standalone, and the mapper no longer touches the DB.
 */
class ContentItemMapperTest {
  private final ObjectMapper objectMapper = ConfigObjectMapper.objectMapper();
  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  void reflectionsScanDiscoversFaqFieldsUnderItsContentTypeKey() {
    Reflections reflections = new Reflections("com.takypok.contentservice");
    boolean found =
        reflections.getSubTypesOf(ContentFields.class).stream()
            .anyMatch(c -> c.equals(FaqFields.class));
    assertTrue(
        found,
        "FaqFields should be discoverable by the same scan ContentFieldsRegistryConfig runs");
  }

  @Test
  void registeredTypeAcceptsValidDataAndDropsUnknownFields() {
    ContentItemMapper mapper = mapperWithRegistry(Map.of("faq", FaqFields.class));
    JsonNode input =
        objectMapper
            .createObjectNode()
            .put("question", "What is this?")
            .put("answer", "A generic content item.")
            .put("unexpectedField", "should be dropped");

    StepVerifier.create(mapper.resolveAndValidate("faq", input))
        .assertNext(
            result -> {
              assertEquals("What is this?", result.get("question").asText());
              assertEquals("A generic content item.", result.get("answer").asText());
              assertTrue(
                  result.get("unexpectedField") == null,
                  "unknown fields must not survive re-serialization through the type's class");
            })
        .verifyComplete();
  }

  @Test
  void registeredTypeRejectsMissingRequiredField() {
    ContentItemMapper mapper = mapperWithRegistry(Map.of("faq", FaqFields.class));
    JsonNode input = objectMapper.createObjectNode().put("question", "What is this?");
    // no "answer" — FaqFields.answer is @NotBlank

    StepVerifier.create(mapper.resolveAndValidate("faq", input))
        .expectErrorMatches(
            e -> e instanceof ApplicationException && e.getMessage().contains("answer"))
        .verify();
  }

  @Test
  void unregisteredContentTypeIsRejected() {
    ContentItemMapper mapper = mapperWithRegistry(Map.of());

    StepVerifier.create(
            mapper.resolveAndValidate("does-not-exist", objectMapper.createObjectNode()))
        .expectErrorMatches(
            e ->
                e instanceof ApplicationException
                    && e.getMessage().contains("Unknown content type"))
        .verify();
  }

  private ContentItemMapper mapperWithRegistry(
      Map<String, Class<? extends ContentFields>> registry) {
    return new ContentItemMapper(objectMapper, validator, registry);
  }
}
