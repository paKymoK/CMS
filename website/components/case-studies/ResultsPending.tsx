"use client";

import { createContext, useContext, useEffect, useState, type ReactNode } from "react";
import { useLinkStatus } from "next/link";

const PendingContext = createContext<(pending: boolean) => void>(() => {});

type Registry = { pending: boolean; set: (pending: boolean) => void };
const RegistryContext = createContext<Registry>({ pending: false, set: () => {} });

/**
 * Lets the filter bar, the pager and the results talk about one navigation: while a page or filter
 * change is loading, the old results stay on screen and fade slightly instead of blanking out.
 */
export function ResultsPendingProvider({ children }: { children: ReactNode }) {
  const [pending, set] = useState(false);
  return (
    <RegistryContext.Provider value={{ pending, set }}>
      <PendingContext.Provider value={set}>{children}</PendingContext.Provider>
    </RegistryContext.Provider>
  );
}

/** Wraps the results (count, grid, pager); dims while a navigation is pending. */
export function PendingFade({ children }: { children: ReactNode }) {
  const { pending } = useContext(RegistryContext);
  return (
    <div className="csl-fade" data-pending={pending || undefined} aria-busy={pending}>
      {children}
    </div>
  );
}

/** Report a transition (e.g. the filter bar's router.push) from outside a <Link>. */
export function useReportPending(pending: boolean) {
  const set = useContext(PendingContext);
  useEffect(() => {
    set(pending);
    return () => set(false);
  }, [pending, set]);
}

/** Render inside a <Link>: reports that link's pending navigation to the results. */
export function LinkPendingFlag() {
  const { pending } = useLinkStatus();
  useReportPending(pending);
  return null;
}
