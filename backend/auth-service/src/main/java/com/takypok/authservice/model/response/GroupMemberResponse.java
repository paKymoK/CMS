package com.takypok.authservice.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class GroupMemberResponse {
  private String sub;
  private String name;
  private String email;
}
