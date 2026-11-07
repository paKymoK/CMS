import { useEffect, useMemo, useState } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import { keepPreviousData, useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { Button, DatePicker, Dropdown, Empty, Input, Modal, Popconfirm, Select, Table, Tag, message } from "antd";
import type { ColumnsType } from "antd/es/table";
import dayjs from "dayjs";
import { contentApi } from "../lib/api";
import { useSite } from "../lib/useSite";
import { SITES } from "../config/sites";
import { CopyIcon, MoreIcon, PlusIcon } from "../components/icons";
import {
  BULK_LIMIT,
  PAGE_SIZE,
  SORT_LABELS,
  STATUS_LABELS,
  bulkPosts,
  listPosts,
  postFacets,
  postUrl,
  type BulkAction,
  type PostQuery,
  type PostRow,
  type PostSort,
  type PostStatus,
} from "../lib/postList";

const STATUSES: PostStatus[] = ["DRAFT", "PUBLISHED", "SCHEDULED", "ARCHIVED"];
const SORTS = Object.keys(SORT_LABELS) as PostSort[];

const ACTION_VERB: Record<BulkAction, string> = {
  PUBLISH: "Publish",
  UNPUBLISH: "Move to draft",
  ARCHIVE: "Archive",
};
const ACTION_DONE: Record<BulkAction, string> = {
  PUBLISH: "Published",
  UNPUBLISH: "Moved to draft",
  ARCHIVE: "Archived",
};

function ColHeader({ children }: { children: string }) {
  return <span className="cms-eyebrow">{children}</span>;
}

/** The list's state lives in the address (?q=&status=&category=&month=&sort=&page=), so Back,
 * a refresh and a shared link all keep it. */
function readQuery(params: URLSearchParams): PostQuery {
  const month = params.get("month"); // YYYY-MM
  const [y, m] = month ? month.split("-").map(Number) : [];
  const status = params.get("status") as PostStatus | null;
  const sort = params.get("sort") as PostSort | null;
  return {
    q: params.get("q") ?? undefined,
    status: status && STATUSES.includes(status) ? status : undefined,
    category: params.get("category") ?? undefined,
    year: y || undefined,
    month: y && m ? m : undefined,
    sort: sort && SORTS.includes(sort) ? sort : undefined,
    page: Number(params.get("page")) || 1,
  };
}

function shortSlug(slug: string): string {
  return slug.length > 18 ? `${slug.slice(0, 17)}…` : slug;
}

async function copyText(text: string): Promise<boolean> {
  try {
    await navigator.clipboard.writeText(text);
    return true;
  } catch {
    return false;
  }
}

/**
 * The posts list (BRD POST-01): search by title or slug, filters (status, category, month), sort,
 * 20 per page, and bulk publish / move to draft / archive. Searching, filtering and sorting happen
 * on the server, so it stays fast however many posts a site has.
 *
 * Not here yet: the Trash and the bulk "assign category / tag" (they need the Trash and the
 * Category and Tag tables). Delete stays what it was: one post at a time, never a published one.
 */
export default function PostsPage() {
  const { site } = useSite();
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const [params, setParams] = useSearchParams();
  const query = useMemo(() => readQuery(params), [params]);
  const [search, setSearch] = useState(query.q ?? "");
  // The selection belongs to one site's list: on another site it starts empty again.
  const [selection, setSelection] = useState<{ site?: string | null; ids: number[] }>({ ids: [] });
  const selected = selection.site === site ? selection.ids : [];
  const setSelected = (next: number[] | ((prev: number[]) => number[])) =>
    setSelection((prev) => {
      const current = prev.site === site ? prev.ids : [];
      return { site, ids: typeof next === "function" ? next(current) : next };
    });

  const update = (patch: Record<string, string | undefined>) => {
    setParams(
      (prev) => {
        const next = new URLSearchParams(prev);
        for (const [key, value] of Object.entries(patch)) {
          if (value === undefined || value === "") next.delete(key);
          else next.set(key, value);
        }
        if (!("page" in patch)) next.delete("page"); // a new filter starts on the first page
        return next;
      },
      { replace: true },
    );
  };

  // Typing searches after a short pause rather than on every key.
  useEffect(() => {
    const t = setTimeout(() => {
      if (search.trim() !== (query.q ?? "")) update({ q: search.trim() || undefined });
    }, 300);
    return () => clearTimeout(t);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [search]);

  const list = useQuery({
    queryKey: ["posts-list", site, query],
    queryFn: () => listPosts(site!, query),
    enabled: !!site,
    placeholderData: keepPreviousData,
    refetchOnMount: "always",
  });
  const facets = useQuery({
    queryKey: ["posts-facets", site],
    queryFn: () => postFacets(site!),
    enabled: !!site,
    refetchOnMount: "always",
  });

  const refresh = () => {
    queryClient.invalidateQueries({ queryKey: ["posts-list", site] });
    queryClient.invalidateQueries({ queryKey: ["posts-facets", site] });
  };

  const bulk = useMutation({
    mutationFn: ({ ids, action }: { ids: number[]; action: BulkAction }) => bulkPosts(site!, ids, action),
    onSuccess: (result, { action }) => {
      refresh();
      setSelected((prev) => prev.filter((id) => !result.updated.includes(id)));
      if (result.failed.length === 0) {
        message.success(`${ACTION_DONE[action]} ${result.updated.length} post${result.updated.length === 1 ? "" : "s"}`);
        return;
      }
      Modal.warning({
        title: `${result.updated.length} changed, ${result.failed.length} could not be`,
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
    },
    onError: () => message.error("The change could not be made. Nothing was changed."),
  });

  const remove = useMutation({
    mutationFn: (id: number) => contentApi.delete(`/v1/admin/posts/${id}`, { params: { site } }),
    onSuccess: () => {
      refresh();
      message.success("Post deleted");
    },
    onError: () => message.error("The post could not be deleted"),
  });

  const siteSub = SITES.find((s) => s.code === site)?.subdomain;
  const counts = facets.data?.statusCounts;
  const total = counts ? STATUSES.reduce((n, s) => n + counts[s], 0) : undefined;
  const filtersOn = Boolean(query.q || query.status || query.category || query.year || query.sort);
  const monthValue = query.year ? dayjs(`${query.year}-${String(query.month ?? 1).padStart(2, "0")}-01`) : null;
  const rows = list.data?.items ?? [];

  const copy = async (row: PostRow) => {
    if (!row.slug || !site) return;
    const ok = await copyText(postUrl(siteSub, site, row.slug));
    if (ok) message.success("URL copied");
    else message.error("Could not copy the URL");
  };

  const clearAll = () => {
    setSearch("");
    setParams({}, { replace: true });
  };

  const columns: ColumnsType<PostRow> = [
    {
      title: <ColHeader>Title</ColHeader>,
      dataIndex: "title",
      render: (title: string | null, row) => (
        <a
          onClick={() => navigate(`/posts/${row.id}`)}
          title={title ?? undefined}
          style={{
            display: "block",
            overflow: "hidden",
            textOverflow: "ellipsis",
            whiteSpace: "nowrap",
            fontSize: 14,
            fontWeight: 600,
            color: "#0b1b3a",
            cursor: "pointer",
          }}
        >
          {title || "(no title)"}
        </a>
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
      title: <ColHeader>URL / Slug</ColHeader>,
      dataIndex: "slug",
      width: 170,
      render: (slug: string | null, row) =>
        slug ? (
          <span style={{ display: "inline-flex", alignItems: "center", gap: 6 }}>
            <span
              title={site ? postUrl(siteSub, site, slug) : slug}
              style={{ fontFamily: "var(--font-mono)", fontSize: 12, color: "#5b6b86", whiteSpace: "nowrap" }}
            >
              {shortSlug(slug)}
            </span>
            <Button
              type="text"
              size="small"
              aria-label="Copy URL"
              title="Copy URL"
              icon={<CopyIcon />}
              onClick={() => copy(row)}
            />
          </span>
        ) : (
          "—"
        ),
    },
    {
      title: <ColHeader>Category</ColHeader>,
      dataIndex: "category",
      width: 96,
      ellipsis: true,
      render: (v: string | null) => <span style={{ fontSize: 14, color: "#5b6b86" }}>{v || "—"}</span>,
    },
    {
      title: <ColHeader>Tags</ColHeader>,
      dataIndex: "tags",
      width: 120,
      render: (tags: string[]) =>
        tags.length === 0 ? (
          "—"
        ) : (
          <span title={tags.join(", ")}>
            {tags.slice(0, 1).map((t) => (
              <Tag key={t} style={{ marginInlineEnd: 4 }}>
                {t}
              </Tag>
            ))}
            {tags.length > 1 && <span style={{ fontSize: 12, color: "#7b8aa5" }}>+{tags.length - 1}</span>}
          </span>
        ),
    },
    {
      title: <ColHeader>Updated</ColHeader>,
      dataIndex: "updatedAt",
      width: 150,
      render: (v: string | null, row) =>
        v ? (
          <div>
            <div style={{ fontSize: 13, color: "#0b1b3a" }}>{dayjs(v).format("MMM D, YYYY · HH:mm")}</div>
            {row.updatedBy && <div style={{ fontSize: 12, color: "#7b8aa5" }}>{row.updatedBy}</div>}
          </div>
        ) : (
          "—"
        ),
    },
    {
      title: <ColHeader>Status</ColHeader>,
      dataIndex: "status",
      width: 104,
      render: (status: PostStatus, row) => (
        <div>
          <span
            className={`cms-status${status === "PUBLISHED" ? " is-published" : status === "SCHEDULED" ? " is-scheduled" : status === "ARCHIVED" ? " is-archived" : ""}`}
          >
            {STATUS_LABELS[status]}
          </span>
          {status === "SCHEDULED" && row.publishAt && (
            <div style={{ fontSize: 12, color: "#7b8aa5", marginTop: 4 }}>
              {dayjs(row.publishAt).format("MMM D, HH:mm")}
            </div>
          )}
        </div>
      ),
    },
    {
      title: "",
      key: "actions",
      width: 48,
      render: (_: unknown, row) => {
        const one = (action: BulkAction) => bulk.mutate({ ids: [row.id], action });
        return (
          <Dropdown
            trigger={["click"]}
            menu={{
              items: [
                { key: "edit", label: "Edit", onClick: () => navigate(`/posts/${row.id}`) },
                ...(row.status !== "PUBLISHED"
                  ? [{ key: "pub", label: "Publish", onClick: () => one("PUBLISH") }]
                  : []),
                ...(row.status === "PUBLISHED" || row.status === "SCHEDULED"
                  ? [{ key: "draft", label: "Move to draft", onClick: () => one("UNPUBLISH") }]
                  : []),
                ...(row.status !== "ARCHIVED"
                  ? [{ key: "arch", label: "Archive", onClick: () => one("ARCHIVE") }]
                  : []),
                { type: "divider" as const },
                {
                  key: "del",
                  danger: true,
                  disabled: row.status === "PUBLISHED",
                  label: row.status === "PUBLISHED" ? "Delete (archive it first)" : "Delete",
                  onClick: () =>
                    Modal.confirm({
                      title: "Delete this post?",
                      content: "This cannot be undone.",
                      okText: "Delete",
                      okButtonProps: { danger: true },
                      onOk: () => remove.mutateAsync(row.id),
                    }),
                },
              ],
            }}
          >
            <Button type="text" size="small" aria-label="More actions" icon={<MoreIcon />} />
          </Dropdown>
        );
      },
    },
  ];

  const overLimit = selected.length > BULK_LIMIT;
  const bulkButton = (action: BulkAction, primary = false) => (
    <Popconfirm
      key={action}
      title={`${ACTION_VERB[action]} ${selected.length} post${selected.length === 1 ? "" : "s"}?`}
      okText={ACTION_VERB[action]}
      onConfirm={() => bulk.mutate({ ids: selected, action })}
      disabled={overLimit}
    >
      <Button type={primary ? "primary" : "default"} loading={bulk.isPending} disabled={overLimit}>
        {ACTION_VERB[action]}
      </Button>
    </Popconfirm>
  );

  return (
    <div>
      <div className="flex flex-wrap items-end justify-between gap-5 mb-6">
        <div className="min-w-0">
          <h1 className="cms-page-title">Posts</h1>
          <p className="cms-page-sub">
            {total === undefined ? "…" : total.toLocaleString("en-US")} posts
            {counts ? (
              <>
                {" · "}
                <span style={{ color: "#0b7a55", fontWeight: 600 }}>{counts.PUBLISHED.toLocaleString("en-US")} published</span>
              </>
            ) : null}
            {siteSub ? ` on ${siteSub}` : ""}
          </p>
        </div>
        <Button className="cms-cta" icon={<PlusIcon />} onClick={() => navigate("/posts/new")}>
          New post
        </Button>
      </div>

      {/* Five tabs are wider than a phone: the strip scrolls by itself instead of the page. */}
      <div className="mb-4" style={{ overflowX: "auto", maxWidth: "100%" }}>
        <div className="cms-segmented" role="tablist" aria-label="Status" style={{ whiteSpace: "nowrap" }}>
          {[{ label: "All", value: undefined as PostStatus | undefined, n: total }, ...STATUSES.map((s) => ({ label: STATUS_LABELS[s], value: s, n: counts?.[s] }))].map(
            (o) => (
              <button
                key={o.label}
                type="button"
                role="tab"
                className={query.status === o.value ? "is-on" : undefined}
                aria-selected={query.status === o.value}
                onClick={() => update({ status: o.value })}
              >
                {o.label}
                {o.n !== undefined ? ` (${o.n.toLocaleString("en-US")})` : ""}
              </button>
            ),
          )}
        </div>
      </div>

      <div className="flex flex-wrap items-center gap-3 mb-4">
        <Input.Search
          allowClear
          placeholder="Search title or slug"
          style={{ width: 260 }}
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          onSearch={(v) => update({ q: v.trim() || undefined })}
        />
        <Select
          allowClear
          placeholder="Any category"
          style={{ width: 190 }}
          value={query.category}
          onChange={(v) => update({ category: v })}
          options={(facets.data?.categories ?? []).map((c) => ({ value: c.name, label: `${c.name} (${c.count})` }))}
        />
        <DatePicker
          picker="month"
          allowClear
          placeholder="Updated month"
          value={monthValue}
          onChange={(d) => update({ month: d ? d.format("YYYY-MM") : undefined })}
        />
        <Select
          style={{ width: 210 }}
          value={query.sort ?? "updated_desc"}
          onChange={(v) => update({ sort: v === "updated_desc" ? undefined : v })}
          options={SORTS.map((s) => ({ value: s, label: SORT_LABELS[s] }))}
        />
        {filtersOn && (
          <Button type="link" onClick={clearAll}>
            Clear filters
          </Button>
        )}
      </div>

      {selected.length > 0 && (
        <div
          className="flex flex-wrap items-center gap-3 mb-4"
          style={{ padding: "10px 14px", borderRadius: 12, background: "#eaf2ff", border: "1px solid #bcd9fb" }}
        >
          <b style={{ color: "#0b1b3a" }}>{selected.length} selected</b>
          {overLimit && <span style={{ color: "#b42318" }}>Select at most {BULK_LIMIT} at a time</span>}
          {bulkButton("PUBLISH", true)}
          {bulkButton("UNPUBLISH")}
          {bulkButton("ARCHIVE")}
          <Button type="link" onClick={() => setSelected([])}>
            Clear selection
          </Button>
        </div>
      )}

      <Table<PostRow>
        className="cms-table"
        rowKey="id"
        columns={columns}
        dataSource={rows}
        loading={list.isFetching}
        tableLayout="fixed"
        scroll={{ x: 980 }}
        rowSelection={{
          selectedRowKeys: selected,
          preserveSelectedRowKeys: true,
          onChange: (keys) => setSelected(keys as number[]),
        }}
        pagination={{
          current: list.data?.page ?? query.page ?? 1,
          pageSize: PAGE_SIZE,
          total: list.data?.totalItems ?? 0,
          showSizeChanger: false,
          hideOnSinglePage: true,
          showTotal: (n, range) => `${range[0]}–${range[1]} of ${n.toLocaleString()}`,
          onChange: (p) => update({ page: p > 1 ? String(p) : "" }),
        }}
        locale={{
          emptyText: list.isError ? (
            <Empty description="The posts could not be loaded" />
          ) : filtersOn ? (
            <div className="py-14 text-center">
              <div style={{ fontSize: 16, fontWeight: 700, color: "#0b1b3a" }}>No posts match</div>
              <p className="mt-2 mb-3" style={{ fontSize: 14, color: "#5b6b86" }}>
                Nothing fits those filters.
              </p>
              <Button onClick={clearAll}>Clear filters</Button>
            </div>
          ) : (
            <div className="py-14 text-center">
              <div style={{ fontSize: 16, fontWeight: 700, color: "#0b1b3a" }}>Nothing here yet</div>
              <p className="mt-2 mb-0" style={{ fontSize: 14, color: "#5b6b86" }}>
                No posts for this site. Create the first one.
              </p>
            </div>
          ),
        }}
      />
    </div>
  );
}
