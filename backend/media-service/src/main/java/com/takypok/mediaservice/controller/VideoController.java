package com.takypok.mediaservice.controller;

import com.takypok.core.exception.ApplicationException;
import com.takypok.core.model.Message;
import com.takypok.mediaservice.config.CmsAdminGuard;
import com.takypok.mediaservice.model.JobResponse;
import com.takypok.mediaservice.model.VideoJob;
import com.takypok.mediaservice.service.TranscodeJobService;
import com.takypok.mediaservice.service.VideoStorageService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/v1/videos")
public class VideoController {

  private final TranscodeJobService transcodeJobService;
  private final VideoStorageService storageService;
  private final CmsAdminGuard cmsAdminGuard;

  @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public Mono<JobResponse> upload(
      @RequestParam String site,
      @RequestPart("file") Mono<FilePart> filePart,
      Authentication authentication) {
    return cmsAdminGuard
        .requireSiteAccess(authentication, site)
        .then(filePart.flatMap(part -> transcodeJobService.submitUpload(part, site)));
  }

  /** Media library listing for the admin picker (Phase 4) — every video for one site. */
  @GetMapping
  public Mono<List<VideoJob>> list(@RequestParam String site, Authentication authentication) {
    return cmsAdminGuard
        .requireSiteAccess(authentication, site)
        .then(transcodeJobService.listForSite(site));
  }

  @DeleteMapping("/{videoId}")
  public Mono<Void> delete(
      @PathVariable String videoId, @RequestParam String site, Authentication authentication) {
    return cmsAdminGuard
        .requireSiteAccess(authentication, site)
        .then(transcodeJobService.getJobByVideoId(videoId, site))
        .switchIfEmpty(
            Mono.error(
                new ApplicationException(
                    Message.Application.ERROR, "No video " + videoId + " for site " + site)))
        .then(storageService.deleteVideo(videoId))
        .then(transcodeJobService.removeJobsForVideo(videoId));
  }
}
