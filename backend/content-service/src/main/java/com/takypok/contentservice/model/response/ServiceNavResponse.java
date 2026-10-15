package com.takypok.contentservice.model.response;

import java.util.List;

/** The header's Services menu: categories in display order, each with its published services. */
public record ServiceNavResponse(List<Category> categories) {
  public record Category(String slug, String name, List<Link> services) {}

  public record Link(String slug, String label) {}
}
