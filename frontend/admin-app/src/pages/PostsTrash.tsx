import { useEffect, useState } from "react";
import { keepPreviousData, useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { Button, Dropdown, Empty, Input, Modal, Popconfirm, Table, message } from "antd";
import type { ColumnsType } from "antd/es/table";
import dayjs from "dayjs";
import { MoreIcon } from "../components/icons";
import { showBulkResult } from "../components/bulkResult";
import {
  BULK_LIMIT,
  PAGE_SIZE,
  STATUS_LABELS,
  TRASH_DAYS,
  bulkPosts,
  listTrash,
  restoreCheck,
  type BulkAction,
  type RestoreCheck,
  type TrashRow,
} from "../lib/postList";

function ColHeader({ children }: { children: string }) {
  return <span className="cms-eyebrow">{children}</span>;
}

/** "12 days", "1 day", "Today" for the last day; red from three days out. */
function DaysLeft({ days }: { days: number }) {
  const urgent = days <= 3;
  return (
    <span style={{ fontSize: 13, fontWeight: 600, color: urgent ? "#b42318" : "#0b1b3a" }}>
      {days <= 0 ? "Any moment" : days === 1 ? "1 day" : `${days} days`}
    </span>
  );
}

/**
 * The Trash for posts (BRD POST-01): what was moved here stays {TRASH_DAYS} days, then the database
 * deletes it for good. Restore brings a post back as a draft; if its address was taken meanwhile the
 * person is asked for another one first. Delete permanently removes it now.
 */
export default function PostsTrash({ site, onChanged }: { site: string; onChanged: () => void }) {
  const queryClient = useQueryClient();
  const [search, setSearch] = useState("");
  const [q, setQ] = useState("");
  const [page, setPage] = useState(1);
  const [selected, setSelected] = useState<number[]>([]);
  /** Posts whose address is taken, waiting for the person to choose another one. */
  const [conflicts, setConflicts] = useState<{ ids: number[]; checks: RestoreCheck[] } | null>(null);
  const [edited, setEdited] = useState<Record<number, string>>({});

  useEffect(() => {
    const t = setTimeout(() => {
      setQ(search.trim());
      setPage(1);
    }, 300);
    return () => clearTimeout(t);
  }, [search]);

  const list = useQuery({
    queryKey: ["posts-trash", site, q, page],
    queryFn: () => listTrash(site, q || undefined, page),
    placeholderData: keepPreviousData,
    refetchOnMount: "always",
  });

  const refresh = () => {
    queryClient.invalidateQueries({ queryKey: ["posts-trash", site] });
    onChanged();
  };

  const act = useMutation({
    mutationFn: ({ ids, action, slugs }: { ids: number[]; action: BulkAction; slugs?: Record<number, string> }) =>
      bulkPosts(site, ids, action, slugs),
    onSuccess: (result, { action }) => {
      refresh();
      setSelected((prev) => prev.filter((id) => !result.updated.includes(id)));
      showBulkResult(result, action === "RESTORE" ? "Restored" : "Deleted");
    },
    onError: () => message.error("The change could not be made. Nothing was changed."),
  });

  /** Restores; first checks the addresses, and asks about the ones that are taken. */
  const restore = async (ids: number[]) => {
    try {
      const checks = await restoreCheck(site, ids);
      const taken = checks.filter((c) => c.holder);
      if (taken.length === 0) {
        act.mutate({ ids, action: "RESTORE" });
        return;
      }
      setEdited(Object.fromEntries(taken.map((c) => [c.id, c.suggestion ?? ""])));
      setConflicts({ ids, checks: taken });
    } catch {
      message.error("Could not check the addresses. Nothing was changed.");
    }
  };

  const confirmRestoreWithNewAddresses = () => {
    if (!conflicts) return;
    act.mutate({ ids: conflicts.ids, action: "RESTORE", slugs: edited });
    setConflicts(null);
  };

  const rows = list.data?.items ?? [];
  const overLimit = selected.length > BULK_LIMIT;
  const emptyAddress = conflicts?.checks.some((c) => !(edited[c.id] ?? "").trim()) ?? false;

  const deleteForever = (ids: number[]) =>
    Modal.confirm({
      title: `Delete ${ids.length} post${ids.length === 1 ? "" : "s"} permanently?`,
      content: "This cannot be undone.",
      okText: "Delete permanently",
      okButtonProps: { danger: true },
      onOk: () => act.mutateAsync({ ids, action: "DELETE_FOREVER" }),
    });

  const columns: ColumnsType<TrashRow> = [
    {
      title: <ColHeader>Title</ColHeader>,
      dataIndex: "title",
      render: (title: string | null) => (
        <span
          title={title ?? undefined}
          style={{
            display: "block",
            overflow: "hidden",
            textOverflow: "ellipsis",
            whiteSpace: "nowrap",
            fontSize: 14,
            fontWeight: 600,
            color: "#0b1b3a",
          }}
        >
          {title || "(no title)"}
        </span>
      ),
    },
    {
      title: <ColHeader>Author</ColHeader>,
      dataIndex: "authorName",
      width: 110,
      ellipsis: true,
      render: (v: string | null) => <span style={{ fontSize: 14, color: "#5b6b86" }}>{v || "—"}</span>,
    },
    {
      title: <ColHeader>Slug</ColHeader>,
      dataIndex: "slug",
      width: 170,
      ellipsis: true,
      render: (v: string | null) => (
        <span style={{ fontFamily: "var(--font-mono)", fontSize: 12, color: "#5b6b86" }} title={v ?? undefined}>
          {v || "—"}
        </span>
      ),
    },
    {
      title: <ColHeader>Was</ColHeader>,
      dataIndex: "previousStatus",
      width: 96,
      render: (s: TrashRow["previousStatus"]) => (
        <span style={{ fontSize: 13, color: "#5b6b86" }}>{s ? STATUS_LABELS[s] : "—"}</span>
      ),
    },
    {
      title: <ColHeader>Moved to Trash</ColHeader>,
      dataIndex: "trashedAt",
      width: 170,
      render: (v: string, row) => (
        <div>
          <div style={{ fontSize: 13, color: "#0b1b3a" }}>{dayjs(v).format("MMM D, YYYY · HH:mm")}</div>
          {row.trashedBy && <div style={{ fontSize: 12, color: "#7b8aa5" }}>{row.trashedBy}</div>}
        </div>
      ),
    },
    {
      title: <ColHeader>Deleted in</ColHeader>,
      dataIndex: "daysLeft",
      width: 110,
      render: (d: number) => <DaysLeft days={d} />,
    },
    {
      title: "",
      key: "actions",
      width: 48,
      render: (_: unknown, row) => (
        <Dropdown
          trigger={["click"]}
          menu={{
            items: [
              { key: "restore", label: "Restore", onClick: () => restore([row.id]) },
              { type: "divider" as const },
              { key: "del", danger: true, label: "Delete permanently", onClick: () => deleteForever([row.id]) },
            ],
          }}
        >
          <Button type="text" size="small" aria-label="More actions" icon={<MoreIcon />} />
        </Dropdown>
      ),
    },
  ];

  return (
    <div>
      <p style={{ fontSize: 14, color: "#5b6b86", margin: "0 0 16px" }}>
        Posts stay here for {TRASH_DAYS} days, then are deleted for good. Restoring brings a post back as a draft.
      </p>
      <div className="flex flex-wrap items-center gap-3 mb-4">
        <Input.Search
          allowClear
          placeholder="Search title or slug"
          style={{ width: 260 }}
          value={search}
          onChange={(e) => setSearch(e.target.value)}
        />
      </div>

      {selected.length > 0 && (
        <div
          className="flex flex-wrap items-center gap-3 mb-4"
          style={{ padding: "10px 14px", borderRadius: 12, background: "#eaf2ff", border: "1px solid #bcd9fb" }}
        >
          <b style={{ color: "#0b1b3a" }}>{selected.length} selected</b>
          {overLimit && <span style={{ color: "#b42318" }}>Select at most {BULK_LIMIT} at a time</span>}
          <Button type="primary" loading={act.isPending} disabled={overLimit} onClick={() => restore(selected)}>
            Restore
          </Button>
          <Popconfirm
            title={`Delete ${selected.length} post${selected.length === 1 ? "" : "s"} permanently?`}
            description="This cannot be undone."
            okText="Delete permanently"
            okButtonProps={{ danger: true }}
            disabled={overLimit}
            onConfirm={() => act.mutate({ ids: selected, action: "DELETE_FOREVER" })}
          >
            <Button danger disabled={overLimit}>
              Delete permanently
            </Button>
          </Popconfirm>
          <Button type="link" onClick={() => setSelected([])}>
            Clear selection
          </Button>
        </div>
      )}

      <Table<TrashRow>
        className="cms-table"
        rowKey="id"
        columns={columns}
        dataSource={rows}
        loading={list.isFetching}
        tableLayout="fixed"
        scroll={{ x: 900 }}
        rowSelection={{
          selectedRowKeys: selected,
          preserveSelectedRowKeys: true,
          onChange: (keys) => setSelected(keys as number[]),
        }}
        pagination={{
          current: list.data?.page ?? page,
          pageSize: PAGE_SIZE,
          total: list.data?.totalItems ?? 0,
          showSizeChanger: false,
          hideOnSinglePage: true,
          showTotal: (n, range) => `${range[0]}–${range[1]} of ${n.toLocaleString()}`,
          onChange: (p) => setPage(p),
        }}
        locale={{
          emptyText: list.isError ? (
            <Empty description="The Trash could not be loaded" />
          ) : (
            <div className="py-14 text-center">
              <div style={{ fontSize: 16, fontWeight: 700, color: "#0b1b3a" }}>
                {q ? "No trashed post matches" : "The Trash is empty"}
              </div>
              <p className="mt-2 mb-0" style={{ fontSize: 14, color: "#5b6b86" }}>
                {q ? "Nothing in the Trash fits that search." : "Posts you move to the Trash wait here before they are deleted."}
              </p>
            </div>
          ),
        }}
      />

      <Modal
        open={!!conflicts}
        title="Some addresses are taken"
        okText="Restore"
        okButtonProps={{ disabled: emptyAddress }}
        onOk={confirmRestoreWithNewAddresses}
        onCancel={() => setConflicts(null)}
      >
        <p style={{ marginTop: 0, marginBottom: 14 }}>
          Another post took these addresses while they were in the Trash. Choose another address to restore them.
        </p>
        {conflicts?.checks.map((c) => (
          <div key={c.id} style={{ marginBottom: 14 }}>
            <div style={{ fontWeight: 600, color: "#0b1b3a" }}>{c.title || `Post ${c.id}`}</div>
            <div style={{ fontSize: 13, color: "#5b6b86", margin: "2px 0 6px" }}>
              <code>/insights/{c.slug}</code> is now used by <b>{c.holder?.title || `post ${c.holder?.id}`}</b>.
            </div>
            <Input
              addonBefore="/insights/"
              value={edited[c.id] ?? ""}
              onChange={(e) => setEdited((prev) => ({ ...prev, [c.id]: e.target.value }))}
              aria-label={`New address for ${c.title ?? `post ${c.id}`}`}
            />
          </div>
        ))}
      </Modal>
    </div>
  );
}
