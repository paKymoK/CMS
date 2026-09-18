package com.takypok.chatservice.config;

import com.takypok.core.exception.ApplicationException;
import com.takypok.core.model.Message;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * IP-based rate limiting for the public assistant endpoint, ANONYMOUS requests only — per the
 * decision log, authenticated/internal callers (testing, internal tools) are never throttled. Keyed
 * off the server-observed remote address, never a client-supplied header, same rule Phase 1 applied
 * to auth-service's login lockout.
 */
@Component
@RequiredArgsConstructor
public class AssistantRateLimiter {
  private static final String KEY_PREFIX = "assistant-rate:";
  private static final long MAX_REQUESTS_PER_WINDOW = 10;
  private static final Duration WINDOW = Duration.ofMinutes(1);

  private final ReactiveStringRedisTemplate redisTemplate;

  public Mono<Void> checkAnonymous(String remoteAddress) {
    String key = KEY_PREFIX + remoteAddress;
    return redisTemplate
        .opsForValue()
        .increment(key)
        .flatMap(
            count -> {
              Mono<Boolean> ensureExpiry =
                  count == 1 ? redisTemplate.expire(key, WINDOW) : Mono.just(true);
              if (count > MAX_REQUESTS_PER_WINDOW) {
                return ensureExpiry.then(
                    Mono.error(
                        new ApplicationException(
                            Message.Application.ERROR,
                            "Too many requests — please slow down and try again shortly")));
              }
              return ensureExpiry.then();
            });
  }
}
