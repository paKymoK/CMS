package com.takypok.mediaservice.model.dto;

public record ChunkAckResponse(int index, long bytesReceived, long totalBytesReceivedSoFar) {}
