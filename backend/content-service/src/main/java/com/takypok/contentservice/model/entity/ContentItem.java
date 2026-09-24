package com.takypok.contentservice.model.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.takypok.core.model.IdEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Generic content row per cms-platform-plan.md's "generic content model" decision.
 *
 * <p>{@code data} is stored as a plain JsonNode (reusing the existing JsonNodeReader/Writer R2DBC
 * converters — no new converter needed) rather than resolved to a concrete {@code ContentFields}
 * class at the persistence layer, unlike Workflow's TicketDetailReader/Writer. That approach embeds
 * a discriminator inside the jsonb blob itself specifically because a column-level {@code
 * Converter<Json, T>} can't see this row's own {@code contentType} column. Here, {@code
 * contentType} and {@code data} are resolved together in {@code ContentItemMapper}, which always
 * has both in hand at once (from the request, or from this same entity) — so there's no need to
 * embed anything inside the JSON to recover which type a row is; the real, already- indexed {@code
 * content_type} column is the only source of truth for that, as it should be. ContentFields
 * resolution + Bean Validation happen there before a row is ever written.
 */
@Getter
@Setter
@ToString(callSuper = true)
@Table("content_item")
public class ContentItem extends IdEntity {
  private Long siteId;
  private String contentType;
  private JsonNode data;
  private Integer displayOrder;
  private Boolean active;
  private String status;
  @Version private Integer version;
}
