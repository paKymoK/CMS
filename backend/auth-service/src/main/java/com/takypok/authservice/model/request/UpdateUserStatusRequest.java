package com.takypok.authservice.model.request;

import com.takypok.authservice.model.entity.UserStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserStatusRequest {
  @NotNull private UserStatus status;
}
