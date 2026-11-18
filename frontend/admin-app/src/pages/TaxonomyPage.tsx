import { useEffect, useMemo, useState } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import { keepPreviousData, useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { Button, Drawer, Dropdown, Empty, Input, Modal, Popconfirm, Select, Switch, Table, message } from "antd";
import type { ColumnsType } from "antd/es/table";
import dayjs from "dayjs";
import { useSite } from "../lib/useSite";
import { MoreIcon, PlusIcon } from "../components/icons";
import { showBulkResult } from "../components/bulkResult";
import {
  KIND_TEXT,
  TAXONOMY_PAGE_SIZES,
  TRASH_DAYS,
  allTaxonomy,
  bulkTaxonomy,
  createTaxonomy,
  listTaxonomy,
  listTaxonomyTrash,
  slugify,
  updateTaxonomy,
  type TaxonomyBulkAction,
  type TaxonomyKind,
  type TaxonomyQuery,
  type TaxonomyRow,
  type TaxonomySort,
  type TaxonomyStatus,
  type TaxonomyTrashRow,
} from "../lib/taxonomy";

const SORT_LABELS: Record<TaxonomySort, string> = {
  name_asc: "Name: A to Z",
  name_desc: "Name: Z to A",
  updated_desc: "Updated: newest first",
  updated_asc: "Updated: oldest first",
  posts_desc: "Posts: most first",
  posts_asc: "Posts: fewest first",
};
const SORTS = Object.keys(SORT_LABELS) as TaxonomySort[];

const DONE: Record<TaxonomyBulkAction, string> = {
  ACTIVATE: "Activated",
  DEACTIVATE: "Deactivated",
  DELETE: "Moved to Trash:",
  REMOVE_FROM_POSTS: "Removed from posts:",
  RESTORE: "Restored",
  DELETE_FOREVER: "Deleted",
};

function ColHeader({ children }: { children: string }) {
  return <span className="cms-eyebrow">{children}</span>;
}

/** The list's state lives in the address (?q=&status=&parent=&sort=&size=&page=), so Back and a refresh keep it. */
function readQuery(params: URLSearchParams): TaxonomyQuery {
  const status = params.get("status");
  const sort = params.get("sort") as TaxonomySort | null;
  const size = params.get("size");
  return {
    q: params.get("q") ?? undefined,
    status: status === "ACTIVE" || status === "INACTIVE" ? status : undefined,
    parentId: Number(params.get("parent")) || undefined,
    sort: sort && SORTS.includes(sort) ? sort : undefined,
    page: Number(params.get("page")) || 1,
    size: size === null ? 20 : Number(size),
  };
}

/**
 * Categories (CAT-01 to CAT-06) and tags (TAG-01 to TAG-06): one screen for both. A list with search,
 * filters, sort and paging; create and edit in a side panel; quick actions on each row and bulk actions
 * on a selection (each says what it could not do); a Trash tab (30 days); and the published-post count
 * as a link to those posts.
 */
export default function TaxonomyPage({ kind }: { kind: TaxonomyKind }) {
  const text = KIND_TEXT[kind];
  const { site } = useSite();
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const [params, setParams] = useSearchParams();
  const query = useMemo(() => readQuery(params), [params]);
  const inTrash = params.get("status") === "TRASH";
  const [search, setSearch] = useState(query.q ?? "");
  const [selection, setSelection] = useState<{ key: string; ids: number[] }>({ key: "", ids: [] });
  const scope = `${site}/${kind}`;
  const selected = selection.key === scope ? selection.ids : [];
  const setSelected = (next: number[] | ((prev: number[]) => number[])) =>
    setSelection((prev) => {
      const current = prev.key === scope ? prev.ids : [];
      return { key: scope, ids: typeof next === "function" ? next(current) : next };
    });
  // "new" opens an empty panel; a row opens that item in it.
  const [editing, setEditing] = useState<TaxonomyRow | "new" | null>(null);

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
    queryKey: ["taxonomy-list", site, kind, query],
    queryFn: () => listTaxonomy(site!, kind, query),
    enabled: !!site && !inTrash,
    placeholderData: keepPreviousData,
    refetchOnMount: "always",
  });
  // The page counts (tabs, trash) and the parent choices need the whole list, whatever is filtered.
  const everything = useQuery({
    queryKey: ["taxonomy-all", site, kind],
    queryFn: () => allTaxonomy(site!, kind),
    enabled: !!site,
    refetchOnMount: "always",
  });
  // The tab counts, which also have to show while the Trash tab is open (the list is off then).
  const head = useQuery({
    queryKey: ["taxonomy-list", site, kind, "counts"],
    queryFn: () => listTaxonomy(site!, kind, { size: 1 }),
    enabled: !!site,
    refetchOnMount: "always",
  });
  // A search result or the Activity page opens an item with ?edit=<id>.
  const editId = Number(params.get("edit")) || null;
  const toEdit = editId ? everything.data?.find((r) => r.id === editId) : undefined;
  const current = editing ?? toEdit ?? null;
  const closeEditor = () => {
    setEditing(null);
    if (editId) update({ edit: undefined });
  };
  const trashCount = head.data?.trashCount;
  const counts = head.data?.statusCounts;
  const total = counts ? counts.ACTIVE + counts.INACTIVE : everything.data?.length;

  const refresh = () => {
    queryClient.invalidateQueries({ queryKey: ["taxonomy-list", site, kind] });
    queryClient.invalidateQueries({ queryKey: ["taxonomy-all", site, kind] });
    queryClient.invalidateQueries({ queryKey: ["taxonomy-trash", site, kind] });
    queryClient.invalidateQueries({ queryKey: ["posts-list", site] });
  };

  const bulk = useMutation({
    mutationFn: ({ ids, action }: { ids: number[]; action: TaxonomyBulkAction }) => bulkTaxonomy(site!, kind, ids, action),
    onSuccess: (result, { action }) => {
      refresh();
      setSelected((prev) => prev.filter((id) => !result.updated.includes(id)));
      showBulkResult(result, DONE[action], text.singular);
    },
    onError: () => message.error("The change could not be made. Nothing was changed."),
  });

  const confirmDelete = (rows: { id: number; name: string }[], postCount: number) => {
    const many = rows.length > 1;
    Modal.confirm({
      title: `Move ${many ? `${rows.length} ${text.plural}` : `“${rows[0].name}”`} to the Trash?`,
      width: 460,
      content: (
        <div>
          {kind === "categories"
            ? "The posts in it move to “Uncategorized”, and its sub-categories become top-level. "
            : "The posts that carry it lose it. "}
          Posts are never deleted. It can be restored for {TRASH_DAYS} days with its posts
          {postCount > 0 ? ` (${postCount} published ${postCount === 1 ? "post" : "posts"} use it)` : ""}, then it is deleted for good.
        </div>
      ),
      okText: "Move to Trash",
      okButtonProps: { danger: true },
      onOk: () => bulk.mutateAsync({ ids: rows.map((r) => r.id), action: "DELETE" }),
    });
  };

  const rows = useMemo(() => list.data?.items ?? [], [list.data]);
  const byId = useMemo(() => new Map(rows.map((r) => [r.id, r])), [rows]);
  const filtersOn = Boolean(query.q || query.status || query.parentId || query.sort);
  const parents = (everything.data ?? []).filter((r) => r.parentId === null && !r.isDefault);
  const clearAll = () => {
    setSearch("");
    setParams({}, { replace: true });
  };

  const columns: ColumnsType<TaxonomyRow> = [
    {
      title: <ColHeader>Name</ColHeader>,
      dataIndex: "name",
      render: (name: string, row) => (
        <a
          onClick={() => setEditing(row)}
          style={{ display: "block", fontSize: 14, fontWeight: 600, color: "#0b1b3a", cursor: "pointer", paddingLeft: row.parentId ? 18 : 0 }}
        >
          {row.parentId ? <span style={{ color: "#9aa8c0", marginRight: 6 }}>↳</span> : null}
          {name}
          {row.isDefault && <span className="cms-status" style={{ marginLeft: 8 }}>Default</span>}
        </a>
      ),
    },
    {
      title: <ColHeader>Slug</ColHeader>,
      dataIndex: "slug",
      width: 170,
      ellipsis: true,
      render: (v: string) => <span style={{ fontFamily: "var(--font-mono)", fontSize: 12, color: "#5b6b86" }}>{v}</span>,
    },
    {
      title: <ColHeader>Description</ColHeader>,
      dataIndex: "description",
      width: 220,
      ellipsis: true,
      render: (v: string | null) => <span style={{ fontSize: 13, color: "#5b6b86" }}>{v || "—"}</span>,
    },
    ...(kind === "categories"
      ? [
          {
            title: <ColHeader>Parent</ColHeader>,
            dataIndex: "parentName",
            width: 130,
            ellipsis: true,
            render: (v: string | null) => <span style={{ fontSize: 14, color: "#5b6b86" }}>{v || "—"}</span>,
          } as ColumnsType<TaxonomyRow>[number],
        ]
      : []),
    {
      title: <ColHeader>Posts</ColHeader>,
      dataIndex: "publishedCount",
      width: 84,
      render: (n: number, row) =>
        n > 0 ? (
          <a
            title="Show the published posts"
            onClick={() => navigate(`/content/posts?status=PUBLISHED&${kind === "categories" ? "categoryId" : "tagId"}=${row.id}`)}
            style={{ fontWeight: 600 }}
          >
            {n.toLocaleString("en-US")}
          </a>
        ) : (
          <span style={{ color: "#9aa8c0" }}>0</span>
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
      width: 96,
      render: (status: TaxonomyStatus) => (
        <span className={`cms-status${status === "ACTIVE" ? " is-published" : ""}`}>{status === "ACTIVE" ? "Active" : "Inactive"}</span>
      ),
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
              { key: "edit", label: "Edit", onClick: () => setEditing(row) },
              row.status === "ACTIVE"
                ? { key: "off", label: "Make inactive", disabled: row.isDefault, onClick: () => bulk.mutate({ ids: [row.id], action: "DEACTIVATE" }) }
                : { key: "on", label: "Make active", onClick: () => bulk.mutate({ ids: [row.id], action: "ACTIVATE" }) },
              {
                key: "strip",
                label: `Remove from all posts`,
                disabled: row.isDefault,
                onClick: () =>
                  Modal.confirm({
                    title: `Remove “${row.name}” from every post?`,
                    content: `The ${text.singular} stays; the posts just stop carrying it${kind === "categories" ? " (a post left with no category goes to “Uncategorized”)" : ""}.`,
                    okText: "Remove from posts",
                    onOk: () => bulk.mutateAsync({ ids: [row.id], action: "REMOVE_FROM_POSTS" }),
                  }),
              },
              { type: "divider" as const },
              { key: "del", danger: true, label: "Move to Trash", disabled: row.isDefault, onClick: () => confirmDelete([row], row.publishedCount) },
            ],
          }}
        >
          <Button type="text" size="small" aria-label="More actions" icon={<MoreIcon />} />
        </Dropdown>
      ),
    },
  ];

  const size = query.size ?? 20;
  const none = selected.length === 0;
  const selectedRows = selected.map((id) => byId.get(id)).filter((r): r is TaxonomyRow => !!r);
  const selectedHasDefault = selectedRows.some((r) => r.isDefault);

  return (
    <div>
      <div className="flex flex-wrap items-end justify-between gap-5 mb-6">
        <div className="min-w-0">
          <h1 className="cms-page-title">{text.Plural}</h1>
          <p className="cms-page-sub">
            {total === undefined ? "…" : total.toLocaleString("en-US")} {text.plural}
            {counts ? ` · ${counts.ACTIVE} active` : ""}
          </p>
        </div>
        <Button className="cms-cta" icon={<PlusIcon />} onClick={() => setEditing("new")}>
          New {text.singular}
        </Button>
      </div>

      <div className="mb-4" style={{ overflowX: "auto", maxWidth: "100%" }}>
        <div className="cms-segmented" role="tablist" aria-label="Status" style={{ whiteSpace: "nowrap" }}>
          {[
            { label: "All", value: undefined as string | undefined, n: total },
            { label: "Active", value: "ACTIVE", n: counts?.ACTIVE },
            { label: "Inactive", value: "INACTIVE", n: counts?.INACTIVE },
            { label: "Trash", value: "TRASH", n: trashCount },
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
        <TaxonomyTrash site={site} kind={kind} onChanged={refresh} />
      ) : (
        <>
          <div className="flex flex-wrap items-center gap-3 mb-4">
            <Input.Search
              allowClear
              placeholder="Search name or slug"
              style={{ width: 240 }}
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              onSearch={(v) => update({ q: v.trim() || undefined })}
            />
            {kind === "categories" && (
              <Select
                allowClear
                placeholder="Any parent"
                style={{ width: 180 }}
                value={query.parentId}
                onChange={(v) => update({ parent: v ? String(v) : undefined })}
                options={parents.map((p) => ({ value: p.id, label: p.name }))}
              />
            )}
            <Select
              style={{ width: 200 }}
              value={query.sort ?? "name_asc"}
              onChange={(v) => update({ sort: v === "name_asc" ? undefined : v })}
              options={SORTS.map((s) => ({ value: s, label: SORT_LABELS[s] }))}
            />
            <Select
              style={{ width: 120 }}
              value={size}
              onChange={(v) => update({ size: v === 20 ? undefined : String(v) })}
              options={TAXONOMY_PAGE_SIZES.map((n) => ({ value: n, label: n === 0 ? "All" : `${n} per page` }))}
            />
            {filtersOn && (
              <Button type="link" onClick={clearAll}>
                Clear filters
              </Button>
            )}
          </div>

          <div
            className="flex flex-wrap items-center gap-3 mb-4"
            style={{ padding: "10px 14px", borderRadius: 12, background: none ? "#f4f7fc" : "#eaf2ff", border: `1px solid ${none ? "#e3e9f4" : "#bcd9fb"}` }}
          >
            <b style={{ color: "#0b1b3a" }}>{selected.length} selected</b>
            <Button disabled={none} loading={bulk.isPending} onClick={() => bulk.mutate({ ids: selected, action: "ACTIVATE" })}>
              Make active
            </Button>
            <Button disabled={none || selectedHasDefault} loading={bulk.isPending} onClick={() => bulk.mutate({ ids: selected, action: "DEACTIVATE" })}>
              Make inactive
            </Button>
            <Popconfirm
              title={`Move ${selected.length} ${selected.length === 1 ? text.singular : text.plural} to the Trash?`}
              okText="Move to Trash"
              onConfirm={() => confirmDelete(selectedRows.length ? selectedRows : selected.map((id) => ({ id, name: String(id) })), selectedRows.reduce((n, r) => n + r.publishedCount, 0))}
              disabled={none || selectedHasDefault}
            >
              <Button danger disabled={none || selectedHasDefault} loading={bulk.isPending}>
                Move to Trash
              </Button>
            </Popconfirm>
            {selectedHasDefault && <span style={{ fontSize: 12, color: "#7b8aa5" }}>“Uncategorized” cannot be deactivated or deleted</span>}
            {!none && (
              <Button type="link" onClick={() => setSelected([])}>
                Clear selection
              </Button>
            )}
          </div>

          <Table<TaxonomyRow>
            className="cms-table"
            rowKey="id"
            columns={columns}
            dataSource={rows}
            loading={list.isFetching}
            tableLayout="fixed"
            scroll={{ x: 960 }}
            rowSelection={{
              selectedRowKeys: selected,
              preserveSelectedRowKeys: true,
              onChange: (keys) => setSelected(keys as number[]),
            }}
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
                <Empty description={`The ${text.plural} could not be loaded`} />
              ) : filtersOn ? (
                <div className="py-14 text-center">
                  <div style={{ fontSize: 16, fontWeight: 700, color: "#0b1b3a" }}>No {text.plural} match</div>
                  <Button className="mt-3" onClick={clearAll}>
                    Clear filters
                  </Button>
                </div>
              ) : (
                <div className="py-14 text-center">
                  <div style={{ fontSize: 16, fontWeight: 700, color: "#0b1b3a" }}>Nothing here yet</div>
                  <p className="mt-2 mb-0" style={{ fontSize: 14, color: "#5b6b86" }}>
                    Create the first {text.singular}.
                  </p>
                </div>
              ),
            }}
          />
        </>
      )}

      <Drawer
        open={current !== null}
        onClose={closeEditor}
        width={440}
        title={current === "new" ? `New ${text.singular}` : `Edit ${text.singular}`}
        destroyOnHidden
      >
        {current !== null && site && (
          <TaxonomyForm
            key={current === "new" ? "new" : current.id}
            site={site}
            kind={kind}
            row={current === "new" ? null : current}
            parents={parents}
            onDone={() => {
              closeEditor();
              refresh();
            }}
          />
        )}
      </Drawer>
    </div>
  );
}

function TaxonomyForm({
  site,
  kind,
  row,
  parents,
  onDone,
}: {
  site: string;
  kind: TaxonomyKind;
  row: TaxonomyRow | null;
  parents: TaxonomyRow[];
  onDone: () => void;
}) {
  const text = KIND_TEXT[kind];
  const [name, setName] = useState(row?.name ?? "");
  const [slug, setSlug] = useState(row?.slug ?? "");
  // A new item's address follows its name until someone types one; an existing one is left alone.
  const [slugTouched, setSlugTouched] = useState(row !== null);
  const [description, setDescription] = useState(row?.description ?? "");
  const [parentId, setParentId] = useState<number | null>(row?.parentId ?? null);
  const [active, setActive] = useState(row ? row.status === "ACTIVE" : true);

  const parentChoices = parents.filter((p) => p.id !== row?.id);
  const chosenParent = parentChoices.find((p) => p.id === parentId);
  const parentInactive = !!chosenParent && chosenParent.status !== "ACTIVE";

  const save = useMutation({
    mutationFn: () => {
      const input = {
        name: name.trim(),
        slug: slug.trim() || undefined,
        description: description.trim(),
        ...(kind === "categories" ? { parentId } : {}),
        status: (active && !parentInactive ? "ACTIVE" : "INACTIVE") as TaxonomyStatus,
        ...(row ? { version: row.version } : {}),
      };
      return row ? updateTaxonomy(site, kind, row.id, input) : createTaxonomy(site, kind, input);
    },
    onSuccess: () => {
      message.success(row ? "Saved" : `${text.Singular} created`);
      onDone();
    },
    onError: (e: unknown) => {
      const reason = (e as { response?: { data?: { status?: { message?: string } } } })?.response?.data?.status?.message;
      message.error(reason || "Could not save");
    },
  });

  const label = { display: "block", margin: "14px 0 4px", fontSize: 12, fontWeight: 600, color: "#5b6b86" } as const;
  return (
    <div>
      <label style={{ ...label, marginTop: 0 }}>Name</label>
      <Input
        autoFocus
        value={name}
        maxLength={100}
        onChange={(e) => {
          setName(e.target.value);
          if (!slugTouched) setSlug(slugify(e.target.value));
        }}
        placeholder={kind === "categories" ? "e.g. Press releases" : "e.g. Cybersecurity"}
      />
      <label style={label}>Slug (the address segment)</label>
      <Input
        value={slug}
        maxLength={100}
        disabled={row?.isDefault}
        onChange={(e) => {
          setSlug(slugify(e.target.value));
          setSlugTouched(true);
        }}
        addonBefore={`/${text.singular}/`}
        placeholder="made from the name"
      />
      {row && row.slug !== slug && slug && (
        <div style={{ fontSize: 12, color: "#b36b00", marginTop: 4 }}>The old address keeps working: it redirects to the new one.</div>
      )}
      <label style={label}>Description</label>
      <Input.TextArea
        value={description}
        maxLength={500}
        autoSize={{ minRows: 3, maxRows: 6 }}
        onChange={(e) => setDescription(e.target.value)}
        placeholder="Shown on its page (optional)"
      />
      {kind === "categories" && !row?.isDefault && (
        <>
          <label style={label}>Parent category</label>
          <Select
            allowClear
            style={{ width: "100%" }}
            placeholder="None (top level)"
            value={parentId ?? undefined}
            onChange={(v) => setParentId(v ?? null)}
            options={parentChoices.map((p) => ({ value: p.id, label: p.status === "ACTIVE" ? p.name : `${p.name} (inactive)` }))}
          />
        </>
      )}
      <label style={label}>Status</label>
      <div className="flex items-center gap-3">
        <Switch checked={active && !parentInactive} disabled={row?.isDefault || parentInactive} onChange={setActive} />
        <span style={{ fontSize: 14, color: "#26354f" }}>{active && !parentInactive ? "Active" : "Inactive"}</span>
      </div>
      <div style={{ fontSize: 12, color: "#7b8aa5", marginTop: 6 }}>
        {parentInactive
          ? "Its parent is inactive, so it stays inactive until the parent is made active."
          : "An inactive one cannot be added to new posts and is hidden on the website; posts that already have it keep it."}
      </div>
      <div className="flex justify-end gap-2" style={{ marginTop: 24 }}>
        <Button onClick={onDone}>Cancel</Button>
        <Button type="primary" disabled={!name.trim()} loading={save.isPending} onClick={() => save.mutate()}>
          Save
        </Button>
      </div>
    </div>
  );
}

/** The Trash for one kind: restore (as inactive, with its posts) or delete for good. */
function TaxonomyTrash({ site, kind, onChanged }: { site: string; kind: TaxonomyKind; onChanged: () => void }) {
  const text = KIND_TEXT[kind];
  const queryClient = useQueryClient();
  const [page, setPage] = useState(1);
  const [selected, setSelected] = useState<number[]>([]);
  const trash = useQuery({
    queryKey: ["taxonomy-trash", site, kind, page],
    queryFn: () => listTaxonomyTrash(site, kind, undefined, page),
    placeholderData: keepPreviousData,
    refetchOnMount: "always",
  });
  // Items whose address is taken: the person picks another one and restores them again.
  const [clashes, setClashes] = useState<{ id: number; name: string; slug: string }[]>([]);
  const [typed, setTyped] = useState<Record<number, string>>({});
  const act = useMutation({
    mutationFn: ({ ids, action, slugs }: { ids: number[]; action: TaxonomyBulkAction; slugs?: Record<number, string> }) =>
      bulkTaxonomy(site, kind, ids, action, slugs),
    onSuccess: (result, { action }) => {
      queryClient.invalidateQueries({ queryKey: ["taxonomy-trash", site, kind] });
      onChanged();
      setSelected((prev) => prev.filter((id) => !result.updated.includes(id)));
      const taken = action === "RESTORE" ? result.failed.filter((f) => f.reason.includes("already used")) : [];
      if (taken.length === 0) {
        showBulkResult(result, DONE[action], text.singular);
        return;
      }
      const rows = trash.data?.items ?? [];
      const found = taken.map((f) => {
        const row = rows.find((r) => r.id === f.id);
        return { id: f.id, name: f.title ?? row?.name ?? "", slug: row?.slug ?? "" };
      });
      const others = result.failed.filter((f) => !taken.some((t) => t.id === f.id));
      if (result.updated.length > 0 || others.length > 0) showBulkResult({ updated: result.updated, failed: others }, DONE[action], text.singular);
      setTyped(Object.fromEntries(found.map((c) => [c.id, c.slug ? `${c.slug}-2` : ""])));
      setClashes(found);
    },
    onError: () => message.error("The change could not be made. Nothing was changed."),
  });
  const none = selected.length === 0;
  const columns: ColumnsType<TaxonomyTrashRow> = [
    { title: <ColHeader>Name</ColHeader>, dataIndex: "name", render: (v: string) => <b style={{ color: "#0b1b3a" }}>{v}</b> },
    {
      title: <ColHeader>Slug</ColHeader>,
      dataIndex: "slug",
      width: 180,
      render: (v: string | null) => <span style={{ fontFamily: "var(--font-mono)", fontSize: 12, color: "#5b6b86" }}>{v}</span>,
    },
    {
      title: <ColHeader>Posts it gets back</ColHeader>,
      dataIndex: "postCount",
      width: 150,
      render: (n: number) => <span style={{ color: "#5b6b86" }}>{n}</span>,
    },
    {
      title: <ColHeader>Moved to Trash</ColHeader>,
      dataIndex: "trashedAt",
      width: 200,
      render: (v: string, row) => {
        const left = Math.max(0, TRASH_DAYS - dayjs().diff(dayjs(v), "day"));
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
        Moved here for {TRASH_DAYS} days, then deleted for good. Restoring brings a {text.singular} back as inactive, with the posts it had
        {kind === "categories" ? " and its sub-categories" : ""}.
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
          title={`Delete ${selected.length} ${selected.length === 1 ? text.singular : text.plural} for good?`}
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
      <Modal
        open={clashes.length > 0}
        title="Choose another address"
        okText="Restore under these addresses"
        okButtonProps={{ loading: act.isPending, disabled: clashes.some((c) => !typed[c.id]?.trim()) }}
        onCancel={() => setClashes([])}
        onOk={() => {
          const slugs = Object.fromEntries(clashes.map((c) => [c.id, typed[c.id].trim()]));
          const ids = clashes.map((c) => c.id);
          setClashes([]);
          act.mutate({ ids, action: "RESTORE", slugs });
        }}
        destroyOnHidden
      >
        <p style={{ marginTop: 0, color: "#5b6b86" }}>
          {clashes.length === 1 ? `This ${text.singular}'s address is` : `These ${text.plural}' addresses are`} already used. Give {clashes.length === 1 ? "it" : "each"} a new one and restore:
        </p>
        {clashes.map((c) => (
          <div key={c.id} style={{ marginBottom: 12 }}>
            <div style={{ fontSize: 13, fontWeight: 600, color: "#0b1b3a" }}>{c.name}</div>
            <Input value={typed[c.id] ?? ""} addonBefore={`was ${c.slug}`} maxLength={100} onChange={(e) => setTyped((t) => ({ ...t, [c.id]: e.target.value }))} />
          </div>
        ))}
      </Modal>
      <Table<TaxonomyTrashRow>
        className="cms-table"
        rowKey="id"
        columns={columns}
        dataSource={trash.data?.items ?? []}
        loading={trash.isFetching}
        rowSelection={{ selectedRowKeys: selected, preserveSelectedRowKeys: true, onChange: (keys) => setSelected(keys as number[]) }}
        pagination={{
          current: trash.data?.page ?? page,
          pageSize: 20,
          total: trash.data?.totalItems ?? 0,
          showSizeChanger: false,
          hideOnSinglePage: true,
          onChange: setPage,
        }}
        locale={{ emptyText: <div className="py-14 text-center" style={{ color: "#5b6b86" }}>The Trash is empty</div> }}
      />
    </>
  );
}
