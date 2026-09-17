package com.takypok.chatservice.repository;

import com.takypok.chatservice.model.entity.DeviceTokenDirectory;
import java.util.Collection;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface DeviceTokenDirectoryRepository
    extends R2dbcRepository<DeviceTokenDirectory, String> {

  // fcm_token is a caller-assigned key, so plain save() can't tell insert from update — upsert
  // explicitly, same as EmployeeDirectoryRepository.
  @Query(
      """
      INSERT INTO device_token_directory (fcm_token, sub, platform)
      VALUES (:fcmToken, :sub, :platform)
      ON CONFLICT (fcm_token) DO UPDATE SET sub = :sub, platform = :platform
      """)
  Mono<Void> upsert(String fcmToken, String sub, String platform);

  Mono<Void> deleteBySubAndFcmToken(String sub, String fcmToken);

  Flux<DeviceTokenDirectory> findBySubIn(Collection<String> subs);
}
