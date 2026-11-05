package com.takypok.authservice.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * auth-service's authoritative record for identity-adjacent general data. Only
 * name/email/departmentId/unitId have real read/write logic — LDAP-derived or self-entered at first
 * login for name/email, admin-managed catalogs for department/unit. {@code status} (see {@link
 * UserStatus}; never null) is set by the user-management API and checked at login. Everything else
 * here (title, workLocation, managerSub, joinedDate, shift, shiftHours, avatarUrl) is an inert
 * placeholder column with no producer or consumer yet.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Userinfo {
  @Id private String sub;

  private String name;
  private String email;

  @Column(name = "department_id")
  private Long departmentId;

  @Column(name = "unit_id")
  private Long unitId;

  private String status = UserStatus.ACTIVE.name();

  @Column(name = "account_type")
  private String accountType = AccountType.LOCAL.name();

  @Column(name = "display_name")
  private String displayName;

  @Column(name = "date_of_birth")
  private LocalDate dateOfBirth;

  @Column(name = "last_login_at")
  private ZonedDateTime lastLoginAt;

  @Column(name = "created_at", updatable = false)
  private ZonedDateTime createdAt;

  @Column(name = "updated_at")
  private ZonedDateTime updatedAt;

  @PrePersist
  void onCreate() {
    createdAt = updatedAt = ZonedDateTime.now(ZoneOffset.UTC);
  }

  @PreUpdate
  void onUpdate() {
    updatedAt = ZonedDateTime.now(ZoneOffset.UTC);
  }

  // Placeholder columns below — no read/write logic exists for these yet.
  private String title;

  @Column(name = "work_location")
  private String workLocation;

  @Column(name = "manager_sub")
  private String managerSub;

  @Column(name = "joined_date")
  private LocalDate joinedDate;

  private String shift;

  @Column(name = "shift_hours")
  private String shiftHours;

  @Column(name = "avatar_url")
  private String avatarUrl;
}
