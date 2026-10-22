import { useState } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { Button, DatePicker, Empty, Input, Modal, Popconfirm, Select, Spin, Table, Tag, message } from "antd";
import type { ColumnsType } from "antd/es/table";
import dayjs from "dayjs";
import { useNavigate } from "react-router-dom";
import ContentDiff from "../components/ContentDiff";
import { useSite } from "../lib/useSite";
import { editorPathFor, newItemPathFor, typeLabel } from "../lib/adminSearch";
import {
  ACTION_COLORS,
  ACTION_LABELS,
  AUDIT_TYPE_OPTIONS,
  fieldLabel,
  getAuditEntry,
  listAudit,
  prefillFrom,
  restoreAudit,
  type AuditAction,
  type AuditEntry,
} from "../lib/audit";

const { RangePicker } = DatePicker;
const PAGE_SIZE = 20;
const MAX_FIELDS = 3;

const ACTION_OPTIONS = (Object.keys(ACTION_LABELS) as AuditAction[]).map((value) => ({
  value,
  label: ACTION_LABELS[value],
}));

/**
 * The changes of one entry, with the actions that make sense for it: an item that still exists can
 * be put back to a version; a deleted one can only seed a new item (it is never brought back).
 */
function EntryModal({ entry, onClose }: { entry: AuditEntry | null; onClose: () => void }) {
  const { site } = useSite();
  const navigate = useNavigate();
  const queryClient = useQueryClient();

  const detail = useQuery({
    queryKey: ["audit-detail", site, entry?.id],
    queryFn: () => getAuditEntry(site!, entry!.id),
    enabled: !!site && !!entry,
    staleTime: Infinity, // an entry never changes
  });

  const restore = useMutation({
    mutationFn: () => restoreAudit(site!, entry!.id),
    onSuccess: (restored) => {
      message.success("Restored");
      queryClient.invalidateQueries({ queryKey: ["audit", site] });
      const path = editorPathFor(restored.contentType, restored.itemId);
      onClose();
      if (path) navigate(path);
    },
  });

  const deleted = entry?.action === "DELETE";
  const openPath = entry ? editorPathFor(entry.contentType, entry.itemId) : null;
  const newPath = entry ? newItemPathFor(entry.contentType) : null;
  const deletedState = deleted ? detail.data?.before : null;
  // A deleted item is not brought back: its last content opens in the new-item form, to review and save.
  const createFromDeleted = () => {
    if (!newPath || !deletedState) return;
    onClose();
    navigate(newPath, { state: { prefill: prefillFrom(deletedState) } });
  };

  return (
    <Modal
      open={!!entry}
      onCancel={onClose}
      width={820}
      destroyOnClose
      title={
        entry && (
          <div>
            <div className="cms-sheet-kicker">
              {ACTION_LABELS[entry.action]} · {dayjs(entry.occurredAt).format("MMM D, YYYY · HH:mm")} ·{" "}
              {entry.actorName ?? "Unknown"}
            </div>
            <div className="cms-sheet-title" style={{ whiteSpace: "normal" }}>
              {entry.title || "Untitled"}
            </div>
          </div>
        )
      }
      footer={
        entry && (
          <div style={{ display: "flex", gap: 8, justifyContent: "flex-end" }}>
            {!deleted && openPath && <Button onClick={() => navigate(openPath)}>Open item</Button>}
            {deleted ? (
              <Button type="primary" disabled={!newPath || !deletedState} onClick={createFromDeleted}>
                Create a new item from this version
              </Button>
            ) : (
              <Popconfirm
                title="Restore this version?"
                description="The content goes back to how it was after this change. Its published status stays as it is now."
                okText="Restore"
                onConfirm={() => restore.mutate()}
              >
                <Button type="primary" loading={restore.isPending}>
                  Restore this version
                </Button>
              </Popconfirm>
            )}
          </div>
        )
      }
    >
      {detail.isLoading && (
        <div style={{ padding: 32, textAlign: "center" }}>
          <Spin />
        </div>
      )}
      {detail.data && entry && (
        <ContentDiff before={detail.data.before} after={detail.data.after} contentType={entry.contentType} />
      )}
    </Modal>
  );
}

/**
 * The site's activity feed (/activity): every recorded change to content — who did what, to which
 * item, when — newest first, filterable by kind of change, content type, person and dates. Opening
 * a row shows exactly what changed and offers to restore it (including bringing a deleted item
 * back).
 */
export default function ActivityPage() {
  const { site } = useSite();
  const [page, setPage] = useState(1);
  const [action, setAction] = useState<AuditAction | undefined>();
  const [type, setType] = useState<string | undefined>();
  const [actor, setActor] = useState("");
  const [range, setRange] = useState<[string, string] | null>(null);
  const [selected, setSelected] = useState<AuditEntry | null>(null);

  const query = useQuery({
    queryKey: ["audit", site, "feed", action, type, actor, range, page],
    queryFn: () =>
      listAudit(site!, {
        action,
        type,
        actor: actor.trim() || undefined,
        from: range?.[0],
        to: range?.[1],
        page,
        size: PAGE_SIZE,
      }),
    enabled: !!site,
    placeholderData: (previous) => previous,
  });

  /** A filter changed: show the first page of the new result. */
  const filter = <T,>(set: (value: T) => void) => (value: T) => {
    set(value);
    setPage(1);
  };

  const columns: ColumnsType<AuditEntry> = [
    {
      title: "When",
      dataIndex: "occurredAt",
      width: 170,
      render: (at: string) => (
        <span title={at} style={{ whiteSpace: "nowrap" }}>
          {dayjs(at).format("MMM D, YYYY · HH:mm")}
        </span>
      ),
    },
    {
      title: "Change",
      dataIndex: "action",
      width: 120,
      render: (value: AuditAction) => <Tag color={ACTION_COLORS[value]}>{ACTION_LABELS[value]}</Tag>,
    },
    {
      title: "Item",
      dataIndex: "title",
      render: (title: string | null, entry) => (
        <div style={{ minWidth: 0 }}>
          <div style={{ fontWeight: 600, color: "#0b1b3a" }}>{title || "Untitled"}</div>
          <Tag bordered={false} style={{ marginTop: 4 }}>
            {typeLabel(entry.contentType)}
          </Tag>
        </div>
      ),
    },
    {
      title: "Fields",
      dataIndex: "changed",
      responsive: ["lg"],
      render: (changed: string[], entry) => (
        <div style={{ display: "flex", gap: 6, flexWrap: "wrap" }}>
          {changed.slice(0, MAX_FIELDS).map((field) => (
            <Tag key={field} bordered={false} style={{ marginInlineEnd: 0 }}>
              {fieldLabel(entry.contentType, field)}
            </Tag>
          ))}
          {changed.length > MAX_FIELDS && (
            <Tag bordered={false} style={{ marginInlineEnd: 0 }}>
              +{changed.length - MAX_FIELDS}
            </Tag>
          )}
        </div>
      ),
    },
    { title: "By", dataIndex: "actorName", width: 160, render: (name: string | null) => name ?? "Unknown" },
  ];

  return (
    <div>
      <div className="mb-6">
        <h1 className="cms-page-title">Activity</h1>
        <p className="cms-page-sub">
          Every change to this site’s content: who made it, when, and exactly what changed.
        </p>
      </div>

      <div className="flex flex-wrap items-center gap-3 mb-5">
        <Select
          allowClear
          placeholder="Any change"
          style={{ width: 150 }}
          options={ACTION_OPTIONS}
          value={action}
          onChange={filter(setAction)}
        />
        <Select
          allowClear
          showSearch
          optionFilterProp="label"
          placeholder="Any content type"
          style={{ width: 200 }}
          options={AUDIT_TYPE_OPTIONS}
          value={type}
          onChange={filter(setType)}
        />
        <Input.Search
          allowClear
          placeholder="Person"
          style={{ width: 180 }}
          onSearch={filter(setActor)}
        />
        <RangePicker
          allowClear
          onChange={(_, dates) => filter(setRange)(dates[0] && dates[1] ? [dates[0], dates[1]] : null)}
        />
      </div>

      <Table
        className="cms-table"
        rowKey="id"
        columns={columns}
        dataSource={query.data?.items ?? []}
        loading={query.isLoading || query.isFetching}
        onRow={(entry) => ({ onClick: () => setSelected(entry), style: { cursor: "pointer" } })}
        pagination={{
          current: query.data?.page ?? page,
          pageSize: PAGE_SIZE,
          total: query.data?.totalItems ?? 0,
          showSizeChanger: false,
          hideOnSinglePage: true,
          onChange: setPage,
        }}
        locale={{
          emptyText: (
            <Empty
              description={
                action || type || actor || range
                  ? "No changes match these filters."
                  : "No recorded changes yet — activity appears as content is edited."
              }
              style={{ padding: "24px 0" }}
            />
          ),
        }}
      />

      <EntryModal entry={selected} onClose={() => setSelected(null)} />
    </div>
  );
}
