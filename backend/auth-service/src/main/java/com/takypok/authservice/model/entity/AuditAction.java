package com.takypok.authservice.model.entity;

/** Stored in {@code account_audit.action}. The label is what the audit screen shows. */
public enum AuditAction {
  LOGIN_SUCCESS("Signed in"),
  LOGIN_FAILED("Sign-in failed"),
  LOGIN_LOCKED("Sign-in refused: locked"),
  LOGIN_BLOCKED("Sign-in refused: inactive"),
  USER_CREATED("Account created"),
  PROFILE_UPDATED("Profile updated"),
  STATUS_CHANGED("Status changed"),
  USER_DELETED("Account deleted"),
  ROLE_ASSIGNED("Role assigned"),
  ROLE_REMOVED("Role removed"),
  GROUP_CREATED("Group created"),
  GROUP_DELETED("Group deleted"),
  GROUP_MEMBER_ADDED("Group member added"),
  GROUP_MEMBER_REMOVED("Group member removed"),
  INVITATION_SENT("Invitation sent"),
  ACCOUNT_ACTIVATED("Account activated"),
  PASSWORD_RESET_SENT("Password reset link sent"),
  PASSWORD_RESET_DONE("Password reset"),
  EMAIL_SEND_FAILED("Email could not be sent"),
  EMAIL_CONFLICT("Directory email already in use"),
  PASSWORD_CHANGED("Password changed"),
  PASSWORD_CHANGE_FAILED("Password change refused"),
  AUDIT_EXPORTED("Audit log exported");

  private final String label;

  AuditAction(String label) {
    this.label = label;
  }

  public String label() {
    return label;
  }
}
