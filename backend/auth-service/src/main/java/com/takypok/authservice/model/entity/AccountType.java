package com.takypok.authservice.model.entity;

/**
 * Stored in {@code userinfo.account_type}. LOCAL accounts are created and managed here (password,
 * profile, delete); DIRECTORY accounts belong to LDAP, so name/email come from the directory and
 * they can be deactivated but never deleted.
 */
public enum AccountType {
  LOCAL,
  DIRECTORY
}
