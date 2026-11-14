import { Modal, message } from "antd";
import { PAGE_TRASH_DAYS, bulkPages, pageTrashLinks } from "../lib/pageList";
import type { BulkResult, LinkCheck } from "../lib/postList";
import { showBulkResult } from "./bulkResult";

/**
 * Asks before moving pages to the Trash: what links to them or puts them in a menu is listed first (a
 * warning, never a block), and everything says it can be restored for 30 days. `onDone` gets the result
 * once the server has answered.
 */
export async function confirmMovePagesToTrash(
  site: string,
  pages: { id: number; title: string | null }[],
  onDone: (result: BulkResult) => void,
) {
  let links: LinkCheck[] = [];
  try {
    links = await pageTrashLinks(
      site,
      pages.map((p) => p.id),
    );
  } catch {
    message.error("Could not check for links to these pages. Nothing was changed.");
    return;
  }
  const many = pages.length > 1;
  Modal.confirm({
    title: `Move ${many ? `${pages.length} pages` : `“${pages[0].title || "this page"}”`} to the Trash?`,
    width: links.length ? 580 : 440,
    content: (
      <div>
        {links.length > 0 && (
          <div style={{ marginBottom: 10 }}>
            <b style={{ color: "#b42318" }}>{many ? "Some of these pages are" : "This page is"} in use.</b> Those places will lose it:
            <ul style={{ margin: "6px 0 0", paddingLeft: 18 }}>
              {links.map((l) => (
                <li key={l.id}>
                  <b>{l.title || `Page ${l.id}`}</b>: {l.referrers.map((r) => r.title || `item ${r.id}`).join(", ")}
                  {l.referrerCount > l.referrers.length ? ` and ${l.referrerCount - l.referrers.length} more` : ""}
                </li>
              ))}
            </ul>
          </div>
        )}
        Its address stops working while it is in the Trash. It can be restored (as a draft) for {PAGE_TRASH_DAYS} days, then it is deleted for good.
      </div>
    ),
    okText: links.length ? "Move to Trash anyway" : "Move to Trash",
    okButtonProps: { danger: links.length > 0 },
    onOk: async () => {
      try {
        const result = await bulkPages(
          site,
          pages.map((p) => p.id),
          "TRASH",
        );
        showBulkResult(result, "Moved to Trash:", "page");
        onDone(result);
      } catch {
        message.error("The change could not be made. Nothing was changed.");
      }
    },
  });
}
