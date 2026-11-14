package com.takypok.contentservice.util;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.takypok.core.exception.ApplicationException;
import org.junit.jupiter.api.Test;

class StatusRulesTest {
  @Test
  void everyTypeMayBeDraftOrPublished() {
    for (String type : new String[] {"post", "case_study", "service_page", "about_page"}) {
      assertDoesNotThrow(() -> StatusRules.requireAllowed(type, "DRAFT"));
      assertDoesNotThrow(() -> StatusRules.requireAllowed(type, "PUBLISHED"));
    }
  }

  @Test
  void aStatusLeftOutIsNotChecked() {
    assertDoesNotThrow(() -> StatusRules.requireAllowed("service_page", null));
  }

  @Test
  void onlyPostsAndPagesMayBeArchived() {
    for (String type : new String[] {"post", "service_page", "industry_page", "about_page"}) {
      assertDoesNotThrow(() -> StatusRules.requireAllowed(type, "ARCHIVED"));
    }
    for (String type : new String[] {"case_study", "stat", "site_footer"}) {
      assertThrows(ApplicationException.class, () -> StatusRules.requireAllowed(type, "ARCHIVED"));
    }
  }

  @Test
  void onlyPostsAndPagesMayBeScheduled() {
    for (String type : new String[] {"post", "service_page", "industry_page", "about_page"}) {
      assertDoesNotThrow(() -> StatusRules.requireAllowed(type, "SCHEDULED"));
    }
    for (String type : new String[] {"case_study", "stat", "site_footer"}) {
      assertThrows(ApplicationException.class, () -> StatusRules.requireAllowed(type, "SCHEDULED"));
    }
  }

  @Test
  void anUnknownStatusIsRejectedWithItsName() {
    ApplicationException e =
        assertThrows(
            ApplicationException.class, () -> StatusRules.requireAllowed("post", "TRASHED"));
    org.junit.jupiter.api.Assertions.assertTrue(e.getMessage().contains("TRASHED"));
  }
}
