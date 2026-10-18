package com.takypok.contentservice.search;

import com.fasterxml.jackson.databind.JsonNode;
import java.text.Normalizer;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;

/**
 * Turns one content item's {@code data} into the three text columns the full-text index is built
 * from (see content_item.search_title/_meta/_body): the title, then the words that say what the
 * item is (category, tags, summary…), then everything else it says.
 *
 * <p>It is deliberately generic — there is one extractor for every content type, driven by field
 * names, so a new {@code ContentFields} class is searchable without touching this. Anything that is
 * a path, a URL, an id or a colour (images, links, slugs…) is skipped, rich text is reduced to its
 * plain text, and accents are folded so "Hà Nội" is found by "ha noi".
 */
public final class SearchTextExtractor {
  /** The title, in order of preference. */
  private static final List<String> TITLE_KEYS =
      List.of("title", "name", "navLabel", "heroTitle", "heading", "label", "question");

  /** Short descriptive fields: weighted below the title, above the body. */
  private static final List<String> META_KEYS =
      List.of(
          "category",
          "tags",
          "excerpt",
          "summary",
          "shortLabel",
          "heroSubtitle",
          "subtitle",
          "company",
          "industry",
          "authorName");

  /** Fields that are technical rather than readable: never indexed. */
  private static final Pattern SKIPPED_KEY =
      Pattern.compile(
          "(?i:slug|url|href|image|photo|logo|icon|avatar|background|colou?r|layout|date)$"
              + "|^(?:id|key|from|to)$|(?:Id|Key|From|To)$");

  private static final Pattern COMBINING_MARKS = Pattern.compile("\\p{M}+");
  private static final Pattern WHITESPACE = Pattern.compile("\\s+");
  private static final int MAX_BODY_CHARS = 200_000;

  /** The three search columns for one item. */
  public record SearchText(String title, String meta, String body) {}

  private SearchTextExtractor() {}

  public static SearchText extract(JsonNode data) {
    if (data == null || !data.isObject()) {
      return new SearchText("", "", "");
    }
    String title = "";
    String titleKey = null;
    for (String key : TITLE_KEYS) {
      String value = text(data.get(key));
      if (!value.isBlank()) {
        title = value;
        titleKey = key;
        break;
      }
    }

    StringBuilder meta = new StringBuilder();
    for (String key : META_KEYS) {
      if (!key.equals(titleKey)) {
        append(meta, data.get(key));
      }
    }

    StringBuilder body = new StringBuilder();
    Iterator<Map.Entry<String, JsonNode>> fields = data.fields();
    while (fields.hasNext() && body.length() < MAX_BODY_CHARS) {
      Map.Entry<String, JsonNode> field = fields.next();
      String key = field.getKey();
      if (key.equals(titleKey) || META_KEYS.contains(key)) {
        continue;
      }
      collect(key, field.getValue(), body);
    }

    return new SearchText(fold(title), fold(meta.toString()), fold(body.toString()));
  }

  /** Lower-cases nothing (Postgres does that) but strips accents and collapses whitespace. */
  public static String fold(String text) {
    if (text == null || text.isBlank()) {
      return "";
    }
    String decomposed = Normalizer.normalize(text, Normalizer.Form.NFD);
    String stripped =
        COMBINING_MARKS.matcher(decomposed).replaceAll("").replace('đ', 'd').replace('Đ', 'D');
    return WHITESPACE.matcher(stripped).replaceAll(" ").trim();
  }

  private static void collect(String key, JsonNode node, StringBuilder out) {
    if (node == null || node.isNull() || SKIPPED_KEY.matcher(key).find()) {
      return;
    }
    if (node.isTextual()) {
      append(out, node);
    } else if (node.isArray()) {
      for (JsonNode element : node) {
        if (element.isTextual()) {
          append(out, element);
        } else {
          collect(key, element, out);
        }
      }
    } else if (node.isObject()) {
      Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
      while (fields.hasNext()) {
        Map.Entry<String, JsonNode> field = fields.next();
        collect(field.getKey(), field.getValue(), out);
      }
    }
  }

  private static void append(StringBuilder out, JsonNode node) {
    String value = text(node);
    if (!value.isBlank()) {
      out.append(' ').append(value);
    }
  }

  /**
   * Readable text of a node: strings as they are (rich text reduced to plain text), arrays joined.
   */
  private static String text(JsonNode node) {
    if (node == null || node.isNull()) {
      return "";
    }
    if (node.isArray()) {
      StringBuilder joined = new StringBuilder();
      for (JsonNode element : node) {
        String value = text(element);
        if (!value.isBlank()) {
          joined.append(' ').append(value);
        }
      }
      return joined.toString().trim();
    }
    if (!node.isTextual()) {
      return "";
    }
    String value = node.asText();
    if (value.isBlank() || looksTechnical(value)) {
      return "";
    }
    return value.indexOf('<') >= 0 ? Jsoup.parse(value).text() : value;
  }

  /** A path, a link, an anchor or a hex colour — not something anyone searches for. */
  private static boolean looksTechnical(String value) {
    return value.startsWith("/") || value.startsWith("#") || value.startsWith("http");
  }
}
