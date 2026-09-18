import { useAuth } from "./useAuth";
import { SITES, type SiteCode } from "../config/sites";

// Mirrors core-v1's Constants.ROLES_CLAIM / PROJECT_ROLES_CLAIM — a global ADMIN role grants
// every site, a project-scoped one only grants the sites present in project_roles.
export interface SiteAccess {
  isGlobalAdmin: boolean;
  /** Site codes this user can manage, in SITES order. Empty means no CMS access at all. */
  accessibleSites: SiteCode[];
}

export function useSiteAccess(): SiteAccess {
  const { user } = useAuth();
  const roles = Array.isArray(user?.roles) ? (user.roles as string[]) : [];
  const isGlobalAdmin = roles.includes("ADMIN");

  if (isGlobalAdmin) {
    return { isGlobalAdmin, accessibleSites: SITES.map((s) => s.code) };
  }

  const projectRoles =
    user?.project_roles && typeof user.project_roles === "object"
      ? (user.project_roles as Record<string, string[]>)
      : {};

  const accessibleSites = SITES.filter((s) =>
    (projectRoles[s.code] ?? []).includes("ADMIN"),
  ).map((s) => s.code);

  return { isGlobalAdmin, accessibleSites };
}
