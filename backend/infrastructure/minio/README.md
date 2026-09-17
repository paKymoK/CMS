# MinIO — Local Object Storage

S3-compatible object storage for local development, standing in for the object store
media-service's uploads (images, HLS video) will move to — local disk today.

## Start

```bash
docker-compose up -d
```

## Stop

```bash
docker-compose down
```

> Omit `-v` to keep the data volume between restarts. Add `-v` only if you want a clean slate.

---

## Connection

| Field | Value |
|-------|-------|
| S3 API endpoint | `http://localhost:9100` |
| Console | `http://localhost:9101` |
| Access key | `minioadmin` |
| Secret key | `minioadmin` |

The API port is remapped to `9100` on the host (container still listens on the standard
`9000` internally) because `auth-service` already owns `9000` locally.

---

## Buckets

Created automatically on first start by the `minio-init` one-shot container:

| Bucket | Intended for |
|--------|--------------|
| `media-images` | Image uploads (`UploadFileServiceImpl`) |
| `media-videos` | Raw uploads + HLS output (`VideoStorageService`, `HlsPackagerService`) |

Both are private by default — no public read policy is set. If media-service ends up
serving files by proxying through itself (as it does today for local disk), that's fine
as-is. If it instead serves directly from MinIO, that decision (presigned URLs vs. a
public bucket policy) needs to be made explicitly when the storage code is migrated.

---

## Status

Infrastructure only — `media-service` does not read/write to this yet. It still uses
local disk (`storage.base-dir` in `application.yaml`). Migrating `UploadFileServiceImpl`,
`VideoStorageService`, and `HlsPackagerService` to write here (and updating
`WebConfig`/`VideoStreamController` to serve from it) is a separate, larger change —
this just stands the container up so that work has somewhere to point at.

## Re-initializing

```bash
docker-compose down -v && docker-compose up -d
```
