package com.takypok.contentservice.model.entity;

import com.takypok.core.model.IdEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@ToString(callSuper = true)
@Table("testimonial")
public class Testimonial extends IdEntity {
  private Long siteId;
  private String name;
  private String company;
  private String photo;

  /** The client's logo on the quote card (dark artwork, drawn on a light background). */
  private String logo;

  /** Abstract artwork behind the quote text. */
  private String backgroundImage;

  /**
   * Logo variant for the brand card that flanks the neighbouring slides, and that card's gradient.
   */
  private String flankLogo;

  private String flankFrom;
  private String flankTo;

  private String title;
  private String quote;
  private Integer displayOrder;
  private Boolean active;
  private String status;
  @Version private Integer version;
}
