package com.takypok.contentservice.model.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.takypok.contentservice.model.content.ContentFields;
import com.takypok.contentservice.model.content.types.AboutPageFields;
import com.takypok.contentservice.model.content.types.FaqFields;
import com.takypok.contentservice.model.content.types.IndustryPageFields;
import com.takypok.contentservice.model.content.types.ServicePageFields;
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
            "blocks": [{"id": "s1", "type": "solutions", "props": {"groups": [
              {"name": "Core Banking", "slides": [
                {"tabLabel": "Temenos", "title": "T", "extra": true}]}]}},
              {"id": "i1", "type": "insights", "hidden": true, "props": {"title": "Latest"}}]}
            """);

    StepVerifier.create(mapper.resolveAndValidate("industry_page", input))
        .assertNext(
            result -> {
              assertEquals("solutions", result.at("/blocks/0/type").asText());
              assertEquals("Core Banking", result.at("/blocks/0/props/groups/0/name").asText());
              assertEquals(
                  "Temenos", result.at("/blocks/0/props/groups/0/slides/0/tabLabel").asText());
              assertTrue(result.at("/blocks/0/props/groups/0/slides/0/extra").isMissingNode());
              assertTrue(result.at("/blocks/1/hidden").asBoolean());
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
            "blocks": [{"id": "s1", "type": "solutions", "props": {"groups": [
              {"name": "A", "slides": [{"tabLabel": "ok"}, {"title": "no tab"}]}]}}]}
            """);

    StepVerifier.create(mapper.resolveAndValidate("industry_page", input))
        .expectErrorMatches(
            e ->
                e instanceof ApplicationException
                    && e.getMessage().contains("blocks[0].props.groups[0].slides[1].tabLabel"))
        .verify();
  }

  @Test
  void industryPageRejectsServiceOnlyBlocksAndASecondInsightsStrip() throws Exception {
    ContentItemMapper mapper =
        mapperWithRegistry(Map.of("industry_page", IndustryPageFields.class));
    String head = "\"slug\": \"bfsi\", \"navLabel\": \"B\", \"heroTitle\": \"H\"";
    JsonNode serviceBlock =
        objectMapper.readTree(
            "{" + head + ", \"blocks\": [{\"id\": \"a\", \"type\": \"process\", \"props\": {}}]}");
    StepVerifier.create(mapper.resolveAndValidate("industry_page", serviceBlock))
        .expectErrorMatches(e -> e instanceof ApplicationException)
        .verify();

    JsonNode twoInsights =
        objectMapper.readTree(
            "{"
                + head
                + ", \"blocks\": [{\"id\": \"a\", \"type\": \"insights\", \"props\": {}},"
                + "{\"id\": \"b\", \"type\": \"insights\", \"props\": {}}]}");
    StepVerifier.create(mapper.resolveAndValidate("industry_page", twoInsights))
        .expectErrorMatches(
            e -> e instanceof ApplicationException && e.getMessage().contains("only once"))
        .verify();
  }

  @Test
  void aboutPageAllowsEachSectionOnlyOnce() throws Exception {
    ContentItemMapper mapper = mapperWithRegistry(Map.of("about_page", AboutPageFields.class));
    JsonNode input =
        objectMapper.readTree(
            "{\"heroTitle\": \"H\", \"blocks\": [{\"id\": \"a\", \"type\": \"values\", \"props\": {}},"
                + "{\"id\": \"b\", \"type\": \"values\", \"props\": {}}]}");
    StepVerifier.create(mapper.resolveAndValidate("about_page", input))
        .expectErrorMatches(
            e -> e instanceof ApplicationException && e.getMessage().contains("only once"))
        .verify();

    JsonNode serviceBlock =
        objectMapper.readTree(
            "{\"heroTitle\": \"H\", \"blocks\": [{\"id\": \"a\", \"type\": \"process\", \"props\": {}}]}");
    StepVerifier.create(mapper.resolveAndValidate("about_page", serviceBlock))
        .expectErrorMatches(e -> e instanceof ApplicationException)
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
            {"heroTitle": "Hello", "bogus": 1, "blocks": [
              {"id": "i", "type": "intro", "props": {"growthStats": [{"value": "33+", "label": "Years"}]}},
              {"id": "l", "type": "leaders", "props": {"items": [{"name": "A", "title": "CEO", "extra": 1}]}},
              {"id": "v", "type": "values", "hidden": true, "props": {"items": [{"title": "Speed"}]}}]}
            """);

    StepVerifier.create(mapper.resolveAndValidate("about_page", input))
        .assertNext(
            result -> {
              assertEquals("33+", result.at("/blocks/0/props/growthStats/0/value").asText());
              assertEquals("A", result.at("/blocks/1/props/items/0/name").asText());
              assertEquals("Speed", result.at("/blocks/2/props/items/0/title").asText());
              assertTrue(result.at("/blocks/2/hidden").asBoolean());
              assertTrue(result.at("/blocks/1/props/items/0/extra").isMissingNode());
              assertTrue(result.path("bogus").isMissingNode());
            })
        .verifyComplete();
  }

  @Test
  void aboutPageRejectsALeaderWithoutANameNamingItsPath() throws Exception {
    ContentItemMapper mapper = mapperWithRegistry(Map.of("about_page", AboutPageFields.class));
    JsonNode input =
        objectMapper.readTree(
            "{\"heroTitle\": \"Hello\", \"blocks\": [{\"id\": \"l\", \"type\": \"leaders\","
                + " \"props\": {\"items\": [{\"name\": \"A\"}, {\"title\": \"no name\"}]}}]}");

    StepVerifier.create(mapper.resolveAndValidate("about_page", input))
        .expectErrorMatches(
            e ->
                e instanceof ApplicationException
                    && e.getMessage().contains("blocks[0].props.items[1].name"))
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
                objectMapper.readTree(
                    "{\"heroTitle\": \"H\", \"blocks\": [{\"id\": \"i\", \"type\": \"intro\","
                        + " \"props\": {\"growthSource\": \"library\"}}]}")))
        .assertNext(
            result -> assertEquals("library", result.at("/blocks/0/props/growthSource").asText()))
        .verifyComplete();

    StepVerifier.create(
            mapper.resolveAndValidate(
                "about_page",
                objectMapper.readTree(
                    "{\"heroTitle\": \"H\", \"blocks\": [{\"id\": \"i\", \"type\": \"intro\","
                        + " \"props\": {\"growthSource\": \"bogus\"}}]}")))
        .expectErrorMatches(
            e -> e instanceof ApplicationException && e.getMessage().contains("growthSource"))
        .verify();
  }

  private static final String SERVICE_HEAD =
      "\"slug\": \"web\", \"category\": \"eng\", \"navLabel\": \"Web\", \"heroTitle\": \"H\"";

  @Test
  void servicePageKeepsTypedBlocksInOrderAndDropsUnknownProps() throws Exception {
    ContentItemMapper mapper = mapperWithRegistry(Map.of("service_page", ServicePageFields.class));
    JsonNode input =
        objectMapper.readTree(
            "{"
                + SERVICE_HEAD
                + ", \"blocks\": ["
                + "{\"id\": \"b1\", \"type\": \"reasons\", \"hidden\": true, \"props\": {\"title\": \"Why\","
                + " \"items\": [{\"title\": \"R\", \"bogus\": 1}], \"extra\": 2}},"
                + "{\"id\": \"b2\", \"type\": \"industries\", \"props\": {}},"
                + "{\"id\": \"b3\", \"type\": \"capabilities\", \"props\": {\"items\": [{\"title\": \"C\"}]}}]}");

    StepVerifier.create(mapper.resolveAndValidate("service_page", input))
        .assertNext(
            result -> {
              assertEquals("reasons", result.at("/blocks/0/type").asText());
              assertTrue(result.at("/blocks/0/hidden").asBoolean());
              assertEquals("R", result.at("/blocks/0/props/items/0/title").asText());
              assertTrue(result.at("/blocks/0/props/extra").isMissingNode());
              assertTrue(result.at("/blocks/0/props/items/0/bogus").isMissingNode());
              assertEquals("industries", result.at("/blocks/1/type").asText());
              assertTrue(result.at("/blocks/1/props").isObject());
              assertEquals("capabilities", result.at("/blocks/2/type").asText());
            })
        .verifyComplete();
  }

  @Test
  void servicePageRejectsAnUnknownBlockType() throws Exception {
    ContentItemMapper mapper = mapperWithRegistry(Map.of("service_page", ServicePageFields.class));
    JsonNode input =
        objectMapper.readTree(
            "{"
                + SERVICE_HEAD
                + ", \"blocks\": [{\"id\": \"b1\", \"type\": \"carousel\", \"props\": {}}]}");

    StepVerifier.create(mapper.resolveAndValidate("service_page", input))
        .expectErrorMatches(e -> e instanceof ApplicationException)
        .verify();
  }

  @Test
  void servicePageRejectsAnInvalidBlockNamingItsPath() throws Exception {
    ContentItemMapper mapper = mapperWithRegistry(Map.of("service_page", ServicePageFields.class));
    JsonNode input =
        objectMapper.readTree(
            "{"
                + SERVICE_HEAD
                + ", \"blocks\": [{\"id\": \"b1\", \"type\": \"process\","
                + " \"props\": {\"steps\": [{\"image\": \"x\"}]}}]}");

    StepVerifier.create(mapper.resolveAndValidate("service_page", input))
        .expectErrorMatches(
            e ->
                e instanceof ApplicationException
                    && e.getMessage().contains("blocks[0].props.steps[0].title"))
        .verify();
  }

  @Test
  void servicePageAllowsEachSingleUseBlockOnlyOnceAndRequiresUniqueIds() throws Exception {
    ContentItemMapper mapper = mapperWithRegistry(Map.of("service_page", ServicePageFields.class));
    JsonNode twoStories =
        objectMapper.readTree(
            "{"
                + SERVICE_HEAD
                + ", \"blocks\": [{\"id\": \"a\", \"type\": \"stories\", \"props\": {}},"
                + "{\"id\": \"b\", \"type\": \"stories\", \"props\": {}}]}");
    StepVerifier.create(mapper.resolveAndValidate("service_page", twoStories))
        .expectErrorMatches(
            e -> e instanceof ApplicationException && e.getMessage().contains("only once"))
        .verify();

    JsonNode sameId =
        objectMapper.readTree(
            "{"
                + SERVICE_HEAD
                + ", \"blocks\": [{\"id\": \"a\", \"type\": \"process\", \"props\": {}},"
                + "{\"id\": \"a\", \"type\": \"process\", \"props\": {}}]}");
    StepVerifier.create(mapper.resolveAndValidate("service_page", sameId))
        .expectErrorMatches(
            e -> e instanceof ApplicationException && e.getMessage().contains("unique"))
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
