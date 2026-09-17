package com.takypok.authservice.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.takypok.authservice.model.entity.ClientSessionPolicy;
import com.takypok.authservice.repository.ClientSessionPolicyRepository;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;

@Slf4j
@RequiredArgsConstructor
public class SingleTabOAuth2AuthorizationService implements OAuth2AuthorizationService {

  static final String REVOKED_KEY_PREFIX = "revoked:jti:";

  private static final String QUERY_ACTIVE_TOKENS =
      "SELECT access_token_value, access_token_expires_at"
          + " FROM oauth2_authorization"
          + " WHERE registered_client_id = ? AND principal_name = ?"
          + "   AND access_token_value IS NOT NULL";

  private static final String DELETE_BY_CLIENT_PRINCIPAL =
      "DELETE FROM oauth2_authorization WHERE registered_client_id = ? AND principal_name = ?";

  private final JdbcOAuth2AuthorizationService delegate;
  private final ClientSessionPolicyRepository policyRepository;
  private final JdbcTemplate jdbcTemplate;
  private final StringRedisTemplate redisTemplate;
  private final ObjectMapper objectMapper;

  @Override
  public void save(OAuth2Authorization authorization) {
    boolean isNew = delegate.findById(authorization.getId()) == null;
    if (isNew) {
      policyRepository
          .findById(authorization.getRegisteredClientId())
          .filter(ClientSessionPolicy::isSingleTab)
          .ifPresent(
              policy -> {
                String lockKey =
                    "login-lock:"
                        + authorization.getRegisteredClientId()
                        + ":"
                        + authorization.getPrincipalName();
                Boolean acquired;
                try {
                  acquired =
                      redisTemplate.opsForValue().setIfAbsent(lockKey, "1", Duration.ofSeconds(5));
                } catch (Exception e) {
                  if (policy.isFailOpen()) {
                    log.warn(
                        "Redis unavailable — lock skipped, degrading to refresh-token-only"
                            + " revocation for principal={}: {}",
                        authorization.getPrincipalName(),
                        e.getMessage());
                    acquired = null;
                  } else {
                    throw e;
                  }
                }
                if (Boolean.TRUE.equals(acquired)) {
                  try {
                    blacklistAndCleanup(authorization, policy);
                  } finally {
                    redisTemplate.delete(lockKey);
                  }
                } else {
                  log.debug(
                      "Login lock already held for client={} principal={}, skipping cleanup",
                      authorization.getRegisteredClientId(),
                      authorization.getPrincipalName());
                }
              });
    }
    delegate.save(authorization);
  }

  private void blacklistAndCleanup(OAuth2Authorization authorization, ClientSessionPolicy policy) {
    try {
      blacklistExistingTokens(
          authorization.getRegisteredClientId(), authorization.getPrincipalName());
    } catch (Exception e) {
      if (policy.isFailOpen()) {
        log.warn(
            "Redis unavailable — blacklist skipped, degrading to refresh-token-only"
                + " revocation for principal={}: {}",
            authorization.getPrincipalName(),
            e.getMessage());
      } else {
        throw e;
      }
    }
    jdbcTemplate.update(
        DELETE_BY_CLIENT_PRINCIPAL,
        authorization.getRegisteredClientId(),
        authorization.getPrincipalName());
  }

  @Override
  public void remove(OAuth2Authorization authorization) {
    delegate.remove(authorization);
  }

  @Override
  public OAuth2Authorization findById(String id) {
    return delegate.findById(id);
  }

  @Override
  public OAuth2Authorization findByToken(String token, OAuth2TokenType tokenType) {
    return delegate.findByToken(token, tokenType);
  }

  private void blacklistExistingTokens(String registeredClientId, String principalName) {
    List<Map<String, Object>> rows =
        jdbcTemplate.queryForList(QUERY_ACTIVE_TOKENS, registeredClientId, principalName);

    Instant now = Instant.now();
    for (Map<String, Object> row : rows) {
      String tokenValue = (String) row.get("access_token_value");
      Timestamp expiresAt = (Timestamp) row.get("access_token_expires_at");
      if (tokenValue == null || expiresAt == null) continue;

      String jti = extractJti(tokenValue);
      if (jti == null) continue;

      long remainingMs = expiresAt.toInstant().toEpochMilli() - now.toEpochMilli();
      if (remainingMs > 0) {
        redisTemplate
            .opsForValue()
            .set(REVOKED_KEY_PREFIX + jti, "1", Duration.ofMillis(remainingMs));
        log.debug("Blacklisted token jti={} for principal={}", jti, principalName);
      }
    }
  }

  private String extractJti(String jwtValue) {
    try {
      String[] parts = jwtValue.split("\\.");
      if (parts.length < 2) return null;
      String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
      JsonNode node = objectMapper.readTree(payload);
      JsonNode jtiNode = node.get("jti");
      return jtiNode != null ? jtiNode.asText() : null;
    } catch (Exception e) {
      log.warn("Failed to extract jti from token: {}", e.getMessage());
      return null;
    }
  }
}
