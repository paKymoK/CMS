package com.takypok.mediaservice.model.entity;

import com.takypok.core.model.BaseEntity;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UploadFile extends BaseEntity {
  @Id private UUID id;
  private String name;
  private String extension;
}
