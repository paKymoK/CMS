package com.takypok.mediaservice.model;

public record JobResponse(String videoId, String jobId, JobStatus status, String message) {}
