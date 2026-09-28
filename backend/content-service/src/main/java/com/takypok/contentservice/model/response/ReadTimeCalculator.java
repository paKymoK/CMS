package com.takypok.contentservice.model.response;

/** Derives a "N min read" estimate from a post/case study's rich-text body — never stored. */
public final class ReadTimeCalculator {
  private static final int WORDS_PER_MINUTE = 200;

  private ReadTimeCalculator() {}

  public static int minutesFor(String body) {
    if (body == null || body.isBlank()) {
      return 1;
    }
    String plainText = body.replaceAll("<[^>]*>", " ").trim();
    if (plainText.isEmpty()) {
      return 1;
    }
    int wordCount = plainText.split("\\s+").length;
    return Math.max(1, Math.round(wordCount / (float) WORDS_PER_MINUTE));
  }
}
