package com.takypok.contentservice.util;

import com.takypok.contentservice.model.entity.ContentItem;
import org.springframework.dao.OptimisticLockingFailureException;

/**
 * Guards an edit made against an out-of-date copy of an item. Updates load the row fresh, so the
 * {@code @Version} column alone never notices a form that was opened before someone else saved: the
 * version the form was loaded with is compared here instead. A request without a version is not
 * checked.
 */
public final class StaleEdit {
  private StaleEdit() {}

  public static void check(ContentItem item, Integer expectedVersion) {
    if (expectedVersion != null && !expectedVersion.equals(item.getVersion())) {
      throw new OptimisticLockingFailureException(
          "Content item " + item.getId() + " is at version " + item.getVersion());
    }
  }
}
