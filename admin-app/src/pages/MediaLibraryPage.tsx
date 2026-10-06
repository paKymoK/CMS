import { useEffect, useRef, useState } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { Upload, Button, Spin, Empty, message, Popconfirm, Tabs } from "antd";
import { InboxOutlined, DeleteOutlined } from "@ant-design/icons";
import Hls from "hls.js";
import { useSite } from "../lib/useSite";
import { siteLabel } from "../config/sites";
import { mediaApi } from "../lib/api";
import {
  listImages,
  listVideos,
  uploadImage,
  uploadVideo,
  imageUrl,
  videoPlaybackUrl,
  type UploadFile,
  type VideoJob,
} from "../lib/mediaClient";
import { mediaSrc } from "../lib/media";

const STATUS_STYLE: Record<VideoJob["status"], { bg: string; color: string; label: string }> = {
  DONE: { bg: "#ffffff", color: "#0b63c5", label: "DONE" },
  FAILED: { bg: "#ffe1e1", color: "#a8071a", label: "FAILED" },
  QUEUED: { bg: "rgba(255,255,255,.18)", color: "#ffffff", label: "QUEUED" },
  PROCESSING: { bg: "rgba(255,255,255,.18)", color: "#ffffff", label: "PROCESSING" },
};

function VideoThumb({ src }: { src: string }) {
  const ref = useRef<HTMLVideoElement>(null);
  useEffect(() => {
    const video = ref.current;
    if (!video) return;
    if (Hls.isSupported()) {
      const hls = new Hls();
      hls.loadSource(src);
      hls.attachMedia(video);
      return () => hls.destroy();
    }
    video.src = src;
  }, [src]);
  return <video ref={ref} muted controls style={{ width: "100%", aspectRatio: "16/9", objectFit: "cover", display: "block" }} />;
}

function Dropzone({
  noun,
  hint,
  accept,
  onFile,
}: {
  noun: string;
  hint: string;
  accept: string;
  onFile: (file: File) => unknown;
}) {
  return (
    <Upload.Dragger
      accept={accept}
      showUploadList={false}
      beforeUpload={(file) => {
        onFile(file);
        return false;
      }}
      style={{
        marginBottom: 24,
        padding: 0,
        border: "1px dashed #b9c6d3",
        background: "linear-gradient(#f2f7fd,#e7f0fa)",
        borderRadius: 0,
      }}
    >
      <div className="flex flex-wrap items-center justify-between gap-4 px-2 py-1">
        <div className="flex items-center gap-3">
          <InboxOutlined style={{ fontSize: 22, color: "#189fe0" }} />
          <div className="text-left">
            <div style={{ fontSize: 15, fontWeight: 600, color: "#10314f" }}>
              Drop {noun} here, or browse
            </div>
            <div className="cms-eyebrow mt-1" style={{ fontSize: 10 }}>
              {hint}
            </div>
          </div>
        </div>
        <span
          style={{
            padding: "12px 20px",
            background: "#189fe0",
            fontFamily: "var(--font-mono)",
            fontSize: 12,
            letterSpacing: ".1em",
            color: "#ffffff",
          }}
        >
          UPLOAD {noun.slice(0, -1).toUpperCase()}
        </span>
      </div>
    </Upload.Dragger>
  );
}

export default function MediaLibraryPage() {
  const { site } = useSite();
  const queryClient = useQueryClient();
  const [tab, setTab] = useState("images");

  const imagesQuery = useQuery({
    queryKey: ["media-images", site],
    queryFn: () => listImages(site!),
    enabled: !!site,
  });

  const videosQuery = useQuery({
    queryKey: ["media-videos", site],
    queryFn: () => listVideos(site!),
    enabled: !!site,
  });

  const deleteVideoMutation = useMutation({
    mutationFn: async (videoId: string) =>
      mediaApi.delete(`/v1/videos/${videoId}`, { params: { site } }),
    onSuccess: () => {
      message.success("Video deleted");
      queryClient.invalidateQueries({ queryKey: ["media-videos", site] });
    },
  });

  const handleUploadImage = async (file: File) => {
    if (!site) return false as const;
    try {
      await uploadImage(site, file);
      message.success("Image uploaded");
      queryClient.invalidateQueries({ queryKey: ["media-images", site] });
    } catch {
      message.error("Upload failed");
    }
    return false as const;
  };

  const handleUploadVideo = async (file: File) => {
    if (!site) return false as const;
    try {
      await uploadVideo(site, file);
      message.success("Video queued for transcoding");
      setTimeout(
        () => queryClient.invalidateQueries({ queryKey: ["media-videos", site] }),
        3000,
      );
    } catch {
      message.error("Upload failed");
    }
    return false as const;
  };

  const imagesPanel = (
    <div>
      <Dropzone
        noun="images"
        hint="JPG · PNG · WEBP · SVG — FILE TYPE VERIFIED BY CONTENT"
        accept="image/*"
        onFile={handleUploadImage}
      />
      {imagesQuery.isLoading ? (
        <Spin />
      ) : !imagesQuery.data?.length ? (
        <Empty description="No images yet for this site" />
      ) : (
        <div className="grid gap-4" style={{ gridTemplateColumns: "repeat(auto-fill, minmax(170px, 1fr))" }}>
          {imagesQuery.data.map((file: UploadFile) => (
            <div key={file.id} className="flex flex-col gap-2">
              <img
                src={mediaSrc(imageUrl(file))}
                alt={file.name}
                style={{ width: "100%", aspectRatio: "4/3", objectFit: "cover", display: "block" }}
              />
              <span
                style={{
                  fontFamily: "var(--font-mono)",
                  fontSize: 11,
                  color: "#3d4046",
                  whiteSpace: "nowrap",
                  overflow: "hidden",
                  textOverflow: "ellipsis",
                }}
              >
                {file.name}
              </span>
            </div>
          ))}
        </div>
      )}
    </div>
  );

  const videosPanel = (
    <div>
      <Dropzone
        noun="videos"
        hint="MP4 · MOV — TRANSCODED TO HLS AFTER UPLOAD"
        accept="video/*"
        onFile={handleUploadVideo}
      />
      {videosQuery.isLoading ? (
        <Spin />
      ) : !videosQuery.data?.length ? (
        <Empty description="No videos yet for this site" />
      ) : (
        <div className="grid gap-4" style={{ gridTemplateColumns: "repeat(auto-fill, minmax(240px, 1fr))" }}>
          {videosQuery.data.map((job: VideoJob) => {
            const badge = STATUS_STYLE[job.status];
            return (
              <div key={job.jobId} style={{ border: "1px solid #e0e4e9", background: "#ffffff" }}>
                {job.status === "DONE" ? (
                  <VideoThumb src={videoPlaybackUrl(job)} />
                ) : (
                  <div
                    style={{
                      position: "relative",
                      aspectRatio: "16/9",
                      background: "repeating-linear-gradient(135deg, #0c2447 0 6px, #143c6e 6px 12px)",
                    }}
                  >
                    <span
                      style={{
                        position: "absolute",
                        top: 10,
                        right: 10,
                        padding: "4px 10px",
                        borderRadius: 999,
                        fontFamily: "var(--font-mono)",
                        fontSize: 10,
                        letterSpacing: ".08em",
                        background: badge.bg,
                        color: badge.color,
                      }}
                    >
                      {job.status === "FAILED" ? job.errorMessage ?? badge.label : badge.label}
                    </span>
                  </div>
                )}
                <div className="flex items-center justify-between gap-4 px-3.5 py-3">
                  <span style={{ fontSize: 13, color: "#55585f" }}>
                    {new Date(job.createdAt).toLocaleDateString()}
                  </span>
                  <Popconfirm title="Delete this video?" onConfirm={() => deleteVideoMutation.mutate(job.videoId)}>
                    <Button
                      size="small"
                      danger
                      icon={<DeleteOutlined />}
                      style={{ fontFamily: "var(--font-mono)", fontSize: 10, letterSpacing: ".08em" }}
                    >
                      DELETE
                    </Button>
                  </Popconfirm>
                </div>
              </div>
            );
          })}
        </div>
      )}
    </div>
  );

  return (
    <div>
      <div className="cms-eyebrow mb-3">— Library · {site ? siteLabel(site) : ""}</div>
      <h1 className="mt-0 mb-5" style={{ fontSize: "clamp(24px,3vw,32px)", fontWeight: 700, letterSpacing: "-.02em", color: "#10314f" }}>
        Media Library
      </h1>
      <Tabs
        activeKey={tab}
        onChange={setTab}
        items={[
          {
            key: "images",
            label: (
              <span className="flex items-center gap-2">
                Images
                <span style={{ fontFamily: "var(--font-mono)", fontSize: 11, color: "#9aa3ad" }}>
                  {imagesQuery.data?.length ?? 0}
                </span>
              </span>
            ),
            children: imagesPanel,
          },
          {
            key: "videos",
            label: (
              <span className="flex items-center gap-2">
                Videos
                <span style={{ fontFamily: "var(--font-mono)", fontSize: 11, color: "#9aa3ad" }}>
                  {videosQuery.data?.length ?? 0}
                </span>
              </span>
            ),
            children: videosPanel,
          },
        ]}
      />
    </div>
  );
}
