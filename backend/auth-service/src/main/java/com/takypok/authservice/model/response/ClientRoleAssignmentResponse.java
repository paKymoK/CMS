package com.takypok.authservice.model.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ClientRoleAssignmentResponse {
  private String id;
  private String registeredClientId;

  /** "USER" or "GROUP" */
  private String type;

  private String subjectId;
  private String subjectName;

  /** Null = applies to every project on the client. */
  private String projectId;

  private String role;
}
