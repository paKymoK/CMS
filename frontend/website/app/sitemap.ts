import type { MetadataRoute } from "next";
import { LOCALES_WITH_CONTENT, SITE_URL } from "@/lib/seo/constants";

// Only lists locales/paths that actually have built content (LOCALES_WITH_CONTENT),
// so search engines aren't pointed at pages that 404 — see lib/seo/constants.ts.
export default function sitemap(): MetadataRoute.Sitemap {
  return LOCALES_WITH_CONTENT.flatMap((locale) => [
    {
      url: `${SITE_URL}/${locale}`,
      lastModified: new Date(),
      changeFrequency: "weekly" as const,
      priority: 1,
    },
    {
      url: `${SITE_URL}/${locale}/about`,
      lastModified: new Date(),
      changeFrequency: "monthly" as const,
      priority: 0.8,
    },
  ]);
}
