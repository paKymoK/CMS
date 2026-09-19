import Image from "next/image";
import { getTranslations } from "next-intl/server";
import type { FooterNavCategory } from "@/content/home/types";

const SOCIAL_LINKS = [
  {
    name: "Facebook",
    href: "#",
    path: "M22 12a10 10 0 1 0-11.56 9.88v-6.99H7.9V12h2.54V9.8c0-2.5 1.49-3.89 3.78-3.89 1.1 0 2.24.2 2.24.2v2.46h-1.26c-1.24 0-1.63.77-1.63 1.56V12h2.78l-.44 2.89h-2.34v6.99A10 10 0 0 0 22 12Z",
  },
  {
    name: "YouTube",
    href: "#",
    path: "M23.5 6.5a3 3 0 0 0-2.1-2.1C19.5 4 12 4 12 4s-7.5 0-9.4.4A3 3 0 0 0 .5 6.5 31 31 0 0 0 0 12a31 31 0 0 0 .5 5.5 3 3 0 0 0 2.1 2.1C4.5 20 12 20 12 20s7.5 0 9.4-.4a3 3 0 0 0 2.1-2.1A31 31 0 0 0 24 12a31 31 0 0 0-.5-5.5ZM9.6 15.5v-7l6.3 3.5-6.3 3.5Z",
  },
  {
    name: "LinkedIn",
    href: "#",
    path: "M20.45 20.45h-3.55v-5.57c0-1.33-.02-3.04-1.85-3.04-1.86 0-2.15 1.45-2.15 2.94v5.67H9.35V9h3.41v1.56h.05c.47-.9 1.63-1.85 3.36-1.85 3.6 0 4.27 2.37 4.27 5.45v6.29ZM5.34 7.43a2.06 2.06 0 1 1 0-4.12 2.06 2.06 0 0 1 0 4.12ZM7.12 20.45H3.56V9h3.56v11.45Z",
  },
];

export async function SiteFooter({ footerNav }: { footerNav: FooterNavCategory[] }) {
  const t = await getTranslations("footer");
  const year = new Date().getFullYear();

  return (
    <footer className="bg-brand-primary-dark pt-[70px] pb-10 text-white/72">
      <div className="mx-auto max-w-6xl px-6">
        <div className="grid grid-cols-2 gap-x-8 gap-y-10 sm:grid-cols-4">
          <div className="col-span-2 sm:col-span-4 lg:col-span-1">
            <Image
              src="/images/logo-full.svg"
              alt="CMC Global"
              width={533.76}
              height={118.46}
              className="h-8 w-auto brightness-0 invert"
            />
            <p className="mt-[18px] max-w-[290px] text-[14.5px] leading-relaxed">
              One-stop IT solutions — tech enablement, digital transformation,
              AI-driven innovation and CMC Solutions, delivered across 30+
              countries.
            </p>
          </div>
          {footerNav.map((category) => (
            <div key={category.label}>
              <h3 className="text-xs font-bold uppercase tracking-[0.1em] text-white">
                {category.label}
              </h3>
              <ul className="mt-[11px] space-y-[11px] text-[14.5px]">
                {category.links.map((link) => (
                  <li key={link}>
                    <a href="#" className="transition-colors hover:text-brand-accent">
                      {link}
                    </a>
                  </li>
                ))}
              </ul>
            </div>
          ))}
        </div>

        <div className="mt-11 flex flex-col items-center justify-between gap-6 border-t border-white/20 pt-[22px] sm:flex-row">
          <div className="flex items-center gap-3">
            {SOCIAL_LINKS.map((social) => (
              <a
                key={social.name}
                href={social.href}
                aria-label={social.name}
                className="flex h-8 w-8 items-center justify-center rounded-full bg-white/10 text-white transition-colors hover:bg-brand-accent hover:text-brand-primary-dark"
              >
                <svg viewBox="0 0 24 24" className="h-4 w-4 fill-current" aria-hidden>
                  <path d={social.path} />
                </svg>
              </a>
            ))}
          </div>
          <p className="text-[13px]">{t("copyright", { year })}</p>
        </div>
      </div>
    </footer>
  );
}
