package com.takypok.contentservice.util;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.takypok.contentservice.model.entity.ContentItem;
import org.junit.jupiter.api.Test;
import org.springframework.dao.OptimisticLockingFailureException;

class StaleEditTest {
  private ContentItem itemAtVersion(int version) {
    ContentItem item = new ContentItem();
    item.setId(7L);
    item.setVersion(version);
    return item;
  }

  @Test
  void aFormLoadedAtTheCurrentVersionMaySave() {
    assertDoesNotThrow(() -> StaleEdit.check(itemAtVersion(3), 3));
  }

  @Test
  void aFormLoadedBeforeSomeoneElsesSaveIsRejected() {
    assertThrows(
        OptimisticLockingFailureException.class, () -> StaleEdit.check(itemAtVersion(4), 3));
  }

  @Test
  void aRequestWithoutAVersionIsNotChecked() {
    assertDoesNotThrow(() -> StaleEdit.check(itemAtVersion(4), null));
  }
}
