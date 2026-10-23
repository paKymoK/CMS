package com.takypok.contentservice.util;

import com.takypok.core.exception.ApplicationException;
import com.takypok.core.model.Message;
import java.util.Set;

/**
 * Which statuses a content type may be in. Every type can be DRAFT or PUBLISHED; posts can also be
 * ARCHIVED (taken off the site but kept). The table accepts ARCHIVED for any row, so this is what
 * stops, say, a service page from being archived through the API.
 */
public final class StatusRules {
  private static final Set<String> BASIC = Set.of("DRAFT", "PUBLISHED");
  private static final Set<String> WITH_ARCHIVE = Set.of("DRAFT", "PUBLISHED", "ARCHIVED");
  private static final Set<String> TYPES_WITH_ARCHIVE = Set.of("post");

  private StatusRules() {}

  /** Throws unless {@code status} (null means "leave it as it is") is allowed for the type. */
  public static void requireAllowed(String contentType, String status) {
    if (status == null) return;
    Set<String> allowed = TYPES_WITH_ARCHIVE.contains(contentType) ? WITH_ARCHIVE : BASIC;
    if (!allowed.contains(status)) {
      throw new ApplicationException(
          Message.Application.ERROR,
          "status: " + status + " is not available for this content type");
    }
  }
}
