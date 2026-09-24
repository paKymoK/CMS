package com.takypok.contentservice.model.entity;

import java.time.ZonedDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A narrow, single-purpose, opaque credential for the token-based draft preview flow — see
 * add-preview-token-table.sql's comment for why it's deliberately not a JWT and not usable anywhere
 * under /v1/admin/**.
 *
 * <p>Deliberately does NOT extend IdEntity/BaseEntity: those add createdBy/modifiedAt/modifiedBy
 * columns the preview_token table was never given — this entity is immutable (refresh rotates, i.e.
 * delete + insert a new row, never updates one in place) and mintedAt already covers what createdAt
 * would.
 */
@Getter
@Setter
@ToString
@Table("preview_token")
public class PreviewToken {
  @Id private Long id;
  private String token;
  private Long siteId;
  private ZonedDateTime mintedAt;
  private ZonedDateTime expiresAt;
}
