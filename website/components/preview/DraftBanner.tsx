"use client";

import { useEffect } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";

// A little under content-service's 15-minute preview_token TTL, so the token never lapses while
// this tab stays open — content-service's refresh rotates it, this just keeps that rotation going
// in the background so an editor mid-review is never surprised by an expired session.
const RENEW_INTERVAL_MS = 10 * 60 * 1000;

export function DraftBanner() {
  const router = useRouter();

  useEffect(() => {
    const interval = setInterval(() => {
      fetch("/api/preview/renew", { method: "POST" }).catch(() => {
        // best-effort — if this fails, the token simply lapses and the next real request (or the
        // next renew attempt) surfaces the expiry clearly rather than failing silently forever
      });
    }, RENEW_INTERVAL_MS);
    return () => clearInterval(interval);
  }, []);

  return (
    <div className="fixed inset-x-0 top-0 z-50 flex items-center justify-center gap-4 bg-amber-400 px-4 py-2 text-sm font-medium text-amber-950">
      <span>You&apos;re previewing draft content — visitors don&apos;t see this.</span>
      <button
        type="button"
        onClick={() => router.refresh()}
        className="rounded bg-amber-950/10 px-2 py-1 hover:bg-amber-950/20"
      >
        Refresh preview
      </button>
      <Link
        href="/api/preview/exit"
        className="rounded bg-amber-950/10 px-2 py-1 hover:bg-amber-950/20"
      >
        Exit preview
      </Link>
    </div>
  );
}
