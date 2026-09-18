import { useEffect, useRef, useState } from "react";
import { Modal, Tabs, Upload, Button, Spin, Empty, message } from "antd";
import { UploadOutlined } from "@ant-design/icons";
import { useQuery, useQueryClient } from "@tanstack/react-query";
import Hls from "hls.js";
import { useSite } from "../lib/useSite";
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

interface MediaPickerModalProps {
  open: boolean;
  onClose: () => void;
  onSelect: (url: string) => void;
  /** "image" (default) only offers the image library/upload; "both" also offers videos. */
  mode?: "image" | "both";
}

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

  return <video ref={ref} muted controls className="w-full h-28 object-cover rounded" />;
}

export default function MediaPickerModal({
  open,
  onClose,
  onSelect,
  mode = "image",
}: MediaPickerModalProps) {
  const { site } = useSite();
  const queryClient = useQueryClient();
  const [tab, setTab] = useState("library");

  const imagesQuery = useQuery({
    queryKey: ["media-images", site],
    queryFn: () => listImages(site!),
    enabled: open && !!site,
  });

  const videosQuery = useQuery({
    queryKey: ["media-videos", site],
    queryFn: () => listVideos(site!),
    enabled: open && !!site && mode === "both",
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
    return false; // prevent antd's default upload behavior — we upload manually above
  };

  const handleUploadVideo = async (file: File) => {
    if (!site) return false;
    try {
      await uploadVideo(site, file);
      message.success("Video queued for transcoding — it will appear once processing finishes");
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
      <Upload
        beforeUpload={handleUploadImage}
        showUploadList={false}
        accept="image/*"
        className="mb-4 block"
      >
        <Button icon={<UploadOutlined />}>Upload image</Button>
      </Upload>
      {imagesQuery.isLoading ? (
        <Spin />
      ) : !imagesQuery.data?.length ? (
        <Empty description="No images yet for this site" />
      ) : (
        <div className="grid grid-cols-4 gap-3 max-h-96 overflow-y-auto">
          {imagesQuery.data.map((file: UploadFile) => (
            <button
              key={file.id}
              onClick={() => onSelect(imageUrl(file))}
              className="border border-gray-200 rounded overflow-hidden hover:border-blue-400 cursor-pointer p-0"
              title={file.name}
            >
              <img
                src={imageUrl(file)}
                alt={file.name}
                className="w-full h-24 object-cover"
              />
            </button>
          ))}
        </div>
      )}
    </div>
  );

  const videosPanel = (
    <div>
      <Upload
        beforeUpload={handleUploadVideo}
        showUploadList={false}
        accept="video/*"
        className="mb-4 block"
      >
        <Button icon={<UploadOutlined />}>Upload video</Button>
      </Upload>
      {videosQuery.isLoading ? (
        <Spin />
      ) : !videosQuery.data?.length ? (
        <Empty description="No videos yet for this site" />
      ) : (
        <div className="grid grid-cols-3 gap-3 max-h-96 overflow-y-auto">
          {videosQuery.data.map((job: VideoJob) => (
            <div key={job.jobId} className="border border-gray-200 rounded p-1">
              {job.status === "DONE" ? (
                <button
                  onClick={() => onSelect(videoPlaybackUrl(job))}
                  className="block w-full p-0 border-none bg-transparent cursor-pointer"
                >
                  <VideoThumb src={videoPlaybackUrl(job)} />
                </button>
              ) : (
                <div className="h-28 flex items-center justify-center text-xs text-gray-500">
                  {job.status === "FAILED" ? "Transcode failed" : job.status}
                </div>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  );

  return (
    <Modal open={open} onCancel={onClose} footer={null} title="Media Library" width={720}>
      {mode === "both" ? (
        <Tabs
          activeKey={tab}
          onChange={setTab}
          items={[
            { key: "library", label: "Images", children: imagesPanel },
            { key: "videos", label: "Videos", children: videosPanel },
          ]}
        />
      ) : (
        imagesPanel
      )}
    </Modal>
  );
}
