import { useCallback, useEffect, useState } from "react";
import { Select, Button, Empty } from "antd";
import { LogoutIcon, MenuIcon, CloseIcon, ChevronRightIcon, ChevronsLeftIcon } from "./icons";
import { Link, Outlet, useLocation } from "react-router-dom";
import { useAuth } from "../auth/useAuth";
import { useSiteAccess } from "../auth/useSiteAccess";
import { useSite } from "../lib/useSite";
import AdminSearchBox from "./AdminSearchBox";
import { NAVIGATION, type NavItem } from "../config/navigation";
import { SITES } from "../config/sites";

// Under an "About Page" group, "About · Hero" repeats its own heading — drop the redundant prefix
// in the sidebar (the page title in the topbar keeps the full label).
const stripGroupPrefix = (label: string) => label.replace(/^About · /, "");

// The post / case-study editors live outside /content/*, but belong to a sidebar entry.
function navKeyFor(pathname: string): string {
  if (pathname.startsWith("/posts/")) return "/content/posts";
  if (pathname.startsWith("/case-studies/")) return "/content/case-studies";
  return pathname;
}

function findGroupLabel(items: NavItem[], key: string): string | undefined {
  for (const item of items) {
    if (item.items?.some((child) => child.key === key)) return item.label;
  }
  return undefined;
}

function findLeafLabel(items: NavItem[], key: string): string | undefined {
  for (const item of items) {
    if (item.key === key) return item.label;
    const child = item.items?.find((c) => c.key === key);
    if (child) return child.label;
  }
  return undefined;
}

function initialsOf(name: string): string {
  const parts = name.trim().split(/\s+/);
  if (parts.length >= 2) return (parts[0][0] + parts[1][0]).toUpperCase();
  return name.slice(0, 2).toUpperCase();
}

// Sidebar preferences survive reloads: whether it is collapsed to the icon rail, and which groups
// the user opened or closed by hand. A group nobody touched follows the current page (see below).
const NAV_PREFS_KEY = "cms.nav.v1";
interface NavPrefs {
  rail: boolean;
  open: Record<string, boolean>;
}

function loadNavPrefs(): NavPrefs {
  try {
    const raw = localStorage.getItem(NAV_PREFS_KEY);
    if (raw) {
      const parsed = JSON.parse(raw) as Partial<NavPrefs>;
      return {
        rail: parsed.rail === true,
        open: parsed.open && typeof parsed.open === "object" ? parsed.open : {},
      };
    }
  } catch {
    // Private mode / blocked storage / corrupt JSON — fall through to the defaults.
  }
  return { rail: false, open: {} };
}

function saveNavPrefs(prefs: NavPrefs) {
  try {
    localStorage.setItem(NAV_PREFS_KEY, JSON.stringify(prefs));
  } catch {
    // Storage unavailable: the sidebar just forgets its state on reload.
  }
}

const MOBILE_QUERY = "(max-width: 768px)";

function useIsMobile(): boolean {
  const [mobile, setMobile] = useState(() => window.matchMedia(MOBILE_QUERY).matches);
  useEffect(() => {
    const mq = window.matchMedia(MOBILE_QUERY);
    const onChange = () => setMobile(mq.matches);
    mq.addEventListener("change", onChange);
    return () => mq.removeEventListener("change", onChange);
  }, []);
  return mobile;
}

function NavGlyph({ d }: { d?: string }) {
  return (
    <svg
      className="cms-nav-glyph"
      width="18"
      height="18"
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      strokeWidth="1.8"
      strokeLinecap="round"
      strokeLinejoin="round"
      aria-hidden
    >
      <path d={d ?? "M5 12h14"} />
    </svg>
  );
}

export default function AppShell() {
  const { logout, user } = useAuth();
  const { isGlobalAdmin } = useSiteAccess();
  const { site, setSite, accessibleSites } = useSite();
  const location = useLocation();
  const [navOpen, setNavOpen] = useState(false);
  const [prefs, setPrefs] = useState<NavPrefs>(loadNavPrefs);
  const isMobile = useIsMobile();
  // The icon rail only exists on desktop; the phone drawer always shows the full nav.
  const rail = prefs.rail && !isMobile;

  const updatePrefs = useCallback((next: (p: NavPrefs) => NavPrefs) => {
    setPrefs((prev) => {
      const value = next(prev);
      saveNavPrefs(value);
      return value;
    });
  }, []);

  const activeKey = navKeyFor(location.pathname);

  // The drawer is a modal on phones: Escape closes it (picking a link closes it too, see onClick).
  useEffect(() => {
    if (!navOpen) return;
    const onKey = (e: KeyboardEvent) => {
      if (e.key === "Escape") setNavOpen(false);
    };
    window.addEventListener("keydown", onKey);
    return () => window.removeEventListener("keydown", onKey);
  }, [navOpen]);

  // The nav scrolls on its own, so bring the screen you're on into view (e.g. after a deep link).
  useEffect(() => {
    document.querySelector(".cms-sider .cms-nav-link.is-active")?.scrollIntoView({ block: "nearest" });
  }, [activeKey, rail]);

  const onSearchPage = location.pathname === "/search";
  const crumbGroup = onSearchPage ? "Content" : (findGroupLabel(NAVIGATION, activeKey) ?? "");
  const pageTitle = onSearchPage ? "Search" : (findLeafLabel(NAVIGATION, activeKey) ?? "");

  const displayName = (user?.name as string) || (user?.email as string) || "Signed in";
  const roleLine = isGlobalAdmin
    ? "Global admin"
    : `Site editor · ${accessibleSites.map((c) => c.toUpperCase()).join(", ")}`;

  if (accessibleSites.length === 0) {
    return (
      <div className="flex min-h-screen items-center justify-center">
        <Empty description="Your account has no CMS site access assigned. Ask a global admin to grant you access to at least one site." />
      </div>
    );
  }

  const onGroupClick = (key: string, isOpen: boolean) => {
    // In the rail there is no room for the items, so a group icon expands the sidebar and opens it.
    if (rail) updatePrefs((p) => ({ rail: false, open: { ...p.open, [key]: true } }));
    else updatePrefs((p) => ({ ...p, open: { ...p.open, [key]: !isOpen } }));
  };

  return (
    <div className="cms-shell">
      <aside
        className={`cms-sider${navOpen ? " cms-sider-open" : ""}${rail ? " is-rail" : ""}`}
        aria-label="Main navigation"
      >
        <div className="cms-sider-head">
          <img className="cms-brand-full" src="/logo.svg" alt="CMC Global" />
          <span className="cms-brand-pill">CMS</span>
          <span className="cms-brand-mark" aria-hidden>
            C
          </span>
          <button type="button" className="cms-nav-close" aria-label="Close navigation" onClick={() => setNavOpen(false)}>
            <CloseIcon />
          </button>
        </div>

        <nav className="cms-nav" aria-label="Sections">
          {NAVIGATION.map((group) => {
            if (!group.items) {
              const active = group.key === activeKey;
              return (
                <Link
                  key={group.key}
                  to={group.key}
                  title={group.label}
                  className={`cms-nav-head cms-nav-link${active ? " is-active" : ""}`}
                  aria-current={active ? "page" : undefined}
                  onClick={() => setNavOpen(false)}
                >
                  <NavGlyph d={group.icon} />
                  <span className="cms-nav-text">{group.label}</span>
                </Link>
              );
            }
            const hasActive = group.items.some((c) => c.key === activeKey);
            const isOpen = prefs.open[group.key] ?? hasActive;
            return (
              <div key={group.key} className="cms-nav-group">
                <button
                  type="button"
                  className={`cms-nav-head${hasActive ? " has-active" : ""}`}
                  aria-expanded={rail ? undefined : isOpen}
                  aria-controls={`nav-${group.key}`}
                  aria-label={rail ? group.label : undefined}
                  title={group.label}
                  onClick={() => onGroupClick(group.key, isOpen)}
                >
                  <NavGlyph d={group.icon} />
                  <span className="cms-nav-text">{group.label}</span>
                  <span className="cms-nav-count">{group.items.length}</span>
                  <ChevronRightIcon className={`cms-nav-chev${isOpen ? " is-open" : ""}`} />
                </button>
                {isOpen && !rail && (
                  <div className="cms-nav-sub" id={`nav-${group.key}`}>
                    {group.items.map((child) => {
                      const active = child.key === activeKey;
                      return (
                        <Link
                          key={child.key}
                          to={child.key}
                          className={`cms-nav-link${active ? " is-active" : ""}`}
                          aria-current={active ? "page" : undefined}
                          onClick={() => setNavOpen(false)}
                        >
                          {stripGroupPrefix(child.label)}
                        </Link>
                      );
                    })}
                  </div>
                )}
              </div>
            );
          })}
        </nav>

        <div className="cms-user">
          <div className="cms-user-avatar">{initialsOf(displayName)}</div>
          <div className="cms-user-text">
            <span className="cms-user-name">{displayName}</span>
            <span className="cms-user-role">{roleLine}</span>
          </div>
          <Button
            type="text"
            size="small"
            icon={<LogoutIcon />}
            onClick={logout}
            title="Log out"
            aria-label="Log out"
            className="cms-user-logout"
          />
        </div>

        <button
          type="button"
          className="cms-rail-toggle"
          onClick={() => updatePrefs((p) => ({ ...p, rail: !p.rail }))}
          aria-label={rail ? "Expand sidebar" : "Collapse sidebar"}
          title={rail ? "Expand sidebar" : "Collapse sidebar"}
        >
          <ChevronsLeftIcon className={rail ? "is-flipped" : ""} />
          <span className="cms-nav-text">Collapse sidebar</span>
        </button>
      </aside>

      <div className={`cms-nav-backdrop${navOpen ? " open" : ""}`} onClick={() => setNavOpen(false)} aria-hidden />

      <div className="cms-main">
        <header className="cms-topbar">
          <div className="cms-crumbs">
            <button
              type="button"
              className="cms-nav-toggle"
              aria-label="Open navigation"
              aria-expanded={navOpen}
              onClick={() => setNavOpen(true)}
            >
              <MenuIcon />
            </button>
            <span className="cms-crumb-group">{crumbGroup}</span>
            {crumbGroup && <span className="cms-crumb-sep">/</span>}
            <span className="cms-crumb-page">{pageTitle}</span>
          </div>
          <div className="cms-topbar-actions">
            <AdminSearchBox />
            <Select
              value={site ?? undefined}
              className="cms-site-select"
              style={{ width: 170 }}
              onChange={setSite}
              options={accessibleSites.map((code) => ({
                value: code,
                label: SITES.find((s) => s.code === code)?.label ?? code,
              }))}
            />
          </div>
        </header>
        <main className="cms-content" style={{ flex: 1, width: "100%", margin: "0 auto" }}>
          <div key={location.pathname} className="cms-page-enter">
            <Outlet />
          </div>
        </main>
      </div>
    </div>
  );
}
