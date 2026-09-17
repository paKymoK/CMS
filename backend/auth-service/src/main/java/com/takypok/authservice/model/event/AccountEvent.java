package com.takypok.authservice.model.event;

import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Announces that a sub has just become real (guest account created, or an internal LDAP user logged
 * in for the first time) — or that auth-service's Userinfo record for a sub changed afterward
 * (Phase 7: name/email/department/unit are now auth-service-owned). employee-service consumes this
 * to create/update the corresponding shell {@code Employee} row; it never accepts a directly-typed
 * sub on create anymore.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountEvent {
  private String sub;
  private String name;
  private String email;
  private Long departmentId;
  private Long unitId;
  private ZonedDateTime occurredAt;

  public AccountEvent(String sub, String name, String email, Long departmentId, Long unitId) {
    this.sub = sub;
    this.name = name;
    this.email = email;
    this.departmentId = departmentId;
    this.unitId = unitId;
    this.occurredAt = ZonedDateTime.now();
  }
}
