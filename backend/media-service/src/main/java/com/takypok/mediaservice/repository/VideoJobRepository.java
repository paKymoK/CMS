package com.takypok.mediaservice.repository;

import com.takypok.mediaservice.model.VideoJob;
import java.time.Instant;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface VideoJobRepository extends R2dbcRepository<VideoJob, String> {
  Mono<Void> deleteByVideoId(String videoId);

  @Modifying
  @Query(
      "UPDATE video_job SET status = 'PROCESSING', started_at = :startedAt WHERE job_id = :jobId")
  Mono<Integer> markProcessing(String jobId, Instant startedAt);

  @Modifying
  @Query("UPDATE video_job SET status = 'DONE', completed_at = :completedAt WHERE job_id = :jobId")
  Mono<Integer> markDone(String jobId, Instant completedAt);

  @Modifying
  @Query(
      "UPDATE video_job SET status = 'FAILED', error_message = :errorMessage, completed_at ="
          + " :completedAt WHERE job_id = :jobId")
  Mono<Integer> markFailed(String jobId, String errorMessage, Instant completedAt);
}
