package com.takypok.contentservice.util;

/** Small rules for turning an update request into stored field values. */
public final class FieldRules {
  private FieldRules() {}

  /**
   * A field a client may not send at all: {@code null} keeps the stored value, a blank string
   * clears it, anything else replaces it. A client that does not know the field never wipes it.
   */
  public static String keepOrClear(String requested, String stored) {
    if (requested == null) return stored;
    return requested.isBlank() ? null : requested;
  }
}
