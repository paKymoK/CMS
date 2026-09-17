package com.takypok.mediaservice.service;

import com.takypok.mediaservice.model.entity.UploadFile;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

public interface UploadFileService {
  Mono<UploadFile> upload(FilePart filePart);
}
