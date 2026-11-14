package com.takypok.contentservice.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.takypok.contentservice.model.entity.ContentItem;
import com.takypok.core.model.authentication.User;

/**
 * What the public page response adds to a page's stored data (G11): a summary when none was written
 * (the first 55 words of the hero subtitle), and the creator's name when the editor ticked "show
 * the author".
 */
public final class PageExtras {
  private static final int SUMMARY_WORDS = 55;

  private PageExtras() {}

  public static JsonNode apply(ContentItem item, JsonNode data) {
    if (data == null || !data.isObject()) return data;
    ObjectNode out = ((ObjectNode) data).deepCopy();
    if (out.path("summary").asText("").isBlank()) {
      String summary = firstWords(out.path("heroSubtitle").asText(""), SUMMARY_WORDS);
      if (!summary.isEmpty()) out.put("summary", summary);
    }
    if (out.path("showAuthor").asBoolean(false) && item.getCreatedBy() != null) {
      User who = item.getCreatedBy();
      String name =
          who.getName() != null && !who.getName().isBlank() ? who.getName() : who.getSub();
      if (name != null && !name.isBlank()) out.put("authorName", name);
    }
    return out;
  }

  static String firstWords(String text, int max) {
    String[] words = text.trim().split("\\s+");
    if (words.length == 1 && words[0].isEmpty()) return "";
    if (words.length <= max) return String.join(" ", words);
    return String.join(" ", java.util.Arrays.copyOf(words, max)) + "\u2026";
  }
}
