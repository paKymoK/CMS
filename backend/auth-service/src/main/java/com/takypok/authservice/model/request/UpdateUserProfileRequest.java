package com.takypok.authservice.model.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;

/**
 * Merge-patch style update: a field that is not sent stays as it is, a field sent as {@code null}
 * (or blank text) is cleared. The setters remember which fields were set, so the service can tell
 * "not sent" from "sent as null". Name and email of a DIRECTORY account belong to LDAP and are
 * refused; the email of a LOCAL account cannot be cleared.
 */
@Getter
public class UpdateUserProfileRequest {
  private String name;
  private String email;
  private Long departmentId;
  private Long unitId;
  private String displayName;
  private LocalDate dateOfBirth;

  @Getter(AccessLevel.NONE)
  private final Set<String> sent = new HashSet<>();

  public void setName(String name) {
    this.name = name;
    sent.add("name");
  }

  public void setEmail(String email) {
    this.email = email;
    sent.add("email");
  }

  public void setDepartmentId(Long departmentId) {
    this.departmentId = departmentId;
    sent.add("departmentId");
  }

  public void setUnitId(Long unitId) {
    this.unitId = unitId;
    sent.add("unitId");
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
    sent.add("displayName");
  }

  public void setDateOfBirth(LocalDate dateOfBirth) {
    this.dateOfBirth = dateOfBirth;
    sent.add("dateOfBirth");
  }

  @JsonIgnore
  public boolean isSent(String field) {
    return sent.contains(field);
  }
}
