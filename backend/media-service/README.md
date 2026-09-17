# Media Service

## FFmpeg Setup

FFmpeg is bundled as a Gradle dependency via [Bytedeco](https://github.com/bytedeco/javacpp-presets/tree/master/ffmpeg) — no manual installation required. The correct binary for the current OS is automatically selected at build time and extracted to a local cache on first startup.

### How it works

1. Gradle detects the build machine OS/arch and pulls only the matching platform JAR.
2. On application startup, `FfmpegResolver` extracts the binary from the classpath into `~/.cache/media-service/ffmpeg/`.
3. The binary is reused on subsequent startups — no re-extraction unless the cache is cleared.

### Default behavior

No configuration needed for local development. Just build and run — the binary for your machine is pulled automatically.

| Build machine | Binary pulled |
|---|---|
| Windows x64 | `windows-x86_64` |
| Mac Intel | `macosx-x86_64` |
| Mac M-series | `macosx-arm64` |
| Linux x64 | `linux-x86_64` |
| Linux ARM | `linux-arm64` |

### Overriding the platform

Use the `FFMPEG_PLATFORM` environment variable to force a specific platform. This is useful when the build machine differs from the deployment target (e.g. building on Mac CI but deploying to a Linux container).

Set it in your `.env` or CI environment before running the build:

```env
FFMPEG_PLATFORM=linux-x86_64
```

Available values:

| Value | Use case |
|---|---|
| `linux-x86_64` | Linux x64 server / Docker |
| `linux-arm64` | Linux ARM server (e.g. AWS Graviton) |
| `macosx-x86_64` | Mac Intel |
| `macosx-arm64` | Mac M-series |
| `windows-x86_64` | Windows |
| `all` | Pull all platforms (larger JAR, useful for shared build artifacts) |

When `FFMPEG_PLATFORM` is unset, auto-detection is used.

### Cache directory

Extracted binaries are stored at:

```
~/.cache/media-service/ffmpeg/ffmpeg        # Unix
~/.cache/media-service/ffmpeg/ffmpeg.exe    # Windows
```

To override the cache location, set `ffmpeg.cache-dir` in `application.yaml`:

```yaml
ffmpeg:
  cache-dir: /custom/path/ffmpeg
```

---

## Running on a Restricted Network (PKIX Error)

If the service throws a PKIX error on startup, set `JAVA_TOOL_OPTIONS` to make Java use the OS truststore.

**macOS**
```bash
JAVA_TOOL_OPTIONS="-Djavax.net.ssl.trustStoreType=KeychainStore"
```

**Windows**
```bash
JAVA_TOOL_OPTIONS="-Djavax.net.ssl.trustStoreType=Windows-ROOT"
```

**Linux**
```bash
JAVA_TOOL_OPTIONS="-Djavax.net.ssl.trustStore=/etc/ssl/certs/java/cacerts"
```

---

## Multipart Upload Memory Tuning (512MB video support)

### The problem

`UploadFileServiceImpl` and `VideoUploadService` already stream uploads to disk correctly (`DataBufferUtils.write`, incremental size checks via `UploadSizeLimiter`) — the app code never buffers a whole file. But that streaming only starts *after* Spring WebFlux's multipart parser has handed a `FilePart` to the app.

The parser itself decides, per part, whether to keep it fully in memory or spool it to a temp file first — controlled by `spring.webflux.multipart.max-in-memory-size`. Any part smaller than that threshold is buffered whole in memory before the app ever sees it.

Previously `max-in-memory-size` was `512MB` while `media.upload.max-video-size` was only `200MB` — every video, being under the threshold, was always fully memory-buffered by the parser regardless of how the app code streamed things afterward. N concurrent uploads meant N × (up to 200MB) of framework-level buffering, on top of JVM heap and ffmpeg transcode memory.

### The fix

Decouple RAM usage from file size instead of raising RAM to match file size:

| Property | Before | After | Why |
|---|---|---|---|
| `spring.codec.max-in-memory-size` | 512MB | 2MB | Only used for non-multipart body aggregation (JSON/form); never touches file parts. |
| `spring.webflux.multipart.max-in-memory-size` | 512MB | 1MB | Below any real image/video size, so every file part spills to a temp file as it streams in instead of being buffered whole. |
| `spring.webflux.multipart.max-disk-usage-per-part` | 200MB | 550MB | Raised to cover the new 512MB video cap with headroom, so the parser doesn't cut off a legitimate upload before `UploadSizeLimiter` gets to return its own friendlier error. |
| `media.upload.max-video-size` (`MEDIA_MAX_VIDEO_SIZE`) | 200MB | 512MB | The actual product requirement — now safe to raise since it no longer drives memory usage. |

With this change, concurrent upload memory cost is roughly flat regardless of file size or concurrency — bounded by JVM heap and ffmpeg transcode processes, not by `concurrent_uploads × video_size`. A modest VPS (e.g. 4 vCPU / 8GB RAM) comfortably supports several concurrent 512MB uploads, instead of needing 16GB+ to buffer them in memory.

### How to verify

1. Start the service and tail memory: `jcmd <pid> VM.native_memory summary` or watch RSS via `docker stats` / Task Manager.
2. Upload a video near 512MB (`multipart/form-data`, `video` field) and confirm:
   - A temp file appears under the OS temp dir (or `storage.raw-dir`) shortly after the request starts, growing incrementally — not a single write at the end.
   - Process RSS/heap does not jump by ~512MB during the upload.
3. Upload several 512MB videos concurrently (e.g. 4 in parallel with `curl` or a small k6/gatling script) and confirm memory stays roughly flat rather than scaling linearly with the number of concurrent uploads.
4. Confirm a >512MB video is still rejected with the app's own `"Video exceeds maximum allowed size of 512MB"` error (from `UploadSizeLimiter`), not a raw framework/connection-reset error — this checks `max-disk-usage-per-part` headroom is sufficient.
5. Confirm a normal JSON request still works — this checks `spring.codec.max-in-memory-size: 2MB` didn't break anything unrelated.
