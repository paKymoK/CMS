import { useMutation } from "@tanstack/react-query";
import { message } from "antd";
import { openPreview } from "./preview";

interface Options {
  site: string | null;
  /** Public path segment for this content type, e.g. "insights" or "case-studies". */
  section: "insights" | "case-studies";
  isNew: boolean;
  dirty: boolean;
  /** Status as stored on the server — not the editor's possibly-unsaved status toggle. */
  serverStatus: string | undefined;
  slug: string;
  /** Saves the form as a draft and resolves with the saved record's slug. */
  saveAsDraft: () => Promise<string | null>;
}

/**
 * One item's preview. Preview reads the *saved* row (the same row visitors would see once
 * published), so this decides what to do about unsaved edits:
 *
 * - New item, or a draft with unsaved edits: save as DRAFT first — harmless, it's not live.
 * - Published item with unsaved edits: do NOT save — that would push the edits live. Preview the
 *   last saved version and say so.
 * - Nothing unsaved: just preview.
 */
export function usePreviewItem(opts: Options) {
  return useMutation({
    mutationFn: async () => {
      if (!opts.site) return;
      let slug: string | null = opts.slug || null;
      const isDraft = !opts.serverStatus || opts.serverStatus === "DRAFT";
      if (opts.isNew || (opts.dirty && isDraft)) {
        slug = await opts.saveAsDraft();
      } else if (opts.dirty) {
        message.warning("Unsaved changes aren't in the preview — it shows the last saved version.");
      }
      if (!slug) {
        message.error("Save the item first — it has no slug yet.");
        return;
      }
      await openPreview(opts.site, `/${opts.site}/${opts.section}/${slug}`);
    },
  });
}
