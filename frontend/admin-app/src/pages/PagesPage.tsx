import { useEffect, useMemo, useState } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import { keepPreviousData, useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { Button, DatePicker, Dropdown, Empty, Input, Modal, Popconfirm, Select, Table, message } from "antd";
import type { ColumnsType } from "antd/es/table";
import dayjs from "dayjs";
import { useSite } from "../lib/useSite";
import { SITES } from "../config/sites";
import { CopyIcon, MoreIcon, PlusIcon } from "../components/icons";
import { showBulkResult } from "../components/bulkResult";
import { confirmMovePagesToTrash } from "../components/pageTrash";
import { BULK_LIMIT } from "../lib/postList";
import {
  PAGE_SIZES,
  PAGE_SORT_LABELS,
  PAGE_STATUS_LABELS,
  PAGE_TRASH_DAYS,
  PAGE_TYPE,
  bulkPages,
  listPageTrash,
  listPages,
  pageFacets,
  pageUrl,
  quickEditPage,
  type PageBulkAction,
  type PageQuery,
  type PageRow,
  type PageSort,
  type PageStatus,
  type PageTrashRow,
  type PageType,
} from "../lib/pageList";

const STATUSES: PageStatus[] = ["DRAFT", "SCHEDULED", "PUBLISHED", "ARCHIVED"];
const SORTS = Object.keys(PAGE_SORT_LABELS) as PageSort[];
const TYPES = Object.keys(PAGE_TYPE) as PageType[];

type StatusAction = "PUBLISH" | "UNPUBLISH" | "ARCHIVE";
const ACTION_VERB: Record<StatusAction, string> = { PUBLISH: "Publish", UNPUBLISH: "Move to draft", ARCHIVE: "Archive" };
const ACTION_DONE: Record<PageBulkAction, string> = {
  PUBLISH: "Published",
  UNPUBLISH: "Moved to draft",
  ARCHIVE: "Archived",
  TRASH: "Moved to Trash:",
  RESTORE: "Restored",
  DELETE_FOREVER: "Deleted",
};

function ColHeader({ children }: { children: string }) {
  return <span className="cms-eyebrow">{children}</span>;
}

/** The list's state lives in the address (?q=&status=&type=&author=&from=&to=&sort=&size=&page=). */
function readQuery(params: URLSearchParams): PageQuery {
  const status = params.get("status") as PageStatus | null;
  const type = params.get("type") as PageType | null;
  const sort = params.get("sort") as PageSort | null;
  const size = params.get("size");
  return {
    q: params.get("q") ?? undefined,
    status: status && STATUSES.includes(status) ? status : undefined,
    type: type && TYPES.includes(type) ? type : undefined,
    author: params.get("author") ?? undefined,
    publishedFrom: params.get("from") ?? undefined,
    publishedTo: params.get("to") ?? undefined,
    sort: sort && SORTS.includes(sort) ? sort : undefined,
    page: Number(params.get("page")) || 1,
    size: size === null ? 20 : Number(size),
  };
}

function shortPath(path: string): string {
  return path.length > 26 ? `${path.slice(0, 25)}…` : path;
}

/**
 * The Pages list (BRD PAGE-01): the Service, Industry and About pages as one list. Search by title or
 * address, filters (status, type, author, published date), sort, 20 / 40 / 60 / all per page; bulk publish,
 * move to draft, archive and Trash; quick edit of the title, address and status; and a Trash tab. A page's
 * content is edited in its own editor (click the title).
 */
export default function PagesPage() {
  const { site } = useSite();
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const [params, setParams] = useSearchParams();
  const query = useMemo(() => readQuery(params), [params]);
  const inTrash = params.get("status") === "TRASH";
  const [search, setSearch] = useState(query.q ?? "");
  const [selection, setSelection] = useState<{ site?: string | null; ids: number[] }>({ ids: [] });
  const selected = selection.site === site ? selection.ids : [];
  const setSelected = (next: number[] | ((prev: number[]) => number[])) =>
    setSelection((prev) => {
      const current = prev.site === site ? prev.ids : [];
      return { site, ids: typeof next === "function" ? next(current) : next };
    });
  const [quick, setQuick] = useState<PageRow | null>(null);

  const update = (patch: Record<string, string | undefined>) => {
    setParams(
      (prev) => {
        const next = new URLSearchParams(prev);
        for (const [key, value] of Object.entries(patch)) {
          if (value === undefined || value === "") next.delete(key);
          else next.set(key, value);
        }
        if (!("page" in patch)) next.delete("page");
        return next;
      },
      { replace: true },
    );
  };

  useEffect(() => {
    const t = setTimeout(() => {
      if (search.trim() !== (query.q ?? "")) update({ q: search.trim() || undefined });
    }, 300);
    return () => clearTimeout(t);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [search]);

  const list = useQuery({
    queryKey: ["pages-list", site, query],
    queryFn: () => listPages(site!, query),
    enabled: !!site && !inTrash,
    placeholderData: keepPreviousData,
    refetchOnMount: "always",
  });
  const facets = useQuery({
    queryKey: ["pages-facets", site],
    queryFn: () => pageFacets(site!),
    enabled: !!site,
    refetchOnMount: "always",
  });

  const refresh = () => {
    queryClient.invalidateQueries({ queryKey: ["pages-list", site] });
    queryClient.invalidateQueries({ queryKey: ["pages-facets", site] });
    queryClient.invalidateQueries({ queryKey: ["pages-trash", site] });
    queryClient.invalidateQueries({ queryKey: ["content"] });
  };

  const bulk = useMutation({
    mutationFn: ({ ids, action }: { ids: number[]; action: PageBulkAction }) => bulkPages(site!, ids, action),
    onSuccess: (result, { action }) => {
      refresh();
      setSelected((prev) => prev.filter((id) => !result.updated.includes(id)));
      showBulkResult(result, ACTION_DONE[action], "page");
    },
    onError: () => message.error("The change could not be made. Nothing was changed."),
  });

  const trashPages = (pages: { id: number; title: string | null }[]) =>
    confirmMovePagesToTrash(site!, pages, (result) => {
      refresh();
      setSelected((prev) => prev.filter((id) => !result.updated.includes(id)));
    });

  const siteSub = SITES.find((s) => s.code === site)?.subdomain;
  const counts = facets.data?.statusCounts;
  const total = counts ? STATUSES.reduce((n, s) => n + counts[s], 0) : undefined;
  const filtersOn = Boolean(query.q || query.status || query.type || query.author || query.publishedFrom || query.publishedTo || query.sort);
  const rows = useMemo(() => list.data?.items ?? [], [list.data]);
  const size = query.size ?? 20;

  const copy = async (row: PageRow) => {
    if (!site) return;
    try {
      await navigator.clipboard.writeText(pageUrl(siteSub, site, row.path));
      message.success("URL copied");
    } catch {
      message.error("Could not copy the URL");
    }
  };
  const clearAll = () => {
    setSearch("");
    setParams({}, { replace: true });
  };

  const columns: ColumnsType<PageRow> = [
    {
      title: <ColHeader>Title</ColHeader>,
      dataIndex: "title",
      render: (title: string | null, row) => (
        <div style={{ minWidth: 0 }}>
          <a
            onClick={() => navigate(PAGE_TYPE[row.type].editor(row.id))}
            title={title ?? undefined}
            style={{ display: "block", overflow: "hidden", textOverflow: "ellipsis", whiteSpace: "nowrap", fontSize: 14, fontWeight: 600, color: "#0b1b3a", cursor: "pointer" }}
          >
            {title || "(no title)"}
          </a>
          <span style={{ fontSize: 12, color: "#7b8aa5" }}>{PAGE_TYPE[row.type].label} page</span>
        </div>
      ),
    },
    {
      title: <ColHeader>URL</ColHeader>,
      dataIndex: "path",
      width: 250,
      render: (path: string, row) => (
        <span style={{ display: "inline-flex", alignItems: "center", gap: 6 }}>
          <span title={site ? pageUrl(siteSub, site, path) : path} style={{ fontFamily: "var(--font-mono)", fontSize: 12, color: "#5b6b86", whiteSpace: "nowrap" }}>
            {shortPath(path)}
          </span>
          <Button type="text" size="small" aria-label="Copy URL" title="Copy URL" icon={<CopyIcon />} onClick={() => copy(row)} />
        </span>
      ),
    },
    {
      title: <ColHeader>Author</ColHeader>,
      dataIndex: "author",
      width: 130,
      ellipsis: true,
      render: (v: string | null) => <span style={{ fontSize: 14, color: "#5b6b86" }}>{v || "—"}</span>,
    },
    {
      title: <ColHeader>Published</ColHeader>,
      dataIndex: "publishedAt",
      width: 130,
      render: (v: string | null, row) =>
        v && (row.status === "PUBLISHED" || row.status === "ARCHIVED") ? (
          <span style={{ fontSize: 13, color: "#0b1b3a" }}>{dayjs(v).format("MMM D, YYYY")}</span>
        ) : (
          <span style={{ color: "#9aa8c0" }}>—</span>
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
      width: 110,
      render: (status: PageStatus, row) => (
        <div>
          <span className={`cms-status${status === "PUBLISHED" ? " is-published" : status === "SCHEDULED" ? " is-scheduled" : status === "ARCHIVED" ? " is-archived" : ""}`}>
            {PAGE_STATUS_LABELS[status]}
          </span>
          {status === "SCHEDULED" && row.publishAt && (
            <div style={{ fontSize: 12, color: "#7b8aa5", marginTop: 4 }}>{dayjs(row.publishAt).format("MMM D, HH:mm")}</div>
          )}
        </div>
      ),
    },
    {
      title: "",
      key: "actions",
      width: 48,
      render: (_: unknown, row) => {
        const one = (action: StatusAction) => bulk.mutate({ ids: [row.id], action });
        return (
          <Dropdown
            trigger={["click"]}
            menu={{
              items: [
                { key: "edit", label: "Edit", onClick: () => navigate(PAGE_TYPE[row.type].editor(row.id)) },
                { key: "quick", label: "Quick edit", onClick: () => setQuick(row) },
                { type: "divider" as const },
                ...(row.status !== "PUBLISHED" ? [{ key: "pub", label: "Publish", onClick: () => one("PUBLISH") }] : []),
                ...(row.status === "PUBLISHED" || row.status === "SCHEDULED" ? [{ key: "draft", label: "Move to draft", onClick: () => one("UNPUBLISH") }] : []),
                ...(row.status !== "ARCHIVED" ? [{ key: "arch", label: "Archive", onClick: () => one("ARCHIVE") }] : []),
                { type: "divider" as const },
                {
                  key: "del",
                  danger: true,
                  disabled: row.type === "about_page",
                  label: row.type === "about_page" ? "Move to Trash (not for the About page)" : "Move to Trash",
                  onClick: () => trashPages([{ id: row.id, title: row.title }]),
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
  const selectedRows = selected.map((id) => rows.find((r) => r.id === id)).filter((r): r is PageRow => !!r);
  const aboutSelected = selectedRows.some((r) => r.type === "about_page");
  const bulkButton = (action: StatusAction, primary = false) => (
    <Popconfirm
      key={action}
      title={`${ACTION_VERB[action]} ${selected.length} page${selected.length === 1 ? "" : "s"}?`}
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
          <h1 className="cms-page-title">Pages</h1>
          <p className="cms-page-sub">
            {total === undefined ? "…" : total.toLocaleString("en-US")} pages
            {counts ? (
              <>
                {" · "}
                <span style={{ color: "#0b7a55", fontWeight: 600 }}>{counts.PUBLISHED.toLocaleString("en-US")} published</span>
              </>
            ) : null}
            {siteSub ? ` on ${siteSub}` : ""}
          </p>
        </div>
        <Dropdown
          trigger={["click"]}
          menu={{
            items: TYPES.filter((t) => PAGE_TYPE[t].newPath).map((t) => ({
              key: t,
              label: `${PAGE_TYPE[t].label} page`,
              onClick: () => navigate(PAGE_TYPE[t].newPath!),
            })),
          }}
        >
          <Button className="cms-cta" icon={<PlusIcon />}>
            New page
          </Button>
        </Dropdown>
      </div>

      <div className="mb-4" style={{ overflowX: "auto", maxWidth: "100%" }}>
        <div className="cms-segmented" role="tablist" aria-label="Status" style={{ whiteSpace: "nowrap" }}>
          {[
            { label: "All", value: undefined as string | undefined, n: total },
            ...STATUSES.map((s) => ({ label: PAGE_STATUS_LABELS[s], value: s as string, n: counts?.[s] })),
            { label: "Trash", value: "TRASH", n: facets.data?.trashCount },
          ].map((o) => (
            <button
              key={o.label}
              type="button"
              role="tab"
              className={(inTrash ? "TRASH" : query.status) === o.value ? "is-on" : undefined}
              aria-selected={(inTrash ? "TRASH" : query.status) === o.value}
              onClick={() => update({ status: o.value })}
            >
              {o.label}
              {o.n !== undefined ? ` (${o.n.toLocaleString("en-US")})` : ""}
            </button>
          ))}
        </div>
      </div>

      {inTrash && site ? (
        <PagesTrash site={site} onChanged={refresh} />
      ) : (
        <>
          <div className="flex flex-wrap items-center gap-3 mb-4">
            <Input.Search
              allowClear
              placeholder="Search title or URL"
              style={{ width: 240 }}
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              onSearch={(v) => update({ q: v.trim() || undefined })}
            />
            <Select
              allowClear
              placeholder="Any type"
              style={{ width: 130 }}
              value={query.type}
              onChange={(v) => update({ type: v })}
              options={TYPES.map((t) => ({ value: t, label: PAGE_TYPE[t].label }))}
            />
            <Select
              allowClear
              showSearch
              optionFilterProp="label"
              placeholder="Any author"
              style={{ width: 170 }}
              value={query.author}
              onChange={(v) => update({ author: v })}
              options={(facets.data?.authors ?? []).map((a) => ({ value: a.sub, label: a.name }))}
            />
            <DatePicker.RangePicker
              allowClear
              placeholder={["Published from", "to"]}
              value={query.publishedFrom && query.publishedTo ? [dayjs(query.publishedFrom), dayjs(query.publishedTo)] : null}
              onChange={(range) => update({ from: range?.[0]?.format("YYYY-MM-DD"), to: range?.[1]?.format("YYYY-MM-DD") })}
            />
            <Select
              style={{ width: 210 }}
              value={query.sort ?? "updated_desc"}
              onChange={(v) => update({ sort: v === "updated_desc" ? undefined : v })}
              options={SORTS.map((s) => ({ value: s, label: PAGE_SORT_LABELS[s] }))}
            />
            <Select
              style={{ width: 130 }}
              value={size}
              onChange={(v) => update({ size: v === 20 ? undefined : String(v) })}
              options={PAGE_SIZES.map((n) => ({ value: n, label: n === 0 ? "All" : `${n} per page` }))}
            />
            {filtersOn && (
              <Button type="link" onClick={clearAll}>
                Clear filters
              </Button>
            )}
          </div>

          <div
            className="flex flex-wrap items-center gap-3 mb-4"
            style={{ padding: "10px 14px", borderRadius: 12, background: selected.length ? "#eaf2ff" : "#f4f7fc", border: `1px solid ${selected.length ? "#bcd9fb" : "#e3e9f4"}` }}
          >
            <b style={{ color: "#0b1b3a" }}>{selected.length} selected</b>
            {overLimit && <span style={{ color: "#b42318" }}>Select at most {BULK_LIMIT} at a time</span>}
            {selected.length === 0 ? (
              <>
                <Button disabled>Publish</Button>
                <Button disabled>Move to draft</Button>
                <Button disabled>Archive</Button>
                <Button disabled>Move to Trash</Button>
              </>
            ) : (
              <>
                {bulkButton("PUBLISH", true)}
                {bulkButton("UNPUBLISH")}
                {bulkButton("ARCHIVE")}
                <Button
                  danger
                  disabled={overLimit || aboutSelected}
                  loading={bulk.isPending}
                  onClick={() => trashPages(selectedRows.length ? selectedRows.map((r) => ({ id: r.id, title: r.title })) : selected.map((id) => ({ id, title: null })))}
                >
                  Move to Trash
                </Button>
                {aboutSelected && <span style={{ fontSize: 12, color: "#7b8aa5" }}>The About page cannot be moved to the Trash</span>}
                <Button type="link" onClick={() => setSelected([])}>
                  Clear selection
                </Button>
              </>
            )}
          </div>

          <Table<PageRow>
            className="cms-table"
            rowKey="id"
            columns={columns}
            dataSource={rows}
            loading={list.isFetching}
            tableLayout="fixed"
            scroll={{ x: 980 }}
            rowSelection={{ selectedRowKeys: selected, preserveSelectedRowKeys: true, onChange: (keys) => setSelected(keys as number[]) }}
            pagination={{
              current: list.data?.page ?? query.page ?? 1,
              pageSize: size === 0 ? Math.max(rows.length, 1) : size,
              total: list.data?.totalItems ?? 0,
              showSizeChanger: false,
              hideOnSinglePage: true,
              showTotal: (n, range) => `${range[0]}–${range[1]} of ${n.toLocaleString()}`,
              onChange: (p) => update({ page: p > 1 ? String(p) : "" }),
            }}
            locale={{
              emptyText: list.isError ? (
                <Empty description="The pages could not be loaded" />
              ) : filtersOn ? (
                <div className="py-14 text-center">
                  <div style={{ fontSize: 16, fontWeight: 700, color: "#0b1b3a" }}>No pages match</div>
                  <Button className="mt-3" onClick={clearAll}>
                    Clear filters
                  </Button>
                </div>
              ) : (
                <div className="py-14 text-center" style={{ color: "#5b6b86" }}>
                  No pages for this site.
                </div>
              ),
            }}
          />
        </>
      )}

      {quick && site && (
        <QuickEdit
          key={quick.id}
          site={site}
          row={quick}
          onClose={() => setQuick(null)}
          onSaved={() => {
            setQuick(null);
            refresh();
          }}
        />
      )}
    </div>
  );
}

/** Quick edit (PAGE-01): title, address and status on the list. Changing the address of a live page asks first. */
function QuickEdit({ site, row, onClose, onSaved }: { site: string; row: PageRow; onClose: () => void; onSaved: () => void }) {
  const [title, setTitle] = useState(row.title ?? "");
  const [slug, setSlug] = useState(row.slug ?? "");
  const [status, setStatus] = useState<PageStatus>(row.status);
  const isAbout = row.type === "about_page";
  const slugChanged = !isAbout && slug.trim() !== (row.slug ?? "");
  const live = row.status === "PUBLISHED";

  const save = useMutation({
    mutationFn: () =>
      quickEditPage(site, row.id, {
        title: title.trim() !== (row.title ?? "") ? title.trim() : undefined,
        slug: slugChanged ? slug.trim() : undefined,
        status: status !== row.status ? status : undefined,
        version: row.version,
      }),
    onSuccess: () => {
      message.success("Saved");
      onSaved();
    },
    onError: (e: unknown) => {
      const reason = (e as { response?: { data?: { status?: { message?: string } } } })?.response?.data?.status?.message;
      message.error(reason || "Could not save");
    },
  });

  const submit = () => {
    if (slugChanged && live) {
      Modal.confirm({
        title: "Change the address of a live page?",
        content: `This page is published at ${row.path}. Its old address keeps working and redirects to the new one, but links that people typed elsewhere should be updated.`,
        okText: "Change the address",
        onOk: () => save.mutateAsync(),
      });
      return;
    }
    save.mutate();
  };

  const label = { display: "block", margin: "14px 0 4px", fontSize: 12, fontWeight: 600, color: "#5b6b86" } as const;
  return (
    <Modal open title={`Quick edit: ${row.title ?? "page"}`} onCancel={onClose} okText="Save" onOk={submit} okButtonProps={{ loading: save.isPending, disabled: !title.trim() }} destroyOnHidden>
      <label style={{ ...label, marginTop: 4 }}>Title</label>
      <Input value={title} maxLength={200} onChange={(e) => setTitle(e.target.value)} />
      {!isAbout && (
        <>
          <label style={label}>Address (slug)</label>
          <Input value={slug} maxLength={100} onChange={(e) => setSlug(e.target.value)} />
          {slugChanged && live && <div style={{ fontSize: 12, color: "#b36b00", marginTop: 4 }}>This page is live: you will be asked to confirm.</div>}
        </>
      )}
      <label style={label}>Status</label>
      <Select
        style={{ width: "100%" }}
        value={status}
        onChange={setStatus}
        options={(["DRAFT", "PUBLISHED", "ARCHIVED"] as PageStatus[]).map((s) => ({ value: s, label: PAGE_STATUS_LABELS[s] }))}
        disabled={row.status === "SCHEDULED"}
      />
      {row.status === "SCHEDULED" && <div style={{ fontSize: 12, color: "#7b8aa5", marginTop: 4 }}>A scheduled page changes its status in the page editor.</div>}
    </Modal>
  );
}

/** The Trash for pages: restore (as drafts) or delete for good. */
function PagesTrash({ site, onChanged }: { site: string; onChanged: () => void }) {
  const queryClient = useQueryClient();
  const [page, setPage] = useState(1);
  const [selected, setSelected] = useState<number[]>([]);
  const trash = useQuery({
    queryKey: ["pages-trash", site, page],
    queryFn: () => listPageTrash(site, undefined, page),
    placeholderData: keepPreviousData,
    refetchOnMount: "always",
  });
  const act = useMutation({
    mutationFn: ({ ids, action }: { ids: number[]; action: PageBulkAction }) => bulkPages(site, ids, action),
    onSuccess: (result, { action }) => {
      queryClient.invalidateQueries({ queryKey: ["pages-trash", site] });
      onChanged();
      setSelected((prev) => prev.filter((id) => !result.updated.includes(id)));
      showBulkResult(result, ACTION_DONE[action], "page");
    },
    onError: () => message.error("The change could not be made. Nothing was changed."),
  });
  const none = selected.length === 0;
  const columns: ColumnsType<PageTrashRow> = [
    {
      title: <ColHeader>Title</ColHeader>,
      dataIndex: "title",
      render: (v: string | null, row) => (
        <div>
          <b style={{ color: "#0b1b3a" }}>{v || "(no title)"}</b>
          <div style={{ fontSize: 12, color: "#7b8aa5" }}>{PAGE_TYPE[row.type].label} page</div>
        </div>
      ),
    },
    {
      title: <ColHeader>URL</ColHeader>,
      dataIndex: "path",
      width: 240,
      render: (v: string) => <span style={{ fontFamily: "var(--font-mono)", fontSize: 12, color: "#5b6b86" }}>{v}</span>,
    },
    {
      title: <ColHeader>Was</ColHeader>,
      dataIndex: "previousStatus",
      width: 110,
      render: (v: PageStatus | null) => <span style={{ color: "#5b6b86" }}>{v ? PAGE_STATUS_LABELS[v] : "—"}</span>,
    },
    {
      title: <ColHeader>Moved to Trash</ColHeader>,
      dataIndex: "trashedAt",
      width: 210,
      render: (v: string, row) => {
        const left = Math.max(0, PAGE_TRASH_DAYS - dayjs().diff(dayjs(v), "day"));
        return (
          <div>
            <div style={{ fontSize: 13, color: "#0b1b3a" }}>{dayjs(v).format("MMM D, YYYY · HH:mm")}</div>
            <div style={{ fontSize: 12, color: "#7b8aa5" }}>
              {row.trashedBy ? `${row.trashedBy} · ` : ""}
              {left} {left === 1 ? "day" : "days"} left
            </div>
          </div>
        );
      },
    },
  ];
  return (
    <>
      <p className="mb-3" style={{ fontSize: 13, color: "#5b6b86" }}>
        Moved here for {PAGE_TRASH_DAYS} days, then deleted for good. Restoring brings a page back as a draft; its address must be free.
      </p>
      <div
        className="flex flex-wrap items-center gap-3 mb-4"
        style={{ padding: "10px 14px", borderRadius: 12, background: none ? "#f4f7fc" : "#eaf2ff", border: `1px solid ${none ? "#e3e9f4" : "#bcd9fb"}` }}
      >
        <b style={{ color: "#0b1b3a" }}>{selected.length} selected</b>
        <Button type="primary" disabled={none} loading={act.isPending} onClick={() => act.mutate({ ids: selected, action: "RESTORE" })}>
          Restore
        </Button>
        <Popconfirm
          title={`Delete ${selected.length} page${selected.length === 1 ? "" : "s"} for good?`}
          description="This cannot be undone."
          okText="Delete for good"
          okButtonProps={{ danger: true }}
          onConfirm={() => act.mutate({ ids: selected, action: "DELETE_FOREVER" })}
          disabled={none}
        >
          <Button danger disabled={none} loading={act.isPending}>
            Delete for good
          </Button>
        </Popconfirm>
      </div>
      <Table<PageTrashRow>
        className="cms-table"
        rowKey="id"
        columns={columns}
        dataSource={trash.data?.items ?? []}
        loading={trash.isFetching}
        rowSelection={{ selectedRowKeys: selected, preserveSelectedRowKeys: true, onChange: (keys) => setSelected(keys as number[]) }}
        pagination={{ current: trash.data?.page ?? page, pageSize: 20, total: trash.data?.totalItems ?? 0, showSizeChanger: false, hideOnSinglePage: true, onChange: setPage }}
        locale={{ emptyText: <div className="py-14 text-center" style={{ color: "#5b6b86" }}>The Trash is empty</div> }}
      />
    </>
  );
}
