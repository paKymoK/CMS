import { MobileBottomBar } from "@cms/content-renderer";
import { siteSlots } from "@/components/content/siteSlots";
import { PRIMARY_NAV } from "@/content/nav";

/** The shared mobile bottom bar, with the site's router link, stuck to the bottom of the screen. */
export function MobileBottomNav() {
  return <MobileBottomBar items={PRIMARY_NAV} slots={siteSlots} />;
}
