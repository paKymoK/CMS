import { Modal, message } from "antd";
import type { BulkResult } from "../lib/postList";

/**
 * Tells the person how a bulk action went: a short message when every item was done, otherwise a
 * list of the ones that were not, with the reason for each (the others went ahead).
 */
export function showBulkResult(result: BulkResult, done: string, noun = "post") {
  const n = result.updated.length;
  if (result.failed.length === 0) {
    message.success(`${done} ${n} ${n === 1 ? noun : `${noun}s`}`);
    return;
  }
  Modal.warning({
    title: `${n} changed, ${result.failed.length} could not be`,
    content: (
      <ul style={{ margin: "8px 0 0", paddingLeft: 18 }}>
        {result.failed.map((f) => (
          <li key={f.id}>
            <b>{f.title || `${noun} ${f.id}`}</b>: {f.reason}
          </li>
        ))}
      </ul>
    ),
  });
}
