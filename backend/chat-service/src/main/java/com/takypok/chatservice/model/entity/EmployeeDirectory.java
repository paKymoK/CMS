package com.takypok.chatservice.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table("employee_directory")
public class EmployeeDirectory {
  @Id private String sub;

  private String name;
  private String email;
  private String title;
  private String department;

  @Column("avatar_url")
  private String avatarUrl;

  @Column("manager_sub")
  private String managerSub;
}
