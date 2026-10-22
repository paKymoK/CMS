import { Inter, IBM_Plex_Mono, JetBrains_Mono } from "next/font/google";
import { NextIntlClientProvider, hasLocale } from "next-intl";
import { setRequestLocale } from "next-intl/server";
import { notFound } from "next/navigation";
import { routing } from "@/i18n/routing";
import { getHomeContent } from "@/lib/cms/homeContent";
import { getServiceNav } from "@/lib/cms/services";
import { SiteHeader } from "@/components/layout/SiteHeader";
import { SiteFooter } from "@/components/layout/SiteFooter";
import { MobileBottomNav } from "@/components/layout/MobileBottomNav";
import "../globals.css";
import "../sections.css";
import "../../../packages/content-renderer/src/about.css";
import "../insights.css";
import "../../../packages/content-renderer/src/industry.css";
import "../../../packages/content-renderer/src/service.css";

// The Figma design ("CMC Global - Website 2026") uses Inter for every piece of type, in its
// "24pt" optical size. The variable font's opsz axis reproduces that: with font-optical-sizing
// (default auto) large headings get the tighter display cut, so line breaks match the artboard.
const inter = Inter({
  variable: "--font-inter",
  subsets: ["latin", "vietnamese"],
  axes: ["opsz"],
});

// Mono accent font for eyebrow labels / stat & slide-counter text in the
// restyled landing page, matching the new design.
const ibmPlexMono = IBM_Plex_Mono({
  variable: "--font-ibm-plex-mono",
  subsets: ["latin"],
  weight: ["400", "500"],
});

// Eyebrow/label/hint font for the redesigned homepage body sections (wave
// band, section nav, service cards, etc).
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
  const [content, serviceNav] = await Promise.all([getHomeContent(), getServiceNav()]);

  return (
    <html
      lang={locale}
      className={`${inter.variable} ${ibmPlexMono.variable} ${jetbrainsMono.variable} h-full antialiased`}
    >
      <body className="flex min-h-full flex-col pb-[calc(64px+env(safe-area-inset-bottom))] md:pb-0">
        <NextIntlClientProvider>
          <SiteHeader serviceNav={serviceNav} />
          {/* Content is designed for a 1920px artboard and capped there (wider screens are unsupported). */}
          <main className="mx-auto w-full max-w-[1920px] flex-1">{props.children}</main>
          <SiteFooter footerNav={content.footerNav} footer={content.sections?.site_footer?.[0]} />
          <MobileBottomNav />
        </NextIntlClientProvider>
      </body>
    </html>
  );
}
