import { useContext } from "react";
import { SiteContext, type SiteContextType } from "./SiteContext";

export function useSite(): SiteContextType {
  const ctx = useContext(SiteContext);
  if (!ctx) throw new Error("useSite must be used within a SiteProvider");
  return ctx;
}
