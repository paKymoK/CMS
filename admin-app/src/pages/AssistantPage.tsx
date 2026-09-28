import { useMutation } from "@tanstack/react-query";
import { Button, Popconfirm, message } from "antd";
import { ArrowRightOutlined } from "@ant-design/icons";
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
      <div className="cms-eyebrow mb-3">— Library · {label}</div>
      <h1 className="mt-0 mb-5" style={{ fontSize: "clamp(24px,3vw,32px)", fontWeight: 700, letterSpacing: "-.02em", color: "#10314f" }}>
        Assistant
      </h1>

      <div
        style={{
          position: "relative",
          overflow: "hidden",
          padding: "clamp(24px,4vw,44px)",
          background: "linear-gradient(180deg, #03091a, #04102a, #061634)",
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
              width: 40,
              height: 40,
              borderRadius: "50%",
              marginBottom: 18,
              background:
                "radial-gradient(circle at 34% 30%, #eaf6ff 0%, #7cc4f7 42%, #1a6fc4 78%, #0c3f7d 100%)",
              boxShadow: "0 0 22px rgba(90,180,255,.55)",
            }}
          />
          <h2 style={{ margin: 0, fontSize: "clamp(20px,2.4vw,26px)", fontWeight: 700, color: "#ffffff" }}>
            Knowledge base for <span style={{ color: "#43a4ff" }}>{label}</span>
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
              loading={ingestMutation.isPending}
              style={{
                display: "inline-flex",
                alignItems: "center",
                gap: 12,
                border: "none",
                borderRadius: 999,
                background: "#ffffff",
                padding: "6px 6px 6px 24px",
                height: "auto",
                fontSize: 14,
                fontWeight: 600,
                color: "#0d2b52",
              }}
            >
              Re-ingest knowledge base
              <span
                style={{
                  display: "flex",
                  width: 36,
                  height: 36,
                  borderRadius: "50%",
                  background: "#0b63c5",
                  color: "#ffffff",
                  alignItems: "center",
                  justifyContent: "center",
                }}
              >
                <ArrowRightOutlined style={{ fontSize: 14 }} />
              </span>
            </Button>
          </Popconfirm>
        </div>
      </div>
    </div>
  );
}
