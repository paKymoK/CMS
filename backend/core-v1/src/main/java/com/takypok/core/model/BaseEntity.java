package com.takypok.core.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.takypok.core.model.authentication.User;
import java.io.Serializable;
import java.time.ZonedDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

@Getter
@Setter
public class BaseEntity implements Serializable {
  @JsonIgnore @CreatedDate private ZonedDateTime createdAt;
  @JsonIgnore @CreatedBy private User createdBy;
  @JsonIgnore @LastModifiedDate private ZonedDateTime modifiedAt;
  @JsonIgnore @LastModifiedBy private User modifiedBy;
}
