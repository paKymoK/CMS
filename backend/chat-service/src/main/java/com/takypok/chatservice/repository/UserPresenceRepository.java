package com.takypok.chatservice.repository;

import com.takypok.chatservice.model.entity.UserPresence;
import java.time.ZonedDateTime;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface UserPresenceRepository extends R2dbcRepository<UserPresence, String> {

  // sub is a caller-assigned key (not a generated id), so plain save() can't tell insert
  // from update — upsert explicitly instead.
  @Query(
      """
      INSERT INTO user_presence (sub, last_seen_at) VALUES (:sub, :lastSeenAt)
      ON CONFLICT (sub) DO UPDATE SET last_seen_at = :lastSeenAt
      """)
  Mono<Void> upsertLastSeen(String sub, ZonedDateTime lastSeenAt);
}
