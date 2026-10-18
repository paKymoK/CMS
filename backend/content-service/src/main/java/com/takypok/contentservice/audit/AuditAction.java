package com.takypok.contentservice.audit;

/** What happened to a content item; the value stored in content_audit.action. */
public enum AuditAction {
  CREATE,
  UPDATE,
  /** Its status went from DRAFT to PUBLISHED (any other change in the same save is listed too). */
  PUBLISH,
  /** Its status went from PUBLISHED back to DRAFT. */
  UNPUBLISH,
  DELETE,
  /** Its content was put back to what an earlier audit entry recorded. */
  RESTORE
}
