package com.takypok.contentservice.service.impl;

import com.takypok.core.exception.ApplicationException;
import com.takypok.core.model.Message;
import java.text.Normalizer;
import java.util.function.Function;
import java.util.regex.Pattern;
import reactor.core.publisher.Mono;

/**
 * URL-slug generation shared by the content types that have a detail page (posts, case studies).
 */
final class Slugs {
  private static final Pattern DIACRITICS = Pattern.compile("\\p{M}");
  private static final Pattern NON_ALPHANUMERIC = Pattern.compile("[^a-z0-9]+");
  private static final int MAX_WORDS = 7;

  private Slugs() {}

  /**
   * Blank slug on create/update → derive one from the title and de-dupe within the site (-2, -3,
   * ...). An explicitly-supplied slug that collides with a *different* item in the same site is
   * rejected outright — silently renaming a slug someone typed on purpose would break whatever link
   * they meant to publish.
   *
   * @param taken whether a slug is already used by another item of the same type and site
   */
  static Mono<String> resolve(
      String requestedSlug, String title, String fallback, Function<String, Mono<Boolean>> taken) {
    if (requestedSlug != null && !requestedSlug.isBlank()) {
      String slug = slugify(requestedSlug, fallback);
      return taken
          .apply(slug)
          .flatMap(
              isTaken ->
                  isTaken
                      ? Mono.error(
                          new ApplicationException(
                              Message.Application.ERROR, "Slug already in use: " + slug))
                      : Mono.just(slug));
    }
    return dedupe(slugify(title, fallback), 1, taken);
  }

  private static Mono<String> dedupe(
      String baseSlug, int attempt, Function<String, Mono<Boolean>> taken) {
    String candidate = attempt == 1 ? baseSlug : baseSlug + "-" + attempt;
    return taken
        .apply(candidate)
        .flatMap(isTaken -> isTaken ? dedupe(baseSlug, attempt + 1, taken) : Mono.just(candidate));
  }

  static String slugify(String text, String fallback) {
    if (text == null) {
      return fallback;
    }
    String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
    String withoutDiacritics = DIACRITICS.matcher(normalized).replaceAll("");
    String slug = NON_ALPHANUMERIC.matcher(withoutDiacritics.toLowerCase()).replaceAll("-");
    slug = slug.replaceAll("^-+|-+$", "");
    String[] words = slug.split("-");
    int wordLimit = Math.min(words.length, MAX_WORDS);
    StringBuilder result = new StringBuilder();
    for (int i = 0; i < wordLimit; i++) {
      if (result.length() > 0) {
        result.append("-");
      }
      result.append(words[i]);
    }
    return result.length() > 0 ? result.toString() : fallback;
  }
}
