package com.takypok.mediaservice.model;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("video_job")
public class VideoJob implements Persistable<String> {
  @Id private String jobId;

  private String videoId;

  private JobStatus status;

  private String errorMessage;

  private Instant createdAt;

  private Instant startedAt;

  private Instant completedAt;

  @Transient @Builder.Default @EqualsAndHashCode.Exclude @ToString.Exclude
  private boolean isNew = true;

  @Override
  public String getId() {
    return jobId;
  }
}
