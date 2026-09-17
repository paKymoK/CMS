package com.takypok.authservice.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "client_session_policy")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClientSessionPolicy {

  @Id
  @Column(name = "registered_client_id")
  private String registeredClientId;

  @Column(name = "single_tab", nullable = false)
  private boolean singleTab;

  @Column(name = "fail_open", nullable = false)
  private boolean failOpen = true;
}
