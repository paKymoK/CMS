package com.takypok.authservice.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "client_session_policy")
@Getter
@Setter
@NoArgsConstructor
public class ClientSessionPolicy {

  /** Used for a client that has no row yet; also the column default. */
  public static final int DEFAULT_MAX_SESSION_DAYS = 30;

  @Id
  @Column(name = "registered_client_id")
  private String registeredClientId;

  @Column(name = "single_tab", nullable = false)
  private boolean singleTab;

  @Column(name = "fail_open", nullable = false)
  private boolean failOpen = true;

  /** Days a sign-in may last from its login, however often refreshed; null = no limit. */
  @Column(name = "max_session_days")
  private Integer maxSessionDays = DEFAULT_MAX_SESSION_DAYS;
}
