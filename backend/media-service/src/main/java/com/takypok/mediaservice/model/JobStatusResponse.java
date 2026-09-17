package com.takypok.mediaservice.model;

import java.time.Instant;

public record JobStatusResponse(
    String jobId,
    String videoId,
    JobStatus status,
    String errorMessage,
    Instant createdAt,
    Instant completedAt,
    String hlsUrl) {}
