package com.takypok.authservice.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Relocated from employee-service (Phase 7) — auth-service is now the authoritative owner;
 * employee-service keeps a read-only Kafka-fed mirror under the same table name.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Department {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String name;

  /** Employee sub of the department head, if assigned. */
  private String head;

  private String location;
}
