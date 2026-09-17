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
@Table("case_study")
public class CaseStudy extends IdEntity {
  private Long siteId;
  private String date;
  private String image;
  private String title;
  private String category;
  private Integer displayOrder;
  private Boolean active;
  private String status;
  @Version private Integer version;
}
