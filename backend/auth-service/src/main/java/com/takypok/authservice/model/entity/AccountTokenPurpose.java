package com.takypok.authservice.model.entity;

/** Stored in {@code account_token.purpose}. */
public enum AccountTokenPurpose {
  /** Sent with an invitation: lets the person set a first password. */
  ACTIVATE,
  /** Sent on request: lets the person choose a new password. */
  RESET
}
