package com.takypok.contentservice.util;

import com.takypok.core.exception.ApplicationException;
import com.takypok.core.model.Message;
import java.util.Set;

/**
 * Which statuses a content type may be in. Every type can be DRAFT or PUBLISHED; posts and pages
 * can also be SCHEDULED (goes live by itself at a set time) and ARCHIVED (taken off the site but
 * kept). The table accepts those for any row, so this is what stops, say, a footer item from being
 * archived or scheduled through the API.
 */
public final class StatusRules {
  private static final Set<String> BASIC = Set.of("DRAFT", "PUBLISHED");
  private static final Set<String> POST_STATUSES =
      Set.of("DRAFT", "SCHEDULED", "PUBLISHED", "ARCHIVED");

  /** The types that can also be scheduled and archived: posts and the page types. */
  private static final Set<String> FULL_STATUS_TYPES =
      Set.of("post", "service_page", "industry_page", "about_page");

  private StatusRules() {}

  /** Throws unless {@code status} (null means "leave it as it is") is allowed for the type. */
  public static void requireAllowed(String contentType, String status) {
    if (status == null) return;
    Set<String> allowed = FULL_STATUS_TYPES.contains(contentType) ? POST_STATUSES : BASIC;
    if (!allowed.contains(status)) {
      throw new ApplicationException(
          Message.Application.ERROR,
          "status: " + status + " is not available for this content type");
    }
  }
}
