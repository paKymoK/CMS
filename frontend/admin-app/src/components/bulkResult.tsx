import { Modal, message } from "antd";
import type { BulkResult } from "../lib/postList";

/**
 * Tells the person how a bulk action went: a short message when every post was done, otherwise a
 * list of the ones that were not, with the reason for each (the others went ahead).
 */
export function showBulkResult(result: BulkResult, done: string) {
  const n = result.updated.length;
  if (result.failed.length === 0) {
    message.success(`${done} ${n} post${n === 1 ? "" : "s"}`);
    return;
  }
  Modal.warning({
    title: `${n} changed, ${result.failed.length} could not be`,
    content: (
      <ul style={{ margin: "8px 0 0", paddingLeft: 18 }}>
        {result.failed.map((f) => (
          <li key={f.id}>
            <b>{f.title || `Post ${f.id}`}</b>: {f.reason}
          </li>
        ))}
      </ul>
    ),
  });
}
