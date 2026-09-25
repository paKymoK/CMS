import { useMutation } from "@tanstack/react-query";
import { Button, Popconfirm, Typography, message } from "antd";
import { SyncOutlined } from "@ant-design/icons";
import { chatApi } from "../lib/api";
import { useSite } from "../lib/useSite";
import { siteLabel } from "../config/sites";

/**
 * Triggers chat-service's POST /v1/assistant/ingest for the currently selected site — see
 * SiteIngestionService. This is a full wipe-and-reload of that site's Qdrant collection from its
 * curated documents/<site>/ files, not an incremental update, so it's confirmed before firing.
 */
export default function AssistantPage() {
  const { site } = useSite();

  const ingestMutation = useMutation({
    mutationFn: async () => {
      const { data } = await chatApi.post("/v1/assistant/ingest", null, { params: { site } });
      return data.data as number;
    },
    onSuccess: (count) => {
      message.success(
        `Ingested ${count} document${count === 1 ? "" : "s"} for ${siteLabel(site ?? "")}.`,
      );
    },
  });

  return (
    <div className="max-w-xl">
      <h2 className="text-lg font-semibold mb-1">Assistant knowledge base</h2>
      <Typography.Paragraph type="secondary">
        Re-ingests the public chatbot's knowledge base for <strong>{siteLabel(site ?? "")}</strong>{" "}
        from its curated documents. This replaces everything previously ingested for this site — it
        does not merge with it.
      </Typography.Paragraph>
      <Popconfirm
        title="Re-ingest knowledge base?"
        description={`This replaces all of ${siteLabel(site ?? "")}'s existing chatbot knowledge.`}
        okText="Re-ingest"
        onConfirm={() => ingestMutation.mutate()}
      >
        <Button type="primary" icon={<SyncOutlined />} loading={ingestMutation.isPending}>
          Re-ingest knowledge base
        </Button>
      </Popconfirm>
    </div>
  );
}
