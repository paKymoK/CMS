package com.takypok.mediaservice.model.entity;

import com.takypok.core.model.BaseEntity;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Getter
@Setter
@NoArgsConstructor
public class UploadFile extends BaseEntity {
  @Id private UUID id;
  private String name;
  private String extension;
  private String siteId;
  private String title;
  private String altText;
  private String description;
  private Long sizeBytes;
}
