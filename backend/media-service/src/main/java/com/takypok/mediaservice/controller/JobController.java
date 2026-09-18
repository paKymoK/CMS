package com.takypok.mediaservice.controller;

import com.takypok.mediaservice.config.CmsAdminGuard;
import com.takypok.mediaservice.model.JobStatus;
import com.takypok.mediaservice.model.JobStatusResponse;
import com.takypok.mediaservice.model.VideoJob;
import com.takypok.mediaservice.service.TranscodeJobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/v1/jobs")
public class JobController {

  private final TranscodeJobService transcodeJobService;
  private final CmsAdminGuard cmsAdminGuard;

  @GetMapping("/{jobId}")
  public Mono<ResponseEntity<JobStatusResponse>> getStatus(
      @PathVariable String jobId, @RequestParam String site, Authentication authentication) {
    return cmsAdminGuard
        .requireSiteAccess(authentication, site)
        .then(transcodeJobService.getJob(jobId, site))
        .map(job -> ResponseEntity.ok(toResponse(job)))
        .defaultIfEmpty(ResponseEntity.notFound().build());
  }

  private JobStatusResponse toResponse(VideoJob job) {
    String hlsUrl =
        job.getStatus() == JobStatus.DONE
            ? "/v1/videos/" + job.getVideoId() + "/master.m3u8"
            : null;
    return new JobStatusResponse(
        job.getJobId(),
        job.getVideoId(),
        job.getStatus(),
        job.getErrorMessage(),
        job.getCreatedAt(),
        job.getCompletedAt(),
        hlsUrl);
  }
}
