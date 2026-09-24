package com.takypok.contentservice.service.impl;

import com.takypok.contentservice.model.entity.PreviewToken;
import com.takypok.contentservice.repository.PreviewTokenRepository;
import com.takypok.contentservice.service.PreviewTokenService;
import com.takypok.core.exception.ApplicationException;
import com.takypok.core.model.Message;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class PreviewTokenServiceImpl implements PreviewTokenService {
  private static final Duration TTL = Duration.ofMinutes(15);
  private static final SecureRandom RANDOM = new SecureRandom();

  private final PreviewTokenRepository previewTokenRepository;

  @Override
  public Mono<PreviewToken> mint(Long siteId) {
    return previewTokenRepository.save(newToken(siteId));
  }

  @Override
  public Mono<PreviewToken> refresh(String token) {
    return previewTokenRepository
        .findByToken(token)
        .filter(existing -> existing.getExpiresAt().isAfter(ZonedDateTime.now()))
        .switchIfEmpty(
            Mono.error(
                new ApplicationException(
                    Message.Application.ERROR, "Invalid or expired preview token")))
        .flatMap(
            existing ->
                previewTokenRepository
                    .deleteByToken(existing.getToken())
                    .then(previewTokenRepository.save(newToken(existing.getSiteId()))));
  }

  private PreviewToken newToken(Long siteId) {
    PreviewToken token = new PreviewToken();
    token.setToken(generateToken());
    token.setSiteId(siteId);
    ZonedDateTime now = ZonedDateTime.now();
    token.setMintedAt(now);
    token.setExpiresAt(now.plus(TTL));
    return token;
  }

  private static String generateToken() {
    byte[] bytes = new byte[32];
    RANDOM.nextBytes(bytes);
    return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
  }
}
