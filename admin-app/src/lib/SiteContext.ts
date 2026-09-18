import { createContext } from "react";
import type { SiteCode } from "../config/sites";

export interface SiteContextType {
  site: SiteCode | null;
  setSite: (site: SiteCode) => void;
  accessibleSites: SiteCode[];
}

export const SiteContext = createContext<SiteContextType | null>(null);
