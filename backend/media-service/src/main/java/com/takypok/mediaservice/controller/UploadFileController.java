package com.takypok.mediaservice.controller;

import com.takypok.mediaservice.config.CmsAdminGuard;
import com.takypok.mediaservice.model.entity.UploadFile;
import com.takypok.mediaservice.service.UploadFileService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/v1/upload")
public class UploadFileController {
  private final UploadFileService uploadFileService;
  private final CmsAdminGuard cmsAdminGuard;

  @PostMapping("/single")
  public Mono<UploadFile> uploadSingleFile(
      @RequestParam String site,
      @RequestPart("file") Mono<FilePart> filePartMono,
      Authentication authentication) {
    return cmsAdminGuard
        .requireSiteAccess(authentication, site)
        .then(filePartMono.flatMap(filePart -> uploadFileService.upload(filePart, site)));
  }

  @PostMapping("/multiple")
  public Mono<List<UploadFile>> uploadMultipleFiles(
      @RequestParam String site,
      @RequestPart("files") Flux<FilePart> filePartsFlux,
      Authentication authentication) {
    return cmsAdminGuard
        .requireSiteAccess(authentication, site)
        .then(
            filePartsFlux
                .flatMap(filePart -> uploadFileService.upload(filePart, site))
                .collectList());
  }

  /**
   * Media library listing for the admin picker (Phase 4) — every uploaded image/file for one site.
   */
  @GetMapping
  public Mono<List<UploadFile>> list(@RequestParam String site, Authentication authentication) {
    return cmsAdminGuard
        .requireSiteAccess(authentication, site)
        .then(uploadFileService.listForSite(site));
  }
}
