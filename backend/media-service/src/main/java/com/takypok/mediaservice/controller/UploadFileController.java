package com.takypok.mediaservice.controller;

import com.takypok.mediaservice.model.entity.UploadFile;
import com.takypok.mediaservice.service.UploadFileService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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

  @PostMapping("/single")
  public Mono<UploadFile> uploadSingleFile(@RequestPart("file") Mono<FilePart> filePartMono) {
    return filePartMono.flatMap(uploadFileService::upload);
  }

  @PostMapping("/multiple")
  public Mono<List<UploadFile>> uploadMultipleFiles(
      @RequestPart("files") Flux<FilePart> filePartsFlux) {
    return filePartsFlux.flatMap(uploadFileService::upload).collectList();
  }
}
