package com.takypok.authservice.model.request;

import lombok.Getter;
import lombok.Setter;

/**
 * Partial update — only non-null fields are applied. This is the write path the future
 * employee-service-hosted HR frontend calls directly for name/email/department/unit (Phase 7).
 */
@Getter
@Setter
public class UpdateUserProfileRequest {
  private String name;
  private String email;
  private Long departmentId;
  private Long unitId;
}
