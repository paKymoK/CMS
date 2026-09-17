package com.takypok.authservice.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_group")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserGroup {

  @Id private String id;

  @Column(nullable = false, unique = true)
  private String name;

  private String description;
}
