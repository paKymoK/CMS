CREATE TABLE IF NOT EXISTS video_job
(
    job_id        character varying NOT NULL,
    video_id      character varying NOT NULL,
    status        character varying NOT NULL,
    error_message text,
    created_at    timestamp with time zone NOT NULL,
    started_at    timestamp with time zone,
    completed_at  timestamp with time zone,
    PRIMARY KEY (job_id)
);

CREATE INDEX IF NOT EXISTS idx_video_job_video_id ON video_job (video_id);
