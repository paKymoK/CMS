"use client";

import { useTransition } from "react";
import { useRouter } from "@/i18n/navigation";
import {
  perspectivesHref,
  type PerspectivesQuery,
} from "@/lib/cms/perspectivesNav";
import { useReportPending } from "@/components/list/ResultsPending";

/** "Sort by" control of the Perspectives results: newest first (default) or oldest first. */
export function PerspectivesSort({
  current,
}: {
  current: Pick<PerspectivesQuery, "category" | "tag" | "q" | "sort">;
}) {
  const router = useRouter();
  const [isPending, startTransition] = useTransition();
  useReportPending(isPending);

  return (
    <label className="psp-sort">
      <span>Sort by</span>
      <span className="psp-sort-box">
        <select
          value={current.sort ?? "newest"}
          onChange={(e) =>
            startTransition(() =>
              router.push(
                perspectivesHref({
                  ...current,
                  sort: e.target.value === "oldest" ? "oldest" : undefined,
                  page: 1,
                }),
              ),
            )
          }
        >
          <option value="newest">Newest</option>
          <option value="oldest">Oldest</option>
        </select>
        <svg viewBox="0 0 12 8" className="psp-sort-caret" aria-hidden>
          <path
            d="m1 1.5 5 5 5-5"
            fill="none"
            stroke="currentColor"
            strokeWidth="2"
          />
        </svg>
      </span>
    </label>
  );
}
