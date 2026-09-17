package com.takypok.mediaservice.model.dto;

import java.time.Instant;

/**
 * @param name the on-disk filename ({@code <uuid><extension>}) — what the download URL is built
 *     from
 * @param originalName the filename as the uploader's client sent it, e.g. "report.xlsx" — what
 *     should be shown to a user
 */
public record ChunkedUploadedFile(
    String name, String originalName, long sizeBytes, Instant modifiedAt) {}
