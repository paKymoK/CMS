package com.takypok.authservice.web;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/** Small helpers for reading form fields in the server-rendered screens. */
final class Forms {
  private Forms() {}

  static String blankToNull(String s) {
    return s == null || s.isBlank() ? null : s.trim();
  }

  /** An ISO date from a date input, or null when empty. Throws IllegalArgumentException if bad. */
  static LocalDate parseBirthDate(String s) {
    String v = blankToNull(s);
    if (v == null) return null;
    try {
      LocalDate d = LocalDate.parse(v);
      if (d.isAfter(LocalDate.now())) {
        throw new IllegalArgumentException("Date of birth cannot be in the future.");
      }
      return d;
    } catch (DateTimeParseException ex) {
      throw new IllegalArgumentException("Date of birth is not a valid date.");
    }
  }
}
