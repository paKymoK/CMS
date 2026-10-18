import { useState } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { Button, Drawer, Empty, Popconfirm, Spin, Tag, message } from "antd";
import dayjs from "dayjs";
import ContentDiff from "./ContentDiff";
import { useSite } from "../lib/useSite";
import {
  ACTION_COLORS,
  ACTION_LABELS,
  fieldLabel,
  getAuditEntry,
  listAudit,
  restoreAudit,
  type AuditEntry,
} from "../lib/audit";

const HISTORY_LIMIT = 100;
const SHOWN_FIELDS = 5;

/** One entry's changes, loaded when it is opened (the list itself never carries the snapshots). */
function EntryChanges({ entry }: { entry: AuditEntry }) {
  const { site } = useSite();
  const detail = useQuery({
    queryKey: ["audit-detail", site, entry.id],
    queryFn: () => getAuditEntry(site!, entry.id),
    enabled: !!site,
    staleTime: Infinity, // an entry never changes
  });
  if (detail.isLoading) return <Spin size="small" />;
  if (!detail.data) return null;
  return <ContentDiff before={detail.data.before} after={detail.data.after} contentType={entry.contentType} />;
}

/**
 * The history of one content item, newest first: who changed it, when, what kind of change it was
 * and which fields it touched, with the exact changes one click away and a Restore button that puts
 * the content back as it was after that change (the item keeps its current published status).
 */
export default function HistoryDrawer({
  open,
  onClose,
  itemId,
  onRestored,
}: {
  open: boolean;
  onClose: () => void;
  itemId: number | null;
  /** Called after a restore, so the screen can reload the item it is showing. */
  onRestored?: () => void;
}) {
  const { site } = useSite();
  const queryClient = useQueryClient();
  const [expanded, setExpanded] = useState<number | null>(null);

  const history = useQuery({
    queryKey: ["audit", site, "item", itemId],
    queryFn: () => listAudit(site!, { itemId: itemId!, size: HISTORY_LIMIT }),
    enabled: open && !!site && itemId != null,
  });

  const restore = useMutation({
    mutationFn: (entry: AuditEntry) => restoreAudit(site!, entry.id),
    onSuccess: () => {
      message.success("Restored");
      queryClient.invalidateQueries({ queryKey: ["audit", site] });
      onRestored?.();
      onClose();
    },
  });

  const entries = history.data?.items ?? [];

  return (
    <Drawer
      title={
        <div>
          <div className="cms-eyebrow mb-2">— History · ID {itemId}</div>
          <div style={{ fontSize: 22, fontWeight: 700, letterSpacing: "-.01em", color: "#002d68" }}>
            {entries[0]?.title || "Item history"}
          </div>
        </div>
      }
      open={open}
      onClose={onClose}
      width={560}
      destroyOnClose
    >
      {history.isLoading && (
        <div style={{ padding: 40, textAlign: "center" }}>
          <Spin />
        </div>
      )}
      {history.isSuccess && entries.length === 0 && (
        <Empty
          description="No recorded changes yet — history starts with the next edit."
          style={{ padding: "40px 0" }}
        />
      )}
      <ol className="m-0 p-0" style={{ listStyle: "none" }}>
        {entries.map((entry, index) => {
          const isOpen = expanded === entry.id;
          const shown = entry.changed.slice(0, SHOWN_FIELDS);
          return (
            <li
              key={entry.id}
              style={{ padding: "14px 0", borderTop: index ? "1px solid #eef1f4" : undefined }}
            >
              <div style={{ display: "flex", alignItems: "center", gap: 8, flexWrap: "wrap" }}>
                <Tag color={ACTION_COLORS[entry.action]} style={{ marginInlineEnd: 0 }}>
                  {ACTION_LABELS[entry.action]}
                </Tag>
                {index === 0 && (
                  <Tag style={{ marginInlineEnd: 0 }} bordered={false}>
                    Latest
                  </Tag>
                )}
                <span style={{ fontWeight: 600, color: "#002d68" }}>{entry.actorName ?? "Unknown"}</span>
                <span style={{ color: "#6a7c90", fontSize: 13 }} title={entry.occurredAt}>
                  {dayjs(entry.occurredAt).format("MMM D, YYYY · HH:mm")}
                </span>
              </div>
              {shown.length > 0 && (
                <div style={{ marginTop: 8, display: "flex", gap: 6, flexWrap: "wrap" }}>
                  {shown.map((field) => (
                    <Tag key={field} bordered={false} style={{ marginInlineEnd: 0 }}>
                      {fieldLabel(entry.contentType, field)}
                    </Tag>
                  ))}
                  {entry.changed.length > SHOWN_FIELDS && (
                    <Tag bordered={false} style={{ marginInlineEnd: 0 }}>
                      +{entry.changed.length - SHOWN_FIELDS} more
                    </Tag>
                  )}
                </div>
              )}
              <div style={{ marginTop: 10, display: "flex", gap: 8 }}>
                <Button size="small" onClick={() => setExpanded(isOpen ? null : entry.id)}>
                  {isOpen ? "Hide changes" : "View changes"}
                </Button>
                <Popconfirm
                  title="Restore this version?"
                  description="The content goes back to how it was after this change. Its published status stays as it is now."
                  okText="Restore"
                  onConfirm={() => restore.mutate(entry)}
                  disabled={index === 0}
                >
                  <Button
                    size="small"
                    disabled={index === 0}
                    loading={restore.isPending && restore.variables?.id === entry.id}
                  >
                    Restore this version
                  </Button>
                </Popconfirm>
              </div>
              {isOpen && (
                <div style={{ marginTop: 14 }}>
                  <EntryChanges entry={entry} />
                </div>
              )}
            </li>
          );
        })}
      </ol>
      {history.data && history.data.totalItems > entries.length && (
        <p style={{ color: "#6a7c90", fontSize: 13, textAlign: "center" }}>
          Showing the latest {entries.length} of {history.data.totalItems} changes.
        </p>
      )}
    </Drawer>
  );
}
