package com.takypok.contentservice.util;

import com.takypok.core.exception.ApplicationException;
import com.takypok.core.model.Message;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * What a post's publish and unpublish times may be for its status. The database job that acts on
 * them runs once a minute, so times are kept to the minute.
 *
 * <ul>
 *   <li>DRAFT and ARCHIVED carry no times.
 *   <li>SCHEDULED needs a publish time in the future, and may have a later unpublish time.
 *   <li>PUBLISHED may have an unpublish time in the future. Its publish time is only kept as the
 *       record of when the job (or an earlier save) made it live; publishing by hand clears it.
 * </ul>
 *
 * A time that is unchanged from what is stored is not checked against the clock again, so saving an
 * edit in the minute before the job runs is not refused.
 */
public final class ScheduleRules {
  /** The times to store. */
  public record Times(Instant publishAt, Instant unpublishAt) {}

  private ScheduleRules() {}

  public static Times resolve(
      String status,
      Instant requestedPublishAt,
      Instant requestedUnpublishAt,
      String storedStatus,
      Instant storedPublishAt,
      Instant storedUnpublishAt,
      Instant now) {
    Instant publishAt = minute(requestedPublishAt);
    Instant unpublishAt = minute(requestedUnpublishAt);
    switch (status) {
      case "SCHEDULED" -> {
        if (publishAt == null) throw invalid("Choose when the post should be published");
        if (!publishAt.equals(storedPublishAt) && !publishAt.isAfter(now)) {
          throw invalid("The publish time must be in the future");
        }
        requireLater(unpublishAt, publishAt, storedUnpublishAt, now);
        return new Times(publishAt, unpublishAt);
      }
      case "PUBLISHED" -> {
        boolean wasLive = "PUBLISHED".equals(storedStatus);
        requireLater(unpublishAt, null, storedUnpublishAt, now);
        return new Times(wasLive ? storedPublishAt : null, unpublishAt);
      }
      default -> {
        return new Times(null, null);
      }
    }
  }

  private static void requireLater(
      Instant unpublishAt, Instant publishAt, Instant stored, Instant now) {
    if (unpublishAt == null) return;
    if (publishAt != null && !unpublishAt.isAfter(publishAt)) {
      throw invalid("The unpublish time must be after the publish time");
    }
    if (!unpublishAt.equals(stored) && !unpublishAt.isAfter(now)) {
      throw invalid("The unpublish time must be in the future");
    }
  }

  private static Instant minute(Instant t) {
    return t == null ? null : t.truncatedTo(ChronoUnit.MINUTES);
  }

  private static ApplicationException invalid(String message) {
    return new ApplicationException(Message.Application.ERROR, message);
  }
}
