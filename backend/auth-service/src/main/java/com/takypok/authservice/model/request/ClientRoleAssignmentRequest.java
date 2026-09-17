package com.takypok.authservice.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClientRoleAssignmentRequest {

  /** Set for user assignment; leave null for group assignment. */
  private String userSub;

  /** Set for group assignment; leave null for user assignment. */
  private String groupId;

  /** Null = role applies to every project on the client (global within the client). */
  private String projectId;

  @NotBlank private String role;
}
