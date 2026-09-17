package com.takypok.mediaservice.model.dto;

public record StartChunkedUploadResponse(
    String sessionId, long chunkSizeBytes, long sessionIdleTimeoutSeconds) {}
