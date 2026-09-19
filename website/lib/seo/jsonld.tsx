import { SITE_NAME, SITE_URL } from "./constants";

/**
 * The live site (via Yoast) already emits WebPage/BreadcrumbList/WebSite JSON-LD —
 * Organization schema is the one genuine gap this Phase 0 page fills.
 *
 * `sameAs` only includes the Facebook URL — that one is real, extracted from the
 * live site's `article:publisher` meta tag. YouTube/LinkedIn were mentioned in the
 * footer but no verified URLs were extracted, so they're a TODO, not fabricated.
 */
export function organizationJsonLd() {
  return {
    "@context": "https://schema.org",
    "@type": "Organization",
    name: SITE_NAME,
    url: `${SITE_URL}/en`,
    logo: `${SITE_URL}/images/logo.svg`,
    foundingDate: "1993",
    description:
      "CMC Global is a member of CMC Corporation with an aspiration to bring ICT products, solutions, and services of Vietnam to the international market.",
    sameAs: ["https://www.facebook.com/CMCGlobal2017"],
  };
}

export function JsonLd({ data }: { data: Record<string, unknown> }) {
  // Plain children rather than dangerouslySetInnerHTML: React's own
  // script-tag serializer already escapes a `</script` sequence inside the
  // string (as a JSON-safe s unicode escape) without corrupting the
  // JSON, so this stays safe even if this ever gets fed non-static content.
  return <script type="application/ld+json">{JSON.stringify(data)}</script>;
}
