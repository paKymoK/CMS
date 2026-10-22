package com.takypok.contentservice.search;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class SearchTextExtractorTest {
  private final ObjectMapper mapper = new ObjectMapper();

  private SearchTextExtractor.SearchText extract(String json) throws Exception {
    return SearchTextExtractor.extract(mapper.readTree(json));
  }

  @Test
  void splitsATitleFromTheDescriptiveFieldsAndTheBody() throws Exception {
    var text =
        extract(
            """
            {"title": "Cloud migration", "category": "Cloud", "tags": ["AI", "Banking"],
            "excerpt": "Moving a bank", "body": "<p>We moved <b>every</b> workload.</p>"}
            """);
    assertEquals("Cloud migration", text.title());
    assertTrue(text.meta().contains("Cloud"));
    assertTrue(text.meta().contains("AI Banking"));
    assertTrue(text.meta().contains("Moving a bank"));
    assertEquals("We moved every workload.", text.body());
  }

  @Test
  void fallsBackThroughTheTitleKeysAndNeverRepeatsTheTitleInTheBody() throws Exception {
    var text =
        extract("{\"name\": \"Jane Doe\", \"company\": \"Acme\", \"quote\": \"Great work\"}");
    assertEquals("Jane Doe", text.title());
    assertTrue(text.meta().contains("Acme"));
    assertEquals("Great work", text.body());
    assertFalse(text.body().contains("Jane"));
  }

  @Test
  void skipsPathsLinksIdsAndColours() throws Exception {
    var text =
        extract(
            """
            {"title": "T", "slug": "t", "image": "/media-service/images/a.webp",
            "ctaUrl": "https://x.test", "flankFrom": "#112233", "date": "June 1, 2026",
            "layout": "wide", "id": "abc", "items": [{"label": "One", "icon": "/i.svg"}],
            "note": "/internal/path", "keep": "Readable words"}
            """);
    assertEquals("T", text.title());
    assertEquals("Readable words", text.body().trim().replace("One ", "").trim());
    assertFalse(text.body().contains("media-service"));
    assertFalse(text.body().contains("http"));
    assertFalse(text.body().contains("112233"));
    assertFalse(text.body().contains("June"));
    assertFalse(text.body().contains("internal"));
  }

  @Test
  void keepsWordsThatMerelyEndLikeASkippedKey() throws Exception {
    // "grid" ends in "id", "motto" in "to" — only exact/camel-case id/key/from/to are skipped.
    var text = extract("{\"title\": \"T\", \"grid\": \"Layout grid\", \"motto\": \"Move fast\"}");
    assertTrue(text.body().contains("Layout grid"));
    assertTrue(text.body().contains("Move fast"));
  }

  @Test
  void foldsAccentsSoVietnameseTextIsFoundWithoutThem() throws Exception {
    var text = extract("{\"title\": \"Hà Nội Đà Lạt\", \"summary\": \"Phát triển\"}");
    assertEquals("Ha Noi Da Lat", text.title());
    assertTrue(text.meta().contains("Phat trien"));
  }

  @Test
  void anItemWithNoTextGivesEmptyStringsNotNulls() throws Exception {
    var text = extract("{}");
    assertEquals("", text.title());
    assertEquals("", text.meta());
    assertEquals("", text.body());
    var none = SearchTextExtractor.extract((JsonNode) null);
    assertEquals("", none.title());
  }

  @Test
  void searchEngineTextIsNotIndexedBecauseItOnlyRepeatsTheTitleAndExcerpt() throws Exception {
    var text =
        extract(
            """
            {"title": "Cloud migration", "excerpt": "Moving a bank", "body": "<p>Real body.</p>",
            "seoTitle": "Cloud migration | Brand", "seoDescription": "Marketing blurb for search",
            "seoImage": "/media-service/images/x.webp", "seoNoindex": true}
            """);
    assertEquals("Real body.", text.body());
    assertFalse(text.meta().contains("Brand"));
    assertFalse(text.meta().contains("blurb"));
  }
}
