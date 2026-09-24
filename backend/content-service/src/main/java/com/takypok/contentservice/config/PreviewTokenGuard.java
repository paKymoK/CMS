package com.takypok.contentservice.config;

import com.takypok.contentservice.model.entity.PreviewToken;
import com.takypok.contentservice.repository.PreviewTokenRepository;
import com.takypok.core.exception.ApplicationException;
import com.takypok.core.model.Message;
import java.time.ZonedDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * The single place every /v1/preview/** endpoint validates its token — mirrors CmsAdminGuard's role
 * for /v1/admin/**. siteId always comes from the resolved token itself, never a caller-supplied
 * parameter, so there's no way to present a valid token alongside a mismatched site — the token IS
 * the site scope.
 */
@Component
@RequiredArgsConstructor
public class PreviewTokenGuard {
  private final PreviewTokenRepository previewTokenRepository;

  public Mono<PreviewToken> requireValidToken(String token) {
    return previewTokenRepository
        .findByToken(token)
        .filter(t -> t.getExpiresAt().isAfter(ZonedDateTime.now()))
        .switchIfEmpty(
            Mono.error(
                new ApplicationException(
                    Message.Application.ERROR, "Invalid or expired preview token")));
  }
}
