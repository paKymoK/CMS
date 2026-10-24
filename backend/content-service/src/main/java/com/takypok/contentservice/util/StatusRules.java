package com.takypok.contentservice.util;

import com.takypok.core.exception.ApplicationException;
import com.takypok.core.model.Message;
import java.util.Set;

/**
 * Which statuses a content type may be in. Every type can be DRAFT or PUBLISHED; posts can also be
 * SCHEDULED (goes live by itself at a set time) and ARCHIVED (taken off the site but kept). The
 * table accepts those for any row, so this is what stops, say, a service page from being archived
 * or scheduled through the API.
 */
public final class StatusRules {
  private static final Set<String> BASIC = Set.of("DRAFT", "PUBLISHED");
  private static final Set<String> POST_STATUSES =
      Set.of("DRAFT", "SCHEDULED", "PUBLISHED", "ARCHIVED");
  private static final Set<String> POST_ONLY_TYPES = Set.of("post");

  private StatusRules() {}

  /** Throws unless {@code status} (null means "leave it as it is") is allowed for the type. */
  public static void requireAllowed(String contentType, String status) {
    if (status == null) return;
    Set<String> allowed = POST_ONLY_TYPES.contains(contentType) ? POST_STATUSES : BASIC;
    if (!allowed.contains(status)) {
      throw new ApplicationException(
          Message.Application.ERROR,
          "status: " + status + " is not available for this content type");
    }
  }
}
