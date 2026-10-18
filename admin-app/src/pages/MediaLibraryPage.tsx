import { useEffect, useRef, useState } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { Upload, Button, Spin, Empty, message, Popconfirm, Tabs } from "antd";
import { InboxIcon, DeleteIcon } from "../components/icons";
import Hls from "hls.js";
import { useSite } from "../lib/useSite";
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
  DONE: { bg: "#e3f7ee", color: "#0b7a55", label: "Done" },
  FAILED: { bg: "#fdeceb", color: "#b3261e", label: "Failed" },
  QUEUED: { bg: "rgba(255,255,255,.2)", color: "#ffffff", label: "Queued" },
  PROCESSING: { bg: "rgba(255,255,255,.2)", color: "#ffffff", label: "Processing" },
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
        border: "1.5px dashed #b7cbe8",
        background: "#f9fbff",
        borderRadius: 16,
      }}
    >
      <div className="flex flex-wrap items-center justify-between gap-4 px-2 py-1">
        <div className="flex items-center gap-3">
          <span
            style={{
              width: 40,
              height: 40,
              borderRadius: 12,
              background: "#e6f2ff",
              color: "#0083ff",
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
            }}
          >
            <InboxIcon style={{ fontSize: 20 }} />
          </span>
          <div className="text-left">
            <div style={{ fontSize: 15, fontWeight: 600, color: "#0b1b3a" }}>
              Drop {noun} here, or browse
            </div>
            <div style={{ marginTop: 2, fontSize: 12, color: "#7b8aa5" }}>{hint}</div>
          </div>
        </div>
        <span
          style={{
            padding: "11px 20px",
            borderRadius: 12,
            background: "#0083ff",
            boxShadow: "0 6px 16px rgba(0,131,255,.28)",
            fontSize: 14,
            fontWeight: 600,
            color: "#ffffff",
          }}
        >
          Upload {noun.slice(0, -1)}
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
        hint="JPG, PNG, WebP or SVG. File type is verified by content."
        accept="image/*"
        onFile={handleUploadImage}
      />
      {imagesQuery.isLoading ? (
        <div className="py-16 text-center">
          <Spin />
        </div>
      ) : !imagesQuery.data?.length ? (
        <Empty description="No images yet for this site" />
      ) : (
        <div className="grid gap-4" style={{ gridTemplateColumns: "repeat(auto-fill, minmax(170px, 1fr))" }}>
          {imagesQuery.data.map((file: UploadFile) => (
            <div key={file.id} className="cms-tile cms-tile-card flex flex-col">
              <img
                src={mediaSrc(imageUrl(file))}
                alt={file.name}
                style={{ width: "100%", aspectRatio: "4/3", objectFit: "cover", display: "block", borderRadius: 0 }}
              />
              <span
                style={{
                  padding: "10px 14px 12px",
                  fontSize: 13,
                  fontWeight: 600,
                  color: "#0b1b3a",
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
        hint="MP4 or MOV. Transcoded to HLS after upload."
        accept="video/*"
        onFile={handleUploadVideo}
      />
      {videosQuery.isLoading ? (
        <div className="py-16 text-center">
          <Spin />
        </div>
      ) : !videosQuery.data?.length ? (
        <Empty description="No videos yet for this site" />
      ) : (
        <div className="grid gap-4" style={{ gridTemplateColumns: "repeat(auto-fill, minmax(240px, 1fr))" }}>
          {videosQuery.data.map((job: VideoJob) => {
            const badge = STATUS_STYLE[job.status];
            return (
              <div key={job.jobId} className="cms-tile cms-tile-card">
                {job.status === "DONE" ? (
                  <VideoThumb src={videoPlaybackUrl(job)} />
                ) : (
                  <div
                    style={{
                      position: "relative",
                      aspectRatio: "16/9",
                      background: "repeating-linear-gradient(135deg, #001c51 0 6px, #003f88 6px 12px)",
                    }}
                  >
                    <span
                      style={{
                        position: "absolute",
                        top: 10,
                        right: 10,
                        padding: "4px 10px",
                        borderRadius: 999,
                        fontSize: 12,
                        fontWeight: 600,
                        background: badge.bg,
                        color: badge.color,
                      }}
                    >
                      {job.status === "FAILED" ? job.errorMessage ?? badge.label : badge.label}
                    </span>
                  </div>
                )}
                <div className="flex items-center justify-between gap-4 px-3.5 py-3">
                  <span style={{ fontSize: 13, color: "#5b6b86" }}>
                    {new Date(job.createdAt).toLocaleDateString()}
                  </span>
                  <Popconfirm title="Delete this video?" onConfirm={() => deleteVideoMutation.mutate(job.videoId)}>
                    <Button
                      size="small"
                      danger
                      icon={<DeleteIcon />}
                      className="cms-mini-btn"
                    >
                      Delete
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
      <h1 className="cms-page-title">Media Library</h1>
      <p className="cms-page-sub" style={{ marginBottom: 20 }}>
        {(imagesQuery.data?.length ?? 0) + (videosQuery.data?.length ?? 0)} files for this site.
      </p>
      <Tabs
        activeKey={tab}
        onChange={setTab}
        items={[
          {
            key: "images",
            label: (
              <span className="flex items-center gap-2">
                Images
                <span style={{ fontFamily: "var(--font-mono)", fontSize: 11, color: "#7b8aa5" }}>
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
                <span style={{ fontFamily: "var(--font-mono)", fontSize: 11, color: "#7b8aa5" }}>
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
