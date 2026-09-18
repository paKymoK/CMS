// Mirrors content-service's seed-sites.sql exactly — site codes are locale codes (site = locale
// per the platform's decision log), not country codes. Keep in sync if that seed ever changes.
export const SITES = [
  { code: "en", label: "English", subdomain: "en.cmcglobal.com" },
  { code: "vi", label: "Vietnam", subdomain: "vn.cmcglobal.com" },
  { code: "ja", label: "Japan", subdomain: "jp.cmcglobal.com" },
  { code: "ko", label: "Korea", subdomain: "kr.cmcglobal.com" },
  { code: "de", label: "Germany", subdomain: "de.cmcglobal.com" },
] as const;

export type SiteCode = (typeof SITES)[number]["code"];

export function siteLabel(code: string): string {
  return SITES.find((s) => s.code === code)?.label ?? code;
}
