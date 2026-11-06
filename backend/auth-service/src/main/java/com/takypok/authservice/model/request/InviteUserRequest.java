package com.takypok.authservice.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InviteUserRequest {
  /** The email address: it is also the sign-in name. */
  @NotBlank private String username;

  private String name;
}
