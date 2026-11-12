package com.takypok.mediaservice.service;

import com.takypok.mediaservice.model.entity.UploadFile;
import java.util.List;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

public interface UploadFileService {
  Mono<UploadFile> upload(FilePart filePart, String siteId);

  Mono<List<UploadFile>> listForSite(String siteId);

  /** Sets the library metadata (title, alt text, description); blank values are stored as empty. */
  Mono<UploadFile> updateMetadata(
      java.util.UUID id, String siteId, String title, String altText, String description);
}
