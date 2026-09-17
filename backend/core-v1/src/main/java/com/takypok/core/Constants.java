package com.takypok.core;

public final class Constants {
  public static final String AUTHENTICATION_HEADER = "Authorization";
  public static final String AUTHENTICATION_PREFIX = "Bearer ";
  public static final String X_REQUEST_ID = "X-Request-Id";
  public static final String USER_ID = "User-Id";

  // JWT claims carrying the caller's authorization context
  public static final String ROLES_CLAIM = "roles"; // global roles (no project scope)
  public static final String PROJECT_ROLES_CLAIM = "project_roles"; // map of projectId -> [roles]

  // Opaque JWT claim for per-client session policy — key and values are intentionally
  // non-descriptive
  public static final String SESSION_POLICY_CLAIM = "_p1";
  public static final String SESSION_POLICY_OPEN = "fo2x";
  public static final String SESSION_POLICY_CLOSED = "fc7k";
}
