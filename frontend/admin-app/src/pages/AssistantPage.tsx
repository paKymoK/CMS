import { useMutation } from "@tanstack/react-query";
import { Button, Popconfirm, message } from "antd";
import { ArrowRightIcon } from "../components/icons";
import { chatApi } from "../lib/api";
import { useSite } from "../lib/useSite";
import { siteLabel } from "../config/sites";

/**
 * Triggers chat-service's POST /v1/assistant/ingest for the currently selected site — see
 * SiteIngestionService. This is a full wipe-and-reload of that site's Qdrant collection from its
 * curated documents/<site>/ files, not an incremental update, so it's confirmed before firing.
 *
 * The hero only surfaces what ingest() actually returns (a document count, via the success toast)
 * — no document list or chunk/collection stats grid, since chat-service exposes no endpoint for
 * those today. Showing them would mean fabricating numbers.
 */
export default function AssistantPage() {
  const { site } = useSite();
  const label = siteLabel(site ?? "");

  const ingestMutation = useMutation({
    mutationFn: async () => {
      const { data } = await chatApi.post("/v1/assistant/ingest", null, { params: { site } });
      return data.data as number;
    },
    onSuccess: (count) => {
      message.success(`Ingested ${count} document${count === 1 ? "" : "s"} for ${label}.`);
    },
  });

  return (
    <div>
      <h1 className="cms-page-title">Assistant</h1>
      <p className="cms-page-sub" style={{ marginBottom: 20 }}>
        Keep the public chatbot's knowledge in sync with this site's documents.
      </p>

      <div
        style={{
          position: "relative",
          overflow: "hidden",
          padding: "clamp(24px,4vw,44px)",
          borderRadius: 16,
          background: "#001449",
          boxShadow: "0 8px 24px rgba(10,30,70,.12)",
        }}
      >
        <div
          style={{
            position: "absolute",
            inset: 0,
            pointerEvents: "none",
            background:
              "linear-gradient(180deg, rgba(150,195,255,.09), rgba(120,175,255,.03) 30%, rgba(8,32,74,.1))",
          }}
        />
        <div
          style={{
            position: "absolute",
            left: 0,
            right: 0,
            top: 0,
            height: 1,
            background: "linear-gradient(90deg, transparent, rgba(170,212,255,.34), transparent)",
          }}
        />
        <div style={{ position: "relative", maxWidth: 460 }}>
          <div
            style={{
              width: 48,
              height: 48,
              borderRadius: 14,
              marginBottom: 18,
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
              color: "#ffffff",
              background: "linear-gradient(135deg, #00bbff, #0083ff)",
              boxShadow: "0 10px 24px rgba(0,131,255,.4)",
            }}
          >
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" aria-hidden>
              <path d="M12 3l1.8 5.2L19 10l-5.2 1.8L12 17l-1.8-5.2L5 10l5.2-1.8Z" />
            </svg>
          </div>
          <h2 style={{ margin: 0, fontSize: "clamp(20px,2.4vw,26px)", fontWeight: 700, color: "#ffffff" }}>
            Knowledge base for <span style={{ color: "#00bbff" }}>{label}</span>
          </h2>
          <p style={{ margin: "12px 0 24px", fontSize: 14, lineHeight: 1.6, color: "#dce7f5" }}>
            Re-ingests the public chatbot's knowledge from this site's curated documents. This
            replaces everything previously ingested — it does not merge.
          </p>
          <Popconfirm
            title="Re-ingest knowledge base?"
            description={`This replaces all of ${label}'s existing chatbot knowledge.`}
            okText="Re-ingest"
            onConfirm={() => ingestMutation.mutate()}
          >
            <Button
              type="primary"
              size="large"
              loading={ingestMutation.isPending}
              icon={<ArrowRightIcon />}
              iconPlacement="end"
              style={{ boxShadow: "0 6px 16px rgba(0,131,255,.35)" }}
            >
              Re-ingest knowledge base
            </Button>
          </Popconfirm>
        </div>
      </div>
    </div>
  );
}
