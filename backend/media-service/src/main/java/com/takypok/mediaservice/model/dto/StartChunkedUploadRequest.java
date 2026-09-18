package com.takypok.mediaservice.model.dto;

public record StartChunkedUploadRequest(
    String sessionId, String filename, Long totalSizeBytes, String site) {}
