import { mediaApi, serviceUrl } from "./api";

export interface UploadFile {
  id: string;
  name: string;
  extension: string;
  siteId: string;
}

export interface VideoJob {
  jobId: string;
  videoId: string;
  siteId: string;
  status: "QUEUED" | "PROCESSING" | "DONE" | "FAILED";
  errorMessage: string | null;
  createdAt: string;
  completedAt: string | null;
}

const MEDIA_BASE = serviceUrl("media-service");

export function imageUrl(file: UploadFile): string {
  return `${MEDIA_BASE}/images/${file.id}${file.extension}`;
}

export function videoPlaybackUrl(job: VideoJob): string {
  return `${MEDIA_BASE}/v1/videos/${job.videoId}/master.m3u8`;
}

export async function listImages(site: string): Promise<UploadFile[]> {
  const { data } = await mediaApi.get<UploadFile[]>("/v1/upload", { params: { site } });
  return data;
}

export async function listVideos(site: string): Promise<VideoJob[]> {
  const { data } = await mediaApi.get<VideoJob[]>("/v1/videos", { params: { site } });
  return data;
}

export async function uploadImage(site: string, file: File): Promise<UploadFile> {
  const form = new FormData();
  form.append("file", file);
  const { data } = await mediaApi.post<UploadFile>("/v1/upload/single", form, {
    params: { site },
    headers: { "Content-Type": "multipart/form-data" },
  });
  return data;
}

export async function uploadVideo(
  site: string,
  file: File,
): Promise<{ videoId: string; jobId: string; status: string; message: string }> {
  const form = new FormData();
  form.append("file", file);
  const { data } = await mediaApi.post("/v1/videos/upload", form, {
    params: { site },
    headers: { "Content-Type": "multipart/form-data" },
  });
  return data;
}
