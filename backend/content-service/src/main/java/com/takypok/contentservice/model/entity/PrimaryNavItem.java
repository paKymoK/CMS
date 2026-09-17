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
@Table("primary_nav_item")
public class PrimaryNavItem extends IdEntity {
  private Long siteId;
  private Boolean hasDropdown;
  private String label;
  private Integer displayOrder;
  private Boolean active;
  private String status;
  @Version private Integer version;
}
