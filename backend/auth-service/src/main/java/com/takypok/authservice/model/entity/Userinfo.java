package com.takypok.authservice.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * auth-service's authoritative record for identity-adjacent general data (Phase 7). Only
 * name/email/departmentId/unitId have real read/write logic — LDAP-derived or self-entered at first
 * login for name/email, admin-managed catalogs for department/unit. Everything else here (title,
 * workLocation, managerSub, joinedDate, shift, shiftHours, status, avatarUrl) is an inert
 * placeholder column with no producer or consumer yet; employee-service remains authoritative for
 * those fields until a future HR-editing frontend exists.
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

  // Placeholder columns below — employee-service is still authoritative for these.
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

  private String status;

  @Column(name = "avatar_url")
  private String avatarUrl;
}
