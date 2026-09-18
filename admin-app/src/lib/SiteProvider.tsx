import { useState, type ReactNode } from "react";
import { useSiteAccess } from "../auth/useSiteAccess";
import { SiteContext } from "./SiteContext";
import type { SiteCode } from "../config/sites";

const STORAGE_KEY = "cms_admin_site";

function resolveSite(accessibleSites: SiteCode[], override: SiteCode | null): SiteCode | null {
  if (accessibleSites.length === 0) return null;
  if (override && accessibleSites.includes(override)) return override;
  const stored = localStorage.getItem(STORAGE_KEY) as SiteCode | null;
  if (stored && accessibleSites.includes(stored)) return stored;
  return accessibleSites[0];
}

export function SiteProvider({ children }: { children: ReactNode }) {
  const { accessibleSites } = useSiteAccess();
  // Only set when the user explicitly switches sites this session — otherwise the site is
  // derived below from localStorage/accessibleSites on every render (cheap: a handful of array
  // ops), so there's no need to sync it into state via an effect just to reflect the JWT
  // resolving after mount.
  const [override, setOverride] = useState<SiteCode | null>(null);
  const site = resolveSite(accessibleSites, override);

  const setSite = (next: SiteCode) => {
    setOverride(next);
    localStorage.setItem(STORAGE_KEY, next);
  };

  return (
    <SiteContext.Provider value={{ site, setSite, accessibleSites }}>
      {children}
    </SiteContext.Provider>
  );
}
