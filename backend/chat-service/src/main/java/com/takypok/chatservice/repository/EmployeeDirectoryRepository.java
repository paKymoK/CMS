package com.takypok.chatservice.repository;

import com.takypok.chatservice.model.entity.EmployeeDirectory;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface EmployeeDirectoryRepository extends R2dbcRepository<EmployeeDirectory, String> {

  // sub is a caller-assigned key (not a generated id), so plain save() can't tell insert
  // from update — upsert explicitly instead, same as UserPresenceRepository.upsertLastSeen.
  @Query(
      """
      INSERT INTO employee_directory (sub, name, email, title, department, avatar_url, manager_sub)
      VALUES (:sub, :name, :email, :title, :department, :avatarUrl, :managerSub)
      ON CONFLICT (sub) DO UPDATE SET
        name = :name, email = :email, title = :title, department = :department,
        avatar_url = :avatarUrl, manager_sub = :managerSub
      """)
  Mono<Void> upsert(
      String sub,
      String name,
      String email,
      String title,
      String department,
      String avatarUrl,
      String managerSub);
}
