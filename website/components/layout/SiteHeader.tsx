"use client";

import Image from "next/image";
import { Link } from "@/i18n/navigation";
import { useTranslations } from "next-intl";
import { HEADER_NAV } from "@/content/nav";

const Chevron = () => (
  <svg viewBox="0 0 8 5" className="h-[5px] w-2 fill-none stroke-current stroke-[1.4]" aria-hidden>
    <path d="m1 1 3 3 3-3" strokeLinecap="round" strokeLinejoin="round" />
  </svg>
);

/**
 * Flat white 58px header from the Figma "Header" component: logo left, six links centred, then
 * search, language and the sky-blue "Contact Us" pill. Lies over the top of the hero exactly as on the
 * artboard (the hero's top 58px is drawn underneath it) and scrolls away with the page.
 */
export function SiteHeader() {
  const t = useTranslations("cta");

  return (
    <header
      className="absolute inset-x-0 top-0 z-[100] h-[58px] bg-white"
      style={{ boxShadow: "0 4px 9.8px rgba(0,0,0,.15)" }}
    >
      <div className="mx-auto grid h-full w-full max-w-[1458px] grid-cols-[auto_1fr_auto] items-center gap-6 px-6 lg:grid-cols-[1fr_auto_1fr]">
        <Link href="/" className="flex items-center" aria-label="CMC Global home">
          <Image
            src="/images/logo.svg"
            alt="CMC Global"
            width={150}
            height={31}
            priority
            className="h-[31px] w-auto"
          />
        </Link>

        <nav
          aria-label="Primary"
          className="hidden items-center justify-center gap-[clamp(28px,3.9vw,76px)] text-base font-medium whitespace-nowrap text-[#7e7e7e] lg:flex"
        >
          {HEADER_NAV.map((item) => {
            const inner = (
              <>
                {item.label}
                {item.hasDropdown && <Chevron />}
              </>
            );
            const cls = "flex items-center gap-1.5 leading-6 transition-colors hover:text-[#00a9ff]";
            return item.href ? (
              <Link key={item.label} href={item.href} className={cls}>
                {inner}
              </Link>
            ) : (
              <a key={item.label} href="#" className={cls}>
                {inner}
              </a>
            );
          })}
        </nav>

        <div className="flex items-center justify-end gap-[clamp(16px,1.8vw,28px)]">
          <button type="button" aria-label="Search" className="hidden text-[#747474] hover:text-[#00a9ff] sm:block">
            <svg viewBox="0 0 18 18" className="h-[18px] w-[18px] fill-none stroke-current stroke-[1.6]" aria-hidden>
              <circle cx="8" cy="8" r="6" />
              <path d="m12.5 12.5 4 4" strokeLinecap="round" />
            </svg>
          </button>
          <button
            type="button"
            aria-label="Language: English"
            className="hidden items-center gap-1 text-base text-[#747474] hover:text-[#00a9ff] sm:flex"
          >
            <svg viewBox="0 0 20 20" className="mr-1 h-5 w-5 fill-none stroke-current stroke-[1.4]" aria-hidden>
              <circle cx="10" cy="10" r="7.5" />
              <path d="M2.5 10h15M10 2.5c2.2 2.1 3.3 4.6 3.3 7.5S12.200 15.400 10 17.500C7.800 15.400 6.700 12.900 6.700 10S7.800 4.600 10 2.500Z" />
            </svg>
            EN
            <Chevron />
          </button>
          <a
            href="#contact"
            className="flex h-[30px] items-center rounded-[46px] bg-[#00bbff] px-[13px] text-xs font-semibold whitespace-nowrap text-white transition-colors hover:bg-[#00a9ff]"
          >
            {t("contactUs")}
          </a>
        </div>
      </div>
    </header>
  );
}
