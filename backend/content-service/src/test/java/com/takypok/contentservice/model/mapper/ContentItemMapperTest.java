package com.takypok.contentservice.model.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.takypok.contentservice.model.content.ContentFields;
import com.takypok.contentservice.model.content.types.AboutPageFields;
import com.takypok.contentservice.model.content.types.FaqFields;
import com.takypok.contentservice.model.content.types.IndustryPageFields;
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
 * jakarta.validation.Validator both work standalone, and the mapper doesn't touch the DB.
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

  @Test
  void industryPageKeepsNestedGroupsAndSlidesAndDropsUnknownFields() throws Exception {
    ContentItemMapper mapper =
        mapperWithRegistry(Map.of("industry_page", IndustryPageFields.class));
    JsonNode input =
        objectMapper.readTree(
            """
            {"slug": "bfsi", "navLabel": "Banking", "heroTitle": "Hero", "bogus": 1,
            "solutionGroups": [{"name": "Core Banking", "slides": [
              {"tabLabel": "Temenos", "title": "T", "extra": true}]}]}
            """);

    StepVerifier.create(mapper.resolveAndValidate("industry_page", input))
        .assertNext(
            result -> {
              assertEquals("Core Banking", result.at("/solutionGroups/0/name").asText());
              assertEquals("Temenos", result.at("/solutionGroups/0/slides/0/tabLabel").asText());
              assertTrue(result.at("/solutionGroups/0/slides/0/extra").isMissingNode());
              assertTrue(result.path("bogus").isMissingNode());
            })
        .verifyComplete();
  }

  @Test
  void industryPageRejectsASlideWithoutATabLabelNamingItsPath() throws Exception {
    ContentItemMapper mapper =
        mapperWithRegistry(Map.of("industry_page", IndustryPageFields.class));
    JsonNode input =
        objectMapper.readTree(
            """
            {"slug": "bfsi", "navLabel": "Banking", "heroTitle": "Hero",
            "solutionGroups": [{"name": "A", "slides": [{"tabLabel": "ok"}, {"title": "no tab"}]}]}
            """);

    StepVerifier.create(mapper.resolveAndValidate("industry_page", input))
        .expectErrorMatches(
            e ->
                e instanceof ApplicationException
                    && e.getMessage().contains("solutionGroups[0].slides[1].tabLabel"))
        .verify();
  }

  @Test
  void industryPageRejectsAMalformedSlug() throws Exception {
    ContentItemMapper mapper =
        mapperWithRegistry(Map.of("industry_page", IndustryPageFields.class));
    JsonNode input =
        objectMapper.readTree(
            "{\"slug\": \"Not A Slug\", \"navLabel\": \"X\", \"heroTitle\": \"H\"}");

    StepVerifier.create(mapper.resolveAndValidate("industry_page", input))
        .expectErrorMatches(
            e -> e instanceof ApplicationException && e.getMessage().contains("slug"))
        .verify();
  }

  @Test
  void aboutPageKeepsLeadersValuesAndGrowthFiguresAndDropsUnknownFields() throws Exception {
    ContentItemMapper mapper = mapperWithRegistry(Map.of("about_page", AboutPageFields.class));
    JsonNode input =
        objectMapper.readTree(
            """
            {"heroTitle": "Hello", "bogus": 1,
            "growthStats": [{"value": "33+", "label": "Years"}],
            "leaders": [{"name": "A", "title": "CEO", "extra": 1}],
            "values": [{"title": "Speed"}]}
            """);

    StepVerifier.create(mapper.resolveAndValidate("about_page", input))
        .assertNext(
            result -> {
              assertEquals("33+", result.at("/growthStats/0/value").asText());
              assertEquals("A", result.at("/leaders/0/name").asText());
              assertEquals("Speed", result.at("/values/0/title").asText());
              assertTrue(result.at("/leaders/0/extra").isMissingNode());
              assertTrue(result.path("bogus").isMissingNode());
            })
        .verifyComplete();
  }

  @Test
  void aboutPageRejectsALeaderWithoutANameNamingItsPath() throws Exception {
    ContentItemMapper mapper = mapperWithRegistry(Map.of("about_page", AboutPageFields.class));
    JsonNode input =
        objectMapper.readTree(
            "{\"heroTitle\": \"Hello\", \"leaders\": [{\"name\": \"A\"}, {\"title\": \"no name\"}]}");

    StepVerifier.create(mapper.resolveAndValidate("about_page", input))
        .expectErrorMatches(
            e -> e instanceof ApplicationException && e.getMessage().contains("leaders[1].name"))
        .verify();
  }

  @Test
  void aboutPageRequiresAHeroTitle() throws Exception {
    ContentItemMapper mapper = mapperWithRegistry(Map.of("about_page", AboutPageFields.class));

    StepVerifier.create(mapper.resolveAndValidate("about_page", objectMapper.readTree("{}")))
        .expectErrorMatches(
            e -> e instanceof ApplicationException && e.getMessage().contains("heroTitle"))
        .verify();
  }

  @Test
  void aboutPageAcceptsTheLibraryGrowthSourceAndRejectsAnythingElse() throws Exception {
    ContentItemMapper mapper = mapperWithRegistry(Map.of("about_page", AboutPageFields.class));

    StepVerifier.create(
            mapper.resolveAndValidate(
                "about_page",
                objectMapper.readTree("{\"heroTitle\": \"H\", \"growthSource\": \"library\"}")))
        .assertNext(result -> assertEquals("library", result.get("growthSource").asText()))
        .verifyComplete();

    StepVerifier.create(
            mapper.resolveAndValidate(
                "about_page",
                objectMapper.readTree("{\"heroTitle\": \"H\", \"growthSource\": \"bogus\"}")))
        .expectErrorMatches(
            e -> e instanceof ApplicationException && e.getMessage().contains("growthSource"))
        .verify();
  }

  @Test
  void aSeoTitleOrDescriptionBeyondTheSafetyLimitIsRejected() throws Exception {
    ContentItemMapper mapper =
        mapperWithRegistry(
            Map.of(
                "post",
                com.takypok.contentservice.model.content.types.PostFields.class,
                "case_study",
                com.takypok.contentservice.model.content.types.CaseStudyFields.class));

    for (String type : new String[] {"post", "case_study"}) {
      JsonNode tooLongTitle =
          objectMapper.readTree("{\"title\": \"T\", \"seoTitle\": \"" + "x".repeat(256) + "\"}");
      StepVerifier.create(mapper.resolveAndValidate(type, tooLongTitle))
          .expectErrorMatches(
              e -> e instanceof ApplicationException && e.getMessage().contains("seoTitle"))
          .verify();
      JsonNode tooLongDescription =
          objectMapper.readTree(
              "{\"title\": \"T\", \"seoDescription\": \"" + "x".repeat(501) + "\"}");
      StepVerifier.create(mapper.resolveAndValidate(type, tooLongDescription))
          .expectErrorMatches(
              e -> e instanceof ApplicationException && e.getMessage().contains("seoDescription"))
          .verify();
    }
  }

  private ContentItemMapper mapperWithRegistry(
      Map<String, Class<? extends ContentFields>> registry) {
    return new ContentItemMapper(objectMapper, validator, registry);
  }
}
