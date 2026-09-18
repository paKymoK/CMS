package com.takypok.chatservice.config;

import java.util.List;
import java.util.Map;

/**
 * Mirrors content-service's seed-sites.sql and admin-app's config/sites.ts. chat-service has no
 * site registry of its own (same reasoning as media-service's CmsAdminGuard) — this is just the
 * fixed, small list needed to size the per-site vector store map and resolve a subdomain for
 * server-to-server content-service calls during ingestion.
 */
public final class Sites {
  private Sites() {}

  public static final List<String> CODES = List.of("en", "vi", "ja", "ko", "de");

  public static final Map<String, String> SUBDOMAIN =
      Map.of(
          "en", "en.cmcglobal.com",
          "vi", "vn.cmcglobal.com",
          "ja", "jp.cmcglobal.com",
          "ko", "kr.cmcglobal.com",
          "de", "de.cmcglobal.com");

  public static final Map<String, String> LANGUAGE =
      Map.of(
          "en", "English",
          "vi", "Vietnamese",
          "ja", "Japanese",
          "ko", "Korean",
          "de", "German");
}
