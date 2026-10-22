"use client";

import { useState } from "react";
import Image from "next/image";
import { Link } from "@/i18n/navigation";
import { useTranslations } from "next-intl";
import { HEADER_NAV, type PrimaryNavItem } from "@/content/nav";
import type { ServiceNav } from "@/lib/cms/services";

const Chevron = ({ open = false }: { open?: boolean }) => (
  <svg
    viewBox="0 0 8 5"
    className={`h-[5px] w-2 fill-none stroke-current stroke-[1.4] transition-transform ${open ? "rotate-180" : ""}`}
    aria-hidden
  >
    <path d="m1 1 3 3 3-3" strokeLinecap="round" strokeLinejoin="round" />
  </svg>
);

/**
 * Flat white 58px header from the Figma "Header" component: logo left, six links centred, then
 * search, language and the sky-blue "Contact Us" pill. Absolutely positioned over the top of the page (pages
 * that start with a hero leave 58px for it) and scrolls away with the page.
 */
export function SiteHeader({ serviceNav }: { serviceNav?: ServiceNav }) {
  const t = useTranslations("cta");
  // The Services menu is built from the CMS: one column per category, its service pages beneath.
  const navItems: PrimaryNavItem[] = HEADER_NAV.map((item) =>
    item.label === "Services" && serviceNav && serviceNav.categories.length > 0
      ? {
          ...item,
          hasDropdown: true,
          groups: serviceNav.categories.map((c) => ({
            label: c.name,
            links: c.services.map((s) => ({ label: s.label, href: `/services/${c.slug}/${s.slug}` })),
          })),
        }
      : item,
  );
  const [openKey, setOpenKey] = useState<string | null>(null);
  const openItem = navItems.find((i) => i.label === openKey && (i.children || i.groups));

  return (
    <header
      className="absolute inset-x-0 top-0 z-[100] h-[58px] bg-white"
      style={{ boxShadow: "0 4px 9.8px rgba(0,0,0,.15)" }}
      onMouseLeave={() => setOpenKey(null)}
      onKeyDown={(e) => {
        if (e.key === "Escape") setOpenKey(null);
      }}
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
          {navItems.map((item) => {
            const cls = "flex items-center gap-1.5 leading-6 transition-colors hover:text-[#00a9ff]";
            if (item.children || item.groups) {
              const open = openKey === item.label;
              return (
                <button
                  key={item.label}
                  type="button"
                  className={`${cls} ${open ? "text-[#00a9ff]" : ""}`}
                  aria-expanded={open}
                  aria-haspopup="true"
                  onMouseEnter={() => setOpenKey(item.label)}
                  onFocus={() => setOpenKey(item.label)}
                  onClick={() => setOpenKey(open ? null : item.label)}
                >
                  {item.label}
                  <Chevron open={open} />
                </button>
              );
            }
            const inner = (
              <>
                {item.label}
                {item.hasDropdown && <Chevron />}
              </>
            );
            return item.href ? (
              <Link key={item.label} href={item.href} className={cls} onMouseEnter={() => setOpenKey(null)}>
                {inner}
              </Link>
            ) : (
              <a key={item.label} href="#" className={cls} onMouseEnter={() => setOpenKey(null)}>
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

      {/* Dropdown panel (Figma "Service Dropdown"): a full-width navy band under the header with the
          sub-pages as light-blue links. */}
      {openItem && (
        <div className="absolute inset-x-0 top-full bg-[#0e192e]">
          {openItem.groups ? (
            <div
              className="mx-auto flex max-w-[1920px] flex-wrap gap-x-[clamp(32px,5vw,96px)] gap-y-8 pt-[31px] pb-12"
              style={{ paddingLeft: "min(13.4vw, 257px)", paddingRight: 24 }}
            >
              {openItem.groups.map((group) => (
                <div key={group.label} className="min-w-[150px]">
                  <div className="text-base leading-none font-semibold text-[#b5e2ff]">{group.label}</div>
                  <ul className="mt-[22px] flex flex-col gap-[3px]">
                    {group.links.map((link) => (
                      <li key={link.href}>
                        <Link
                          href={link.href}
                          className="text-base leading-[25px] text-white transition-colors hover:text-[#b5e2ff]"
                          onClick={() => setOpenKey(null)}
                        >
                          {link.label}
                        </Link>
                      </li>
                    ))}
                  </ul>
                </div>
              ))}
            </div>
          ) : (
            <ul
              className="mx-auto flex max-w-[1920px] flex-wrap gap-x-[clamp(32px,5vw,96px)] gap-y-3 py-[31px] pb-10"
              style={{ paddingLeft: "min(13.4vw, 257px)", paddingRight: 24 }}
            >
              {openItem.children?.map((child) => (
                <li key={child.href}>
                  <Link
                    href={child.href}
                    className="text-base leading-none font-semibold text-[#b5e2ff] transition-colors hover:text-white"
                    onClick={() => setOpenKey(null)}
                  >
                    {child.label}
                  </Link>
                </li>
              ))}
            </ul>
          )}
        </div>
      )}
    </header>
  );
}
