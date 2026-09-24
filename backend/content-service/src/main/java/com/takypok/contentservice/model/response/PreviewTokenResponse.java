package com.takypok.contentservice.model.response;

import com.takypok.contentservice.model.entity.PreviewToken;
import java.time.ZonedDateTime;

public record PreviewTokenResponse(String token, ZonedDateTime expiresAt) {
  public static PreviewTokenResponse from(PreviewToken previewToken) {
    return new PreviewTokenResponse(previewToken.getToken(), previewToken.getExpiresAt());
  }
}
