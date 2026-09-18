ALTER TABLE upload_file ADD COLUMN site_id character varying NOT NULL;
ALTER TABLE video_job ADD COLUMN site_id character varying NOT NULL;

CREATE INDEX IF NOT EXISTS idx_upload_file_site_id ON upload_file (site_id);
CREATE INDEX IF NOT EXISTS idx_video_job_site_id ON video_job (site_id);
