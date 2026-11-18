"use client";

import { useRouter } from "@/i18n/navigation";

/**
 * A card's category label as a link to the category's page. The card itself is one big link, and a link
 * cannot sit inside a link, so this is a labelled control that navigates on click or Enter.
 */
export function CategoryPill({ href, children }: { href: string; children: string }) {
  const router = useRouter();
  const go = () => router.push(href);
  return (
    <span
      role="link"
      tabIndex={0}
      className="psp-pill psp-pill--link"
      aria-label={`Category: ${children}`}
      onClick={(e) => {
        e.preventDefault();
        e.stopPropagation();
        go();
      }}
      onKeyDown={(e) => {
        if (e.key === "Enter") {
          e.preventDefault();
          e.stopPropagation();
          go();
        }
      }}
    >
      {children}
    </span>
  );
}
