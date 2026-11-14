import { useState } from "react";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { Button, Select, message } from "antd";
import { useSite } from "../lib/useSite";
import { KIND_TEXT, MAX_PER_POST, createTaxonomy, type TaxonomyKind, type TaxonomyRow } from "../lib/taxonomy";

/**
 * Choose the categories or tags of a post (CAT-02, TAG-02): the chosen ones first, the rest A to Z; only
 * active ones can be added (an inactive one the post already has stays and can be taken off); a new one
 * can be made on the spot and is added to the post (it stays even if the post is not saved); at most
 * {@link MAX_PER_POST} categories and tags together.
 */
export default function TaxonomyPicker({
  kind,
  rows,
  value,
  onChange,
  chosenTotal,
}: {
  kind: TaxonomyKind;
  rows: TaxonomyRow[];
  value: number[];
  onChange: (ids: number[]) => void;
  /** How many categories and tags the post has in all (the cap spans both). */
  chosenTotal: number;
}) {
  const text = KIND_TEXT[kind];
  const { site } = useSite();
  const queryClient = useQueryClient();
  const [search, setSearch] = useState("");
  const atCap = chosenTotal >= MAX_PER_POST;

  const create = useMutation({
    mutationFn: (name: string) => createTaxonomy(site!, kind, { name }),
    onSuccess: (row) => {
      queryClient.invalidateQueries({ queryKey: ["taxonomy-all", site, kind] });
      queryClient.invalidateQueries({ queryKey: ["taxonomy-list", site, kind] });
      onChange([...value, row.id]);
      setSearch("");
      message.success(`${text.Singular} “${row.name}” created and added`);
    },
    onError: (e: unknown) => {
      const reason = (e as { response?: { data?: { status?: { message?: string } } } })?.response?.data?.status?.message;
      message.error(reason || `Could not create the ${text.singular}`);
    },
  });

  const options = rows
    .filter((r) => r.status === "ACTIVE" || value.includes(r.id))
    .map((r) => ({
      value: r.id,
      label: r.status === "ACTIVE" ? r.name : `${r.name} (inactive)`,
      disabled: atCap && !value.includes(r.id),
    }))
    .sort((a, b) => Number(value.includes(b.value)) - Number(value.includes(a.value)) || a.label.localeCompare(b.label));

  const typed = search.trim();
  const exists = rows.some((r) => r.name.toLowerCase() === typed.toLowerCase());

  return (
    <div>
      <Select
        mode="multiple"
        style={{ width: "100%" }}
        placeholder={`Choose ${text.plural}`}
        value={value}
        options={options}
        onChange={onChange}
        showSearch
        searchValue={search}
        onSearch={setSearch}
        optionFilterProp="label"
        filterOption={(input, option) => String(option?.label ?? "").toLowerCase().includes(input.toLowerCase())}
        popupRender={(menu) => (
          <>
            {menu}
            {typed && !exists && (
              <div style={{ padding: "6px 8px", borderTop: "1px solid #eef2f9" }}>
                <Button
                  type="link"
                  size="small"
                  disabled={atCap}
                  loading={create.isPending}
                  onClick={() => create.mutate(typed)}
                >
                  + Create {text.singular} “{typed}”
                </Button>
              </div>
            )}
          </>
        )}
        // Not null: antd hides the whole list (and the Create button in it) when nothing matches and this is null.
        notFoundContent={<span style={{ display: "block", padding: "6px 12px", fontSize: 12, color: "#7b8aa5" }}>{typed ? `No ${text.singular} named “${typed}” yet` : `No ${text.plural} yet`}</span>}
      />
      <div style={{ fontSize: 12, color: atCap ? "#b36b00" : "#7b8aa5", marginTop: 4 }}>
        {chosenTotal} / {MAX_PER_POST} categories and tags
        {atCap ? ": the limit is reached" : ""}
      </div>
    </div>
  );
}
