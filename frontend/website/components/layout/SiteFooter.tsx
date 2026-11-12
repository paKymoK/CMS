import { getTranslations } from "next-intl/server";
import { SiteFooterView } from "@cms/content-renderer";
import type { FooterNavCategory, SiteFooterContent } from "@/content/home/types";

/**
 * The dark footer from the Figma "Footer": a "Let's connect" block with social icons on the left,
 * four link columns on the right, and a rule with the copyright and legal links. Columns come from
 * the CMS's footer navigation; the rest from the "site_footer" content item. Drawn by the shared
 * renderer, so the admin's preview shows the same footer.
 */
export async function SiteFooter({
  footerNav,
  footer,
}: {
  footerNav: FooterNavCategory[];
  footer?: SiteFooterContent;
}) {
  const t = await getTranslations("footer");
  return <SiteFooterView footerNav={footerNav} footer={footer} defaultCopyright={t("copyright", { year: new Date().getFullYear() })} />;
}
