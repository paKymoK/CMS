package com.takypok.contentservice.util;

import java.util.List;
import java.util.Set;

/** The content types that are pages (G11): each has its own address family on the website. */
public final class PageTypes {
  public static final String SERVICE = "service_page";
  public static final String INDUSTRY = "industry_page";
  public static final String ABOUT = "about_page";

  public static final List<String> ALL = List.of(SERVICE, INDUSTRY, ABOUT);
  private static final Set<String> SET = Set.copyOf(ALL);

  private PageTypes() {}

  public static boolean is(String contentType) {
    return SET.contains(contentType);
  }

  /** The public address of a page: /services/{category}/{slug}, /industries/{slug} or /about. */
  public static String path(String contentType, String category, String slug) {
    return switch (contentType) {
      case SERVICE -> "/services/" + category + "/" + slug;
      case INDUSTRY -> "/industries/" + slug;
      default -> "/about";
    };
  }

  /**
   * The key of the page's title in its data: the Service and Industry pages' menu label, the About
   * page's headline.
   */
  public static String titleKey(String contentType) {
    return ABOUT.equals(contentType) ? "heroTitle" : "navLabel";
  }
}
