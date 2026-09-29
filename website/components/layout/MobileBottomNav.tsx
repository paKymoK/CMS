import { Link } from "@/i18n/navigation";
import { PRIMARY_NAV } from "@/content/nav";

const NAV_ICONS: Record<string, React.ReactNode> = {
  Services: (
    <>
      <rect x="3.5" y="3.5" width="7" height="7" rx="1.4" />
      <rect x="13.5" y="3.5" width="7" height="7" rx="1.4" />
      <rect x="3.5" y="13.5" width="7" height="7" rx="1.4" />
      <rect x="13.5" y="13.5" width="7" height="7" rx="1.4" />
    </>
  ),
  "Case Studies": (
    <>
      <path d="M4 20V10M12 20V4M20 20v-7" strokeLinecap="round" strokeLinejoin="round" />
      <path d="M2 20h20" strokeLinecap="round" />
    </>
  ),
  "About Us": (
    <>
      <circle cx="12" cy="8" r="3.2" />
      <path d="M5 20c0-3.9 3.1-7 7-7s7 3.1 7 7" strokeLinecap="round" />
    </>
  ),
  Resources: (
    <>
      <path
        d="M6 3h8l4 4v13a1 1 0 0 1-1 1H6a1 1 0 0 1-1-1V4a1 1 0 0 1 1-1z"
        strokeLinecap="round"
        strokeLinejoin="round"
      />
      <path d="M14 3v4h4" strokeLinecap="round" strokeLinejoin="round" />
      <path d="M8 12h8M8 16h5" strokeLinecap="round" />
    </>
  ),
  Careers: (
    <>
      <rect x="3" y="8" width="18" height="11" rx="1.4" />
      <path d="M8 8V6a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2" strokeLinecap="round" strokeLinejoin="round" />
      <path d="M3 13h18" />
    </>
  ),
  Insights: (
    <>
      <path d="M4 4h13l3 3v13a1 1 0 0 1-1 1H4a1 1 0 0 1-1-1V5a1 1 0 0 1 1-1z" strokeLinecap="round" strokeLinejoin="round" />
      <path d="M7 9h8M7 13h8M7 17h5" strokeLinecap="round" />
    </>
  ),
};

export function MobileBottomNav() {
  return (
    <nav
      aria-label="Primary"
      className="fixed inset-x-0 bottom-0 z-[100] flex min-h-[64px] items-stretch justify-around border-t border-black/8 bg-white/95 shadow-[0_-8px_24px_rgba(10,37,64,0.08)] backdrop-blur-xl md:hidden"
      style={{ paddingBottom: "env(safe-area-inset-bottom)" }}
    >
      {PRIMARY_NAV.map((item) => {
        const content = (
          <>
            <svg
              viewBox="0 0 24 24"
              className="h-[22px] w-[22px] fill-none stroke-current stroke-[1.6]"
              aria-hidden
            >
              {NAV_ICONS[item.label]}
            </svg>
            <span className="leading-none whitespace-nowrap">{item.label}</span>
          </>
        );
        const className =
          "flex flex-1 flex-col items-center justify-center gap-1 py-2 text-[10.5px] font-medium text-[#3c4858] transition-colors active:text-brand-primary";
        return item.href ? (
          <Link key={item.label} href={item.href} className={className}>
            {content}
          </Link>
        ) : (
          <a key={item.label} href="#" className={className}>
            {content}
          </a>
        );
      })}
    </nav>
  );
}
