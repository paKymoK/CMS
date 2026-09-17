package com.takypok.authservice.model.event;

import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Published to {@code department.events} on create/update/delete — full snapshot, employee-service
 * upserts (or deletes) its read-only mirror row from {@code payload} alone.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentEvent {
  private String eventType; // CREATED, UPDATED, DELETED
  private Long id;
  private String name;
  private String head;
  private String location;
  private ZonedDateTime occurredAt;

  public DepartmentEvent(String eventType, Long id, String name, String head, String location) {
    this.eventType = eventType;
    this.id = id;
    this.name = name;
    this.head = head;
    this.location = location;
    this.occurredAt = ZonedDateTime.now();
  }
}
