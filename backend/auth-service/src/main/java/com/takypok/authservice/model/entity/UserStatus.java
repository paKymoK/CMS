package com.takypok.authservice.model.entity;

/** Stored in {@code userinfo.status}; never null. */
public enum UserStatus {
  ACTIVE,
  INACTIVE,
  /** Created by invitation: no password set yet, so it cannot sign in until activated. */
  INVITED
}
