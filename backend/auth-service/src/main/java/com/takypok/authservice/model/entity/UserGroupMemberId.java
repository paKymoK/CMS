package com.takypok.authservice.model.entity;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserGroupMemberId implements Serializable {
  private String groupId;
  private String userSub;
}
