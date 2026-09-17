package com.takypok.mediaservice.repository;

import com.takypok.mediaservice.model.entity.UploadFile;
import java.util.UUID;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.lang.NonNull;
import reactor.core.publisher.Mono;

public interface UploadFileRepository extends R2dbcRepository<UploadFile, UUID> {
  @NonNull
  @Query(
      """
                  SELECT * FROM upload_file
                  WHERE id = :id
                  """)
  Mono<UploadFile> findById(@NonNull UUID id);
}
