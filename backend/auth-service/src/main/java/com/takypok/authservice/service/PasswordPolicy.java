package com.takypok.authservice.service;

/**
 * The password rule, in one place. The BRD (USR-05) leaves complexity, expiry and history to the
 * BA; until those are defined this is only a minimum length.
 */
public final class PasswordPolicy {
  public static final int MIN_LENGTH = 8;

  private PasswordPolicy() {}

  /** Throws IllegalArgumentException with a sentence to show when the password is not allowed. */
  public static void require(String password) {
    if (password == null || password.length() < MIN_LENGTH) {
      throw new IllegalArgumentException(
          "The password needs at least " + MIN_LENGTH + " characters.");
    }
  }
}
