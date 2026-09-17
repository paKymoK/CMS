package com.takypok.mediaservice.controller;

import com.takypok.mediaservice.service.VideoStorageService;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/v1/videos")
public class VideoStreamController {

  private static final MediaType HLS_MIME =
      MediaType.parseMediaType("application/vnd.apple.mpegurl");
  private static final MediaType TS_MIME = MediaType.parseMediaType("video/MP2T");

  // These are VOD playlists/segments (-hls_playlist_type vod) written once by the transcode
  // job and never modified again, so a long, immutable cache is safe — repeat views (very
  // common when scrolling back through chat history) previously re-read from disk every time.
  private static final CacheControl HLS_CACHE_CONTROL =
      CacheControl.maxAge(Duration.ofDays(365)).cachePublic().immutable();

  private final VideoStorageService storageService;

  @GetMapping("/{videoId}/master.m3u8")
  public Mono<ResponseEntity<Resource>> serveMaster(@PathVariable String videoId) {
    return servePlaylistFile(storageService.hlsDir(videoId).resolve("master.m3u8"));
  }

  @GetMapping("/{videoId}/{quality}/index.m3u8")
  public Mono<ResponseEntity<Resource>> servePlaylist(
      @PathVariable String videoId, @PathVariable String quality) {
    return servePlaylistFile(storageService.qualityDir(videoId, quality).resolve("index.m3u8"));
  }

  private static final java.util.regex.Pattern SEGMENT_PATTERN =
      java.util.regex.Pattern.compile("^[a-zA-Z0-9_-]+\\.ts$");

  @GetMapping("/{videoId}/{quality}/{segment}")
  public Mono<ResponseEntity<byte[]>> serveSegment(
      @PathVariable String videoId,
      @PathVariable String quality,
      @PathVariable String segment,
      ServerHttpRequest request) {
    if (!SEGMENT_PATTERN.matcher(segment).matches()) {
      return Mono.just(ResponseEntity.badRequest().build());
    }
    Path path = storageService.qualityDir(videoId, quality).resolve(segment);
    return Mono.fromCallable(() -> buildSegmentResponse(path, request.getHeaders().getRange()))
        .subscribeOn(Schedulers.boundedElastic());
  }

  private Mono<ResponseEntity<Resource>> servePlaylistFile(Path path) {
    FileSystemResource resource = new FileSystemResource(path);
    if (!resource.exists()) {
      return Mono.just(ResponseEntity.notFound().build());
    }
    return Mono.just(
        ResponseEntity.ok()
            .contentType(HLS_MIME)
            .cacheControl(HLS_CACHE_CONTROL)
            .header(HttpHeaders.ACCEPT_RANGES, "bytes")
            .body((Resource) resource));
  }

  private ResponseEntity<byte[]> buildSegmentResponse(Path path, List<HttpRange> ranges)
      throws Exception {
    FileSystemResource resource = new FileSystemResource(path);
    if (!resource.exists()) {
      return ResponseEntity.notFound().build();
    }

    if (!ranges.isEmpty()) {
      long total = resource.contentLength();
      HttpRange range = ranges.get(0);
      long start = range.getRangeStart(total);
      long end = range.getRangeEnd(total);
      byte[] bytes = new byte[(int) (end - start + 1)];
      try (RandomAccessFile raf = new RandomAccessFile(path.toFile(), "r")) {
        raf.seek(start);
        raf.readFully(bytes);
      }
      return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
          .contentType(TS_MIME)
          .cacheControl(HLS_CACHE_CONTROL)
          .header(HttpHeaders.CONTENT_RANGE, "bytes " + start + "-" + end + "/" + total)
          .header(HttpHeaders.ACCEPT_RANGES, "bytes")
          .body(bytes);
    }

    return ResponseEntity.ok()
        .contentType(TS_MIME)
        .cacheControl(HLS_CACHE_CONTROL)
        .header(HttpHeaders.ACCEPT_RANGES, "bytes")
        .body(resource.getContentAsByteArray());
  }
}
