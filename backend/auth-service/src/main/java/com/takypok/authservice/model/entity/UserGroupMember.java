package com.takypok.authservice.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_group_member")
@IdClass(UserGroupMemberId.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserGroupMember {

  @Id
  @Column(name = "group_id")
  private String groupId;

  @Id
  @Column(name = "user_sub")
  private String userSub;
}
