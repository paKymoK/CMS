package com.takypok.chatservice.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * References an asset already uploaded to media-service (images via {@code /v1/upload/single},
 * videos via {@code /v1/videos/upload}). Chat never stores or proxies the file bytes itself.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Attachment {
  @NotNull private AttachmentType type;

  @NotBlank private String mediaAssetId; // UploadFile.id for images, JobResponse.videoId for videos

  private String url; // e.g. /media-service/images/{id}{ext}, null until READY for videos
  private AttachmentStatus status = AttachmentStatus.READY;
}
