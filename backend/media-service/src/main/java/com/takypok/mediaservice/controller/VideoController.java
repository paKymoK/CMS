package com.takypok.mediaservice.controller;

import com.takypok.mediaservice.model.JobResponse;
import com.takypok.mediaservice.service.TranscodeJobService;
import com.takypok.mediaservice.service.VideoStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/v1/videos")
public class VideoController {

  private final TranscodeJobService transcodeJobService;
  private final VideoStorageService storageService;

  @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public Mono<JobResponse> upload(@RequestPart("file") Mono<FilePart> filePart) {
    return filePart.flatMap(transcodeJobService::submitUpload);
  }

  @DeleteMapping("/{videoId}")
  public Mono<Void> delete(@PathVariable String videoId) {
    return storageService
        .deleteVideo(videoId)
        .then(transcodeJobService.removeJobsForVideo(videoId));
  }
}
