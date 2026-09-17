package com.takypok.authservice.model.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserGroupResponse {
  private String id;
  private String name;
  private String description;
  private List<GroupMemberResponse> members;
}
