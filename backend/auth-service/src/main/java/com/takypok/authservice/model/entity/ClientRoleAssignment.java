package com.takypok.authservice.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "client_role_assignment")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientRoleAssignment {

  @Id private String id;

  @Column(name = "registered_client_id", nullable = false)
  private String registeredClientId;

  @Column(name = "user_sub")
  private String userSub;

  @Column(name = "group_id")
  private String groupId;

  /** Null = the assignment applies to every project on the client (global within the client). */
  @Column(name = "project_id")
  private String projectId;

  @Column(nullable = false)
  private String role;
}
