import { useState } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { Upload, Button, Spin, Empty, message, Popconfirm, Tabs } from "antd";
import { UploadOutlined, DeleteOutlined } from "@ant-design/icons";
import Hls from "hls.js";
import { useEffect, useRef } from "react";
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
  return <video ref={ref} muted controls className="w-full h-32 object-cover rounded" />;
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
    if (!site) return false;
    try {
      await uploadImage(site, file);
      message.success("Image uploaded");
      queryClient.invalidateQueries({ queryKey: ["media-images", site] });
    } catch {
      message.error("Upload failed");
    }
    return false;
  };

  const handleUploadVideo = async (file: File) => {
    if (!site) return false;
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
    return false;
  };

  const imagesPanel = (
    <div>
      <Upload beforeUpload={handleUploadImage} showUploadList={false} accept="image/*">
        <Button icon={<UploadOutlined />} className="mb-4">
          Upload image
        </Button>
      </Upload>
      {imagesQuery.isLoading ? (
        <Spin />
      ) : !imagesQuery.data?.length ? (
        <Empty description="No images yet for this site" />
      ) : (
        <div className="grid grid-cols-6 gap-3">
          {imagesQuery.data.map((file: UploadFile) => (
            <div key={file.id} className="border border-gray-200 rounded overflow-hidden">
              <img src={imageUrl(file)} alt={file.name} className="w-full h-24 object-cover" />
              <div className="text-xs text-gray-500 truncate px-1 py-0.5">{file.name}</div>
            </div>
          ))}
        </div>
      )}
    </div>
  );

  const videosPanel = (
    <div>
      <Upload beforeUpload={handleUploadVideo} showUploadList={false} accept="video/*">
        <Button icon={<UploadOutlined />} className="mb-4">
          Upload video
        </Button>
      </Upload>
      {videosQuery.isLoading ? (
        <Spin />
      ) : !videosQuery.data?.length ? (
        <Empty description="No videos yet for this site" />
      ) : (
        <div className="grid grid-cols-4 gap-3">
          {videosQuery.data.map((job: VideoJob) => (
            <div key={job.jobId} className="border border-gray-200 rounded p-1">
              {job.status === "DONE" ? (
                <VideoThumb src={videoPlaybackUrl(job)} />
              ) : (
                <div className="h-32 flex items-center justify-center text-xs text-gray-500">
                  {job.status === "FAILED" ? "Transcode failed" : job.status}
                </div>
              )}
              <Popconfirm
                title="Delete this video?"
                onConfirm={() => deleteVideoMutation.mutate(job.videoId)}
              >
                <Button size="small" danger icon={<DeleteOutlined />} className="mt-1 w-full">
                  Delete
                </Button>
              </Popconfirm>
            </div>
          ))}
        </div>
      )}
    </div>
  );

  return (
    <div>
      <h2 className="text-lg font-semibold mb-4">Media Library</h2>
      <Tabs
        activeKey={tab}
        onChange={setTab}
        items={[
          { key: "images", label: "Images", children: imagesPanel },
          { key: "videos", label: "Videos", children: videosPanel },
        ]}
      />
    </div>
  );
}
