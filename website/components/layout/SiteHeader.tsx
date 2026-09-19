"use client";

import { useEffect, useRef, useState } from "react";
import Image from "next/image";
import { Link } from "@/i18n/navigation";
import { useTranslations } from "next-intl";
import { PRIMARY_NAV } from "@/content/nav";

export function SiteHeader() {
  const t = useTranslations("cta");
  const [hidden, setHidden] = useState(false);
  const lastY = useRef(0);
  const raf = useRef<number | null>(null);

  useEffect(() => {
    lastY.current = window.scrollY;

    const onScroll = () => {
      if (raf.current !== null) return;
      raf.current = requestAnimationFrame(() => {
        raf.current = null;
        const y = window.scrollY;
        const dy = y - lastY.current;
        if (Math.abs(dy) > 4) {
          setHidden(dy > 0 && y > 120);
          lastY.current = y;
        }
      });
    };

    window.addEventListener("scroll", onScroll, { passive: true });
    return () => {
      window.removeEventListener("scroll", onScroll);
      if (raf.current !== null) cancelAnimationFrame(raf.current);
    };
  }, []);

  return (
    <header
      className={`fixed inset-x-0 top-[18px] z-[100] flex justify-center px-6 transition-[transform,opacity] duration-500 ease-out ${
        hidden ? "-translate-y-[140%] opacity-0" : "translate-y-0 opacity-100"
      }`}
    >
      <div className="flex h-[68px] w-full max-w-[1180px] items-center justify-between gap-6 rounded-[18px] border border-brand-primary/20 bg-white/85 pl-6 pr-4 shadow-[0_10px_34px_rgba(10,37,64,0.1),0_2px_6px_rgba(10,37,64,0.05)] backdrop-blur-xl">
        <Link href="/" className="flex items-center" aria-label="CMC Global home">
          <Image
            src="/images/logo.svg"
            alt="CMC Global"
            width={135}
            height={30}
            priority
            className="h-[30px] w-auto"
          />
        </Link>

        <nav className="hidden items-center gap-6 whitespace-nowrap text-[15px] font-medium text-[#3c4858] md:flex">
          {PRIMARY_NAV.map((item) => (
            <a key={item.label} href="#" className="flex items-center gap-1.5 transition-colors hover:text-brand-primary">
              {item.label}
              {item.hasDropdown && (
                <svg viewBox="0 0 10 6" className="h-[7px] w-[11px] fill-none stroke-current stroke-[1.5]" aria-hidden>
                  <path d="M1 1l4 4 4-4" strokeLinecap="round" strokeLinejoin="round" />
                </svg>
              )}
            </a>
          ))}
        </nav>

        <div className="flex items-center gap-3.5">
          <div className="hidden flex-none items-center gap-1.5 rounded-[9px] border border-[#dceaf5] px-3 py-2 text-sm text-[#3c4858] sm:flex">
            EN
            <svg viewBox="0 0 10 6" className="h-[7px] w-[11px] fill-none stroke-current stroke-[1.5]" aria-hidden>
              <path d="M1 1l4 4 4-4" strokeLinecap="round" strokeLinejoin="round" />
            </svg>
          </div>
          <a
            href="#contact"
            className="flex-none whitespace-nowrap rounded-[10px] bg-gradient-to-r from-brand-primary to-brand-accent px-[22px] py-[11px] text-sm font-bold text-white shadow-[0_10px_24px_rgba(24,159,224,0.36)] transition-colors hover:bg-brand-primary-dark"
          >
            {t("contactUs")}
          </a>
        </div>
      </div>
    </header>
  );
}
