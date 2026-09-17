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
@Table("office")
public class Office extends IdEntity {
  private Long siteId;
  private Double lat;
  private Double lon;
  private String flagColor;
  private Boolean big;
  private String city;
  private String address;
  private Integer displayOrder;
  private Boolean active;
  private String status;
  @Version private Integer version;
}
