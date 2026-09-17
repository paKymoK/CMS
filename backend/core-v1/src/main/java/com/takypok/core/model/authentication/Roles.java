package com.takypok.core.model.authentication;

/**
 * Canonical role names shared by auth-service (which assigns them) and the resource services (which
 * enforce them). Roles are bundles of capabilities; the actual capability rules live in each
 * service's access policy. Keep this list small and stable.
 */
public final class Roles {

  /** Full access everywhere; granted globally (no project scope). */
  public static final String ADMIN = "ADMIN";

  /** Works the queue on a project: read all, edit, assign, transition. */
  public static final String AGENT = "AGENT";

  /** May act on approval transitions for a project. */
  public static final String APPROVER = "APPROVER";

  /** Default for any authenticated user; can raise and follow their own tickets. */
  public static final String REQUESTER = "REQUESTER";

  private Roles() {}
}
