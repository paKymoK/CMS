package com.takypok.contentservice.audit;

import com.fasterxml.jackson.databind.JsonNode;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * One row of the content audit trail (see content_audit in init-schema.sql). Not an IdEntity: it
 * has no created/modified columns of its own — an entry is never modified, and {@code occurredAt}
 * and {@code actor} are the whole point of it.
 */
@Getter
@Setter
@Table("content_audit")
public class ContentAuditEntry {
  @Id private Long id;
  private Long siteId;

  /** The audited item. Not a foreign key: the history outlives a deleted item. */
  private Long itemId;

  private String contentType;

  /** An {@link AuditAction} name. */
  private String action;

  private String title;

  /** The signed-in user, as the JSON stored in created_by/modified_by. */
  private JsonNode actor;

  private String actorName;
  private OffsetDateTime occurredAt;

  /** Names of the fields that differ between the two snapshots (a JSON array of strings). */
  private JsonNode changed;

  /** The item before the change (see audit_snapshot in init-schema.sql); null for a create. */
  private JsonNode beforeData;

  /** The item after the change; null for a delete. */
  private JsonNode afterData;
}
