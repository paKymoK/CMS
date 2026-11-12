import { mediaApi, serviceUrl } from "./api";

export interface UploadFile {
  id: string;
  name: string;
  extension: string;
  siteId: string;
  /** Library metadata an editor can set; all optional. altText is offered as the alt when the image is inserted in a post. */
  title: string | null;
  altText: string | null;
  description: string | null;
  sizeBytes: number | null;
}

/** What the library shows for an image: its title, or the file name when none was set. */
export function imageLabel(file: Pick<UploadFile, "title" | "name">): string {
  return file.title?.trim() || file.name;
}

/** A readable alt text from a file name, e.g. "team-photo_01.jpg" becomes "team photo 01". */
export function altFromFileName(name: string): string {
  return name
    .replace(/\.[^.]+$/, "")
    .replace(/[-_]+/g, " ")
    .replace(/\s+/g, " ")
    .trim();
}

/** The alt text offered for an image when it is inserted: the one saved in the library, else one made from the file name. */
export function defaultAlt(file: UploadFile): string {
  return file.altText?.trim() || altFromFileName(file.name);
}

const escapeAttr = (v: string) => v.replace(/&/g, "&amp;").replace(/"/g, "&quot;").replace(/</g, "&lt;");

/** The figure inserted in a post body for a picked image, its alt prefilled from the library. */
export function figureHtml(url: string, file?: UploadFile): string {
  const alt = file ? escapeAttr(defaultAlt(file)) : "";
  return `<figure><img src="${url}" alt="${alt}"><figcaption>Add a caption</figcaption></figure><p><br></p>`;
}

export function formatBytes(bytes: number | null): string {
  if (bytes == null) return "—";
  if (bytes < 1024) return `${bytes} B`;
  if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} KB`;
  return `${(bytes / (1024 * 1024)).toFixed(1)} MB`;
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

/** The stored form: an origin-less path (see lib/media.ts). Pass it through mediaSrc() to display. */
export function imageUrl(file: UploadFile): string {
  return `/media-service/images/${file.id}${file.extension}`;
}

/** The stored form of a video reference: origin-less, like imageUrl(). Resolve with mediaSrc(). */
export function videoStoredUrl(job: VideoJob): string {
  return `/media-service/v1/videos/${job.videoId}/master.m3u8`;
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

export async function updateImageMetadata(
  site: string,
  id: string,
  body: { title: string; altText: string; description: string },
): Promise<UploadFile> {
  const { data } = await mediaApi.put<UploadFile>(`/v1/upload/${id}`, body, { params: { site } });
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
