package com.takypok.contentservice.model.mapper;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.takypok.contentservice.model.content.ContentFields;
import com.takypok.contentservice.model.content.types.FaqFields;
import com.takypok.contentservice.model.entity.ContentType;
import com.takypok.contentservice.repository.ContentTypeRepository;
import com.takypok.core.config.ConfigObjectMapper;
import com.takypok.core.exception.ApplicationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reflections.Reflections;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * No Spring context needed for the parts that don't touch the DB — ContentFieldsRegistryConfig's
 * classpath scan and jakarta.validation.Validator both work standalone. ContentTypeRepository is
 * mocked since it's an R2dbcRepository (too many inherited methods to hand-write a fake) and
 * exercising a real Postgres round-trip belongs in an integration test, not this unit test.
 */
@ExtendWith(MockitoExtension.class)
class ContentItemMapperTest {
  private final ObjectMapper objectMapper = ConfigObjectMapper.objectMapper();
  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @Mock private ContentTypeRepository contentTypeRepository;

  private ContentType activeFaqType;

  @BeforeEach
  void setUp() {
    activeFaqType = new ContentType();
    activeFaqType.setKey("faq");
    activeFaqType.setActive(true);
  }

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
  void strictTypeAcceptsValidDataAndDropsUnknownFields() {
    when(contentTypeRepository.findByKey("faq")).thenReturn(Mono.just(activeFaqType));
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
              org.junit.jupiter.api.Assertions.assertEquals(
                  "What is this?", result.get("question").asText());
              org.junit.jupiter.api.Assertions.assertEquals(
                  "A generic content item.", result.get("answer").asText());
              assertTrue(
                  result.get("unexpectedField") == null,
                  "unknown fields must not survive re-serialization through the strict class");
            })
        .verifyComplete();
  }

  @Test
  void strictTypeRejectsMissingRequiredField() {
    when(contentTypeRepository.findByKey("faq")).thenReturn(Mono.just(activeFaqType));
    ContentItemMapper mapper = mapperWithRegistry(Map.of("faq", FaqFields.class));
    JsonNode input = objectMapper.createObjectNode().put("question", "What is this?");
    // no "answer" — FaqFields.answer is @NotBlank

    StepVerifier.create(mapper.resolveAndValidate("faq", input))
        .expectErrorMatches(
            e -> e instanceof ApplicationException && e.getMessage().contains("answer"))
        .verify();
  }

  @Test
  void unregisteredContentTypeStillFallsBackToGenericOnceTypeExists() {
    ContentType notYetStrictType = new ContentType();
    notYetStrictType.setKey("not-yet-a-real-type");
    notYetStrictType.setActive(true);
    when(contentTypeRepository.findByKey("not-yet-a-real-type"))
        .thenReturn(Mono.just(notYetStrictType));
    ContentItemMapper mapper = mapperWithRegistry(Map.of());
    JsonNode input =
        objectMapper.createObjectNode().put("whatever", "no strict class registered yet");

    StepVerifier.create(mapper.resolveAndValidate("not-yet-a-real-type", input))
        .assertNext(
            result ->
                org.junit.jupiter.api.Assertions.assertEquals(
                    "no strict class registered yet", result.get("whatever").asText()))
        .verifyComplete();
  }

  @Test
  void unknownContentTypeIsRejectedWithAFriendlyErrorBeforeAnyValidation() {
    when(contentTypeRepository.findByKey("does-not-exist")).thenReturn(Mono.empty());
    ContentItemMapper mapper = mapperWithRegistry(Map.of());

    StepVerifier.create(
            mapper.resolveAndValidate("does-not-exist", objectMapper.createObjectNode()))
        .expectErrorMatches(
            e ->
                e instanceof ApplicationException
                    && e.getMessage().contains("Unknown content type"))
        .verify();
  }

  @Test
  void inactiveContentTypeIsRejectedWithAFriendlyError() {
    ContentType inactiveType = new ContentType();
    inactiveType.setKey("retired-type");
    inactiveType.setActive(false);
    when(contentTypeRepository.findByKey("retired-type")).thenReturn(Mono.just(inactiveType));
    ContentItemMapper mapper = mapperWithRegistry(Map.of());

    StepVerifier.create(mapper.resolveAndValidate("retired-type", objectMapper.createObjectNode()))
        .expectErrorMatches(
            e -> e instanceof ApplicationException && e.getMessage().contains("not active"))
        .verify();
  }

  private ContentItemMapper mapperWithRegistry(
      Map<String, Class<? extends ContentFields>> registry) {
    return new ContentItemMapper(objectMapper, validator, registry, contentTypeRepository);
  }
}
