package com.takypok.mediaservice.service;

import com.takypok.mediaservice.model.JobResponse;
import com.takypok.mediaservice.model.JobStatus;
import com.takypok.mediaservice.model.VideoJob;
import com.takypok.mediaservice.repository.VideoJobRepository;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class TranscodeJobService {

  private final VideoUploadService uploadService;
  private final FfmpegTranscodeService transcodeService;
  private final HlsPackagerService hlsPackagerService;
  private final VideoJobRepository jobRepository;

  public Mono<JobResponse> submitUpload(FilePart filePart) {
    String videoId = UUID.randomUUID().toString();
    String jobId = UUID.randomUUID().toString();

    VideoJob job =
        VideoJob.builder()
            .jobId(jobId)
            .videoId(videoId)
            .status(JobStatus.QUEUED)
            .createdAt(Instant.now())
            .build();

    // Persist QUEUED before touching disk so a poll immediately after this returns
    // never 404s even if the transcode hasn't been picked up by a worker yet.
    return jobRepository
        .save(job)
        .then(uploadService.save(filePart.content(), videoId))
        .doOnSuccess(ignored -> startTranscode(jobId, videoId))
        .thenReturn(
            new JobResponse(
                videoId, jobId, JobStatus.QUEUED, "Upload received, transcoding queued"));
  }

  public Mono<VideoJob> getJob(String jobId) {
    return jobRepository.findById(jobId);
  }

  public Mono<Void> removeJobsForVideo(String videoId) {
    return jobRepository.deleteByVideoId(videoId);
  }

  private void startTranscode(String jobId, String videoId) {
    markProcessing(jobId)
        .then(transcodeService.transcode(videoId))
        .flatMap(qualities -> hlsPackagerService.writeMasterPlaylist(videoId, qualities))
        .then(markDone(jobId, videoId))
        .onErrorResume(e -> markFailed(jobId, e.getMessage()))
        .subscribe();
  }

  private Mono<Void> markProcessing(String jobId) {
    return jobRepository.markProcessing(jobId, Instant.now()).then();
  }

  private Mono<Void> markDone(String jobId, String videoId) {
    return jobRepository
        .markDone(jobId, Instant.now())
        .doOnNext(count -> log.info("Job {} completed for video {}", jobId, videoId))
        .then();
  }

  private Mono<Void> markFailed(String jobId, String message) {
    return jobRepository
        .markFailed(jobId, message, Instant.now())
        .doOnNext(count -> log.error("Job {} failed: {}", jobId, message))
        .then();
  }
}
