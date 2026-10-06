-- media-service schema. Every table is site-scoped from its first migration (multi-tenant rule):
-- site_id holds the site code ('en', 'vi', ...) — media-service doesn't own the site table, which
-- lives in content-service's database, so this is the code itself rather than a foreign key.

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Uploaded images. The id doubles as the stored file name (<id><extension> under
-- storage.images-dir) and as the public URL segment: /media-service/images/<id><extension>.
CREATE TABLE IF NOT EXISTS upload_file
(
    id          uuid              NOT NULL DEFAULT uuid_generate_v4(),
    name        character varying NOT NULL, -- original file name, including its extension
    extension   character varying NOT NULL, -- with the leading dot, e.g. '.png'
    site_id     character varying NOT NULL,
    created_at  timestamp with time zone,
    created_by  jsonb,
    modified_at timestamp with time zone,
    modified_by jsonb,
    PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS idx_upload_file_site_id ON upload_file (site_id);

-- HLS video transcode jobs.
CREATE TABLE IF NOT EXISTS video_job
(
    job_id        character varying        NOT NULL,
    video_id      character varying        NOT NULL,
    status        character varying        NOT NULL,
    error_message text,
    site_id       character varying        NOT NULL,
    created_at    timestamp with time zone NOT NULL,
    started_at    timestamp with time zone,
    completed_at  timestamp with time zone,
    PRIMARY KEY (job_id)
);

CREATE INDEX IF NOT EXISTS idx_video_job_video_id ON video_job (video_id);
CREATE INDEX IF NOT EXISTS idx_video_job_site_id ON video_job (site_id);
