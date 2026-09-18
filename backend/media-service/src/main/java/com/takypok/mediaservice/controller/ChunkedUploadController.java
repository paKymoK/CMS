package com.takypok.mediaservice.controller;

import com.takypok.mediaservice.config.CmsAdminGuard;
import com.takypok.mediaservice.model.dto.Base64ChunkRequest;
import com.takypok.mediaservice.model.dto.ChunkAckResponse;
import com.takypok.mediaservice.model.dto.ChunkedUploadedFile;
import com.takypok.mediaservice.model.dto.FinishChunkedUploadRequest;
import com.takypok.mediaservice.model.dto.StartChunkedUploadRequest;
import com.takypok.mediaservice.model.dto.StartChunkedUploadResponse;
import com.takypok.mediaservice.model.entity.UploadFile;
import com.takypok.mediaservice.service.ChunkedUploadService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Standalone chunked-upload API, separate from {@link UploadFileController}'s single-shot multipart
 * endpoints. A client slices a large file into fixed-size pieces (see the {@code chunkSizeBytes}
 * returned by {@code /start}) and sends each with independent retry.
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/v1/upload/chunked")
public class ChunkedUploadController {
  private final ChunkedUploadService chunkedUploadService;
  private final CmsAdminGuard cmsAdminGuard;

  @PostMapping("/start")
  public Mono<StartChunkedUploadResponse> start(
      @RequestBody StartChunkedUploadRequest request, Authentication authentication) {
    return cmsAdminGuard
        .requireSiteAccess(authentication, request.site())
        .then(chunkedUploadService.start(request));
  }

  @PostMapping(
      value = "/{sessionId}/chunks/{index}",
      consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public Mono<ChunkAckResponse> uploadChunk(
      @PathVariable String sessionId, @PathVariable int index, @RequestBody Flux<DataBuffer> body) {
    return chunkedUploadService.writeChunk(sessionId, index, body);
  }

  @PostMapping(
      value = "/{sessionId}/chunks/{index}/base64",
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public Mono<ChunkAckResponse> uploadChunkBase64(
      @PathVariable String sessionId,
      @PathVariable int index,
      @RequestBody Base64ChunkRequest request) {
    return chunkedUploadService.writeChunkBase64(sessionId, index, request);
  }

  @PostMapping(
      value = "/{sessionId}/chunks/{index}/encrypted",
      consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public Mono<ChunkAckResponse> uploadChunkEncrypted(
      @PathVariable String sessionId, @PathVariable int index, @RequestBody Flux<DataBuffer> body) {
    return chunkedUploadService.writeChunkEncrypted(sessionId, index, body);
  }

  @PostMapping("/{sessionId}/finish")
  public Mono<UploadFile> finish(
      @PathVariable String sessionId, @RequestBody FinishChunkedUploadRequest request) {
    return chunkedUploadService.finish(sessionId, request);
  }

  @GetMapping("/files")
  public Mono<List<ChunkedUploadedFile>> listFiles(
      @RequestParam String site, Authentication authentication) {
    return cmsAdminGuard
        .requireSiteAccess(authentication, site)
        .then(chunkedUploadService.listFiles(site));
  }

  @DeleteMapping("/files/{name}")
  public Mono<Void> deleteFile(
      @PathVariable String name, @RequestParam String site, Authentication authentication) {
    return cmsAdminGuard
        .requireSiteAccess(authentication, site)
        .then(chunkedUploadService.deleteFile(name, site));
  }

  @DeleteMapping("/files")
  public Mono<Void> deleteAllFiles(@RequestParam String site, Authentication authentication) {
    return cmsAdminGuard
        .requireSiteAccess(authentication, site)
        .then(chunkedUploadService.deleteAllFiles(site));
  }
}
