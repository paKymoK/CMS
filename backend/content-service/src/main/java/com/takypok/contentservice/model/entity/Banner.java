package com.takypok.contentservice.model.entity;

import com.takypok.core.model.IdEntity;
import java.time.ZonedDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

/** Not used by the current homepage build — added ready for other pages, per the plan. */
@Getter
@Setter
@ToString(callSuper = true)
@Table("banner")
public class Banner extends IdEntity {
  private Long siteId;
  private String title;
  private String body;
  private String image;
  private String ctaLabel;
  private String ctaUrl;
  private ZonedDateTime activeFrom;
  private ZonedDateTime activeUntil;
  private Integer displayOrder;
  private Boolean active;
  private String status;
  @Version private Integer version;
}
