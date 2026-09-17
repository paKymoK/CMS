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
@Table("post")
public class Post extends IdEntity {
  private Long siteId;
  private String category;
  private String image;
  private String title;
  private String excerpt;
  private String date;
  private Integer displayOrder;
  private Boolean active;
  private String status;
  @Version private Integer version;
}
