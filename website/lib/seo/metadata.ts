import type { Metadata } from "next";
import { DEFAULT_OG_IMAGE, LOCALES_WITH_CONTENT, SITE_NAME, SITE_URL } from "./constants";

const OG_LOCALE_MAP: Record<string, string> = {
  en: "en_US",
  vi: "vi_VN",
  ja: "ja_JP",
  ko: "ko_KR",
};

type BuildPageMetadataInput = {
  locale: string;
  /** Path within the locale, no leading/trailing slash. Empty string for the home page. */
  path: string;
  title: string;
  description: string;
  ogImage?: string;
  noIndex?: boolean;
};

function localizedUrl(locale: string, path: string): string {
  const suffix = path ? `/${path}` : "";
  return `${SITE_URL}/${locale}${suffix}`;
}

/**
 * Reusable metadata helper — every page should call this from its own
 * generateMetadata with just its own title/description/path, rather than
 * re-deriving canonical/hreflang/OG boilerplate per page.
 */
export function buildPageMetadata({
  locale,
  path,
  title,
  description,
  ogImage = DEFAULT_OG_IMAGE,
  noIndex = false,
}: BuildPageMetadataInput): Metadata {
  const url = localizedUrl(locale, path);
  const ogImageUrl = ogImage.startsWith("http") ? ogImage : `${SITE_URL}${ogImage}`;

  // Only advertise hreflang alternates for locales that actually have content,
  // so we don't point search engines at pages that currently 404.
  const languages: Record<string, string> = {};
  for (const loc of LOCALES_WITH_CONTENT) {
    languages[loc] = localizedUrl(loc, path);
  }
  languages["x-default"] = localizedUrl("en", path);

  return {
    title,
    description,
    alternates: {
      canonical: url,
      languages,
    },
    openGraph: {
      type: "website",
      title,
      description,
      url,
      siteName: SITE_NAME,
      locale: OG_LOCALE_MAP[locale] ?? OG_LOCALE_MAP.en,
      images: [{ url: ogImageUrl }],
    },
    twitter: {
      card: "summary_large_image",
      title,
      description,
      images: [ogImageUrl],
    },
    robots: noIndex
      ? { index: false, follow: false }
      : {
          index: true,
          follow: true,
          "max-image-preview": "large",
          "max-snippet": -1,
          "max-video-preview": -1,
        },
  };
}
