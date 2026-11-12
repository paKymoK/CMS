export interface SiteHeaderBarItem {
  label: string;
  hasDropdown?: boolean;
}

const Chevron = () => (
  <svg viewBox="0 0 8 5" className="h-[5px] w-2 fill-none stroke-current stroke-[1.4]" aria-hidden>
    <path d="m1 1 3 3 3-3" strokeLinecap="round" strokeLinejoin="round" />
  </svg>
);

/**
 * A still picture of the website's header, for the admin's preview: same size, spacing and colours,
 * but no menus open and nothing to click. The live header (website/components/layout/SiteHeader.tsx)
 * is interactive and reads Next.js routing, so it cannot be shared; keep the two looking alike.
 * Absolutely positioned over the top of its (relative) parent, like the live one.
 */
export function SiteHeaderBar({ items, logoSrc, contactLabel }: { items: SiteHeaderBarItem[]; logoSrc: string; contactLabel: string }) {
  return (
    <header className="absolute inset-x-0 top-0 z-[100] h-[58px] bg-white" style={{ boxShadow: "0 4px 9.8px rgba(0,0,0,.15)" }}>
      <div className="mx-auto grid h-full w-full max-w-[1458px] grid-cols-[auto_1fr_auto] items-center gap-6 px-6 lg:grid-cols-[1fr_auto_1fr]">
        <span className="flex items-center">
          <img src={logoSrc} alt="CMC Global" width={150} height={31} className="h-[31px] w-auto" />
        </span>
        <nav
          aria-label="Primary"
          className="hidden items-center justify-center gap-[clamp(28px,3.9vw,76px)] text-base font-medium whitespace-nowrap text-[#7e7e7e] lg:flex"
        >
          {items.map((item) => (
            <span key={item.label} className="flex items-center gap-1.5 leading-6">
              {item.label}
              {item.hasDropdown && <Chevron />}
            </span>
          ))}
        </nav>
        <div className="flex items-center justify-end gap-[clamp(16px,1.8vw,28px)]">
          <svg viewBox="0 0 18 18" className="hidden h-[18px] w-[18px] fill-none stroke-[#747474] stroke-[1.6] sm:block" aria-hidden>
            <circle cx="8" cy="8" r="6" />
            <path d="m12.5 12.5 4 4" strokeLinecap="round" />
          </svg>
          <span className="hidden items-center gap-1 text-base text-[#747474] sm:flex">EN</span>
          <span className="flex h-[30px] items-center rounded-[46px] bg-[#00bbff] px-[13px] text-xs font-semibold whitespace-nowrap text-white">
            {contactLabel}
          </span>
        </div>
      </div>
    </header>
  );
}
