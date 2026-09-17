package com.takypok.core.model.authentication;

/**
 * Atomic capabilities checked in code. Roles map onto these in each service's access policy. Naming
 * convention: {@code resource:action[:scope]}.
 */
public final class Permissions {

  public static final String TICKET_CREATE = "ticket:create";
  public static final String TICKET_READ_OWN = "ticket:read:own";
  public static final String TICKET_READ_ALL = "ticket:read:all";
  public static final String TICKET_EDIT = "ticket:edit";
  public static final String TICKET_ASSIGN = "ticket:assign";
  public static final String TICKET_TRANSITION = "ticket:transition";

  private Permissions() {}
}
