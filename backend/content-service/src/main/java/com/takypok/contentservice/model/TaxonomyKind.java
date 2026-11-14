package com.takypok.contentservice.model;

import java.util.Arrays;
import java.util.Optional;

/**
 * The two things a post can be filed under: a category or a tag. Both are content items of their
 * own type.
 */
public enum TaxonomyKind {
  CATEGORY("post_category", "categoryIds", "post-categories", "category"),
  TAG("post_tag", "tagIds", "post-tags", "tag");

  /** The content_item type of the category / tag rows. */
  private final String type;

  /** The key in a post's data that lists the ids of this kind. */
  private final String postField;

  /** The API path segment. */
  private final String path;

  private final String noun;

  TaxonomyKind(String type, String postField, String path, String noun) {
    this.type = type;
    this.postField = postField;
    this.path = path;
    this.noun = noun;
  }

  public String type() {
    return type;
  }

  public String postField() {
    return postField;
  }

  public String path() {
    return path;
  }

  public String noun() {
    return noun;
  }

  public static Optional<TaxonomyKind> fromPath(String path) {
    return Arrays.stream(values()).filter(k -> k.path.equals(path)).findFirst();
  }
}
