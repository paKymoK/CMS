package com.takypok.authservice.model.response;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponse {
  private String sub;
  private String name;
  private String email;
  private Long departmentId;
  private Long unitId;

  /** ACTIVE or INACTIVE. */
  private String status;

  /** LOCAL (managed here, can be deleted) or DIRECTORY (LDAP staff, can only be deactivated). */
  private String accountType;

  private String displayName;
  private LocalDate dateOfBirth;

  /** Always UTC (+00:00); null until the first sign-in. */
  private ZonedDateTime lastLoginAt;

  private ZonedDateTime createdAt;

  /** cms-admin roles; a null projectId is a role on every site. */
  private List<RoleView> roles;

  @Getter
  @Builder
  public static class RoleView {
    private String projectId;
    private String role;
  }
}
