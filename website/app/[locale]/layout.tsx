import { Montserrat, IBM_Plex_Mono, JetBrains_Mono } from "next/font/google";
import { NextIntlClientProvider, hasLocale } from "next-intl";
import { setRequestLocale } from "next-intl/server";
import { notFound } from "next/navigation";
import { routing } from "@/i18n/routing";
import { getHomeContent } from "@/lib/cms/homeContent";
import { SiteHeader } from "@/components/layout/SiteHeader";
import { SiteFooter } from "@/components/layout/SiteFooter";
import { MobileBottomNav } from "@/components/layout/MobileBottomNav";
import "../globals.css";

// Real site's body/heading font, confirmed via computed-style inspection
// (getComputedStyle(...).fontFamily === "Montserrat, sans-serif") on 2026-08-27.
const montserrat = Montserrat({
  variable: "--font-montserrat",
  subsets: ["latin"],
  weight: ["400", "500", "600", "700", "800"],
});

// Mono accent font for eyebrow labels / stat & slide-counter text in the
// restyled landing page, matching the new design.
const ibmPlexMono = IBM_Plex_Mono({
  variable: "--font-ibm-plex-mono",
  subsets: ["latin"],
  weight: ["400", "500"],
});

// Eyebrow/label/hint font for the redesigned homepage body sections (wave
// band, section nav, service cards, etc), per the Claude Design handoff.
// Scoped to its own token (font-mono-wave) — kept separate from font-mono
// (IBM Plex Mono) which the header/footer keep using.
const jetbrainsMono = JetBrains_Mono({
  variable: "--font-jetbrains-mono",
  subsets: ["latin"],
  weight: ["400", "500", "700"],
});

export function generateStaticParams() {
  return routing.locales.map((locale) => ({ locale }));
}

export default async function LocaleLayout(
  props: LayoutProps<"/[locale]">,
) {
  const { locale } = await props.params;

  if (!hasLocale(routing.locales, locale)) {
    notFound();
  }

  setRequestLocale(locale);

  // Next dedupes identical concurrent fetch() calls within one render (Request Memoization),
  // so this and page.tsx's own getHomeContent() call cost one network round-trip, not two.
  const content = await getHomeContent();

  return (
    <html
      lang={locale}
      className={`${montserrat.variable} ${ibmPlexMono.variable} ${jetbrainsMono.variable} h-full antialiased`}
    >
      <body className="flex min-h-full flex-col pb-[calc(64px+env(safe-area-inset-bottom))] md:pb-0">
        <NextIntlClientProvider>
          <SiteHeader />
          <main className="flex-1">{props.children}</main>
          <SiteFooter footerNav={content.footerNav} />
          <MobileBottomNav />
        </NextIntlClientProvider>
      </body>
    </html>
  );
}
