package com.takypok.contentservice.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.takypok.core.exception.ApplicationException;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class ScheduleRulesTest {
  private static final Instant NOW = Instant.parse("2026-10-25T09:00:30Z");
  private static final Instant LATER = Instant.parse("2026-10-26T09:00:45Z");
  private static final Instant EVEN_LATER = Instant.parse("2026-10-27T09:00:00Z");

  private static ScheduleRules.Times resolve(
      String status, Instant publishAt, Instant unpublishAt, String storedStatus) {
    return ScheduleRules.resolve(status, publishAt, unpublishAt, storedStatus, null, null, NOW);
  }

  @Test
  void draftAndArchivedCarryNoTimes() {
    for (String status : new String[] {"DRAFT", "ARCHIVED"}) {
      ScheduleRules.Times t = resolve(status, LATER, EVEN_LATER, "DRAFT");
      assertNull(t.publishAt());
      assertNull(t.unpublishAt());
    }
  }

  @Test
  void aScheduledPostKeepsItsTimesToTheMinute() {
    ScheduleRules.Times t = resolve("SCHEDULED", LATER, EVEN_LATER, "DRAFT");
    assertEquals(Instant.parse("2026-10-26T09:00:00Z"), t.publishAt());
    assertEquals(EVEN_LATER, t.unpublishAt());
  }

  @Test
  void aScheduledPostNeedsAFuturePublishTime() {
    assertThrows(ApplicationException.class, () -> resolve("SCHEDULED", null, null, "DRAFT"));
    assertThrows(ApplicationException.class, () -> resolve("SCHEDULED", NOW, null, "DRAFT"));
  }

  @Test
  void aPublishTimeAlreadyStoredIsNotCheckedAgainstTheClock() {
    Instant stored = Instant.parse("2026-10-25T09:00:00Z"); // due a moment ago, job not run yet
    ScheduleRules.Times t =
        ScheduleRules.resolve("SCHEDULED", stored, null, "SCHEDULED", stored, null, NOW);
    assertEquals(stored, t.publishAt());
  }

  @Test
  void theUnpublishTimeMustFollowThePublishTime() {
    assertThrows(
        ApplicationException.class, () -> resolve("SCHEDULED", EVEN_LATER, LATER, "DRAFT"));
    assertThrows(ApplicationException.class, () -> resolve("SCHEDULED", LATER, LATER, "DRAFT"));
  }

  @Test
  void aLivePostMayHaveAFutureUnpublishTimeOnly() {
    assertEquals(EVEN_LATER, resolve("PUBLISHED", null, EVEN_LATER, "DRAFT").unpublishAt());
    assertThrows(
        ApplicationException.class,
        () -> resolve("PUBLISHED", null, Instant.parse("2026-10-24T00:00:00Z"), "DRAFT"));
  }

  @Test
  void publishingByHandClearsThePublishTimeButARepeatSaveKeepsIt() {
    Instant wentLive = Instant.parse("2026-10-20T08:00:00Z");
    assertNull(
        ScheduleRules.resolve("PUBLISHED", LATER, null, "SCHEDULED", LATER, null, NOW).publishAt());
    assertEquals(
        wentLive,
        ScheduleRules.resolve("PUBLISHED", null, null, "PUBLISHED", wentLive, null, NOW)
            .publishAt());
  }
}
