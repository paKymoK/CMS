package com.takypok.authservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.takypok.authservice.model.entity.AuditAction;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

class MfaServiceTest {

  static final long NOW = 1111111109L;
  static final byte[] SECRET = "12345678901234567890".getBytes();
  static final String GOOD_CODE = "081804"; // RFC 6238 vector for NOW

  NamedParameterJdbcTemplate jdbc = mock(NamedParameterJdbcTemplate.class);
  StringRedisTemplate redis = mock(StringRedisTemplate.class);

  @SuppressWarnings("unchecked")
  ValueOperations<String, String> ops = mock(ValueOperations.class);

  AuditService audit = mock(AuditService.class);
  MfaSecretCipher cipher = new MfaSecretCipher(Base64.getEncoder().encodeToString(new byte[32]));
  MfaService service;

  @BeforeEach
  void setUp() {
    when(redis.opsForValue()).thenReturn(ops);
    service = new MfaService(jdbc, new TotpService(), cipher, redis, audit);
    service.clock = Clock.fixed(Instant.ofEpochSecond(NOW), ZoneOffset.UTC);
  }

  /** The stored row for "sam": turned on, last accepted step {@code lastUsedStep}. */
  @SuppressWarnings("unchecked")
  private void rowExists(boolean enabled, long lastUsedStep) {
    String enc = cipher.encrypt("sam", SECRET);
    when(jdbc.query(anyString(), any(SqlParameterSource.class), any(RowMapper.class)))
        .thenAnswer(
            inv -> {
              String sql = inv.getArgument(0);
              if (sql.contains("FROM user_mfa WHERE sub")) {
                try {
                  var rs = mock(java.sql.ResultSet.class);
                  when(rs.getString("secret_enc")).thenReturn(enc);
                  when(rs.getBoolean("enabled")).thenReturn(enabled);
                  when(rs.getLong("last_used_step")).thenReturn(lastUsedStep);
                  return List.of(((RowMapper<?>) inv.getArgument(2)).mapRow(rs, 0));
                } catch (java.sql.SQLException e) {
                  throw new IllegalStateException(e);
                }
              }
              return List.of();
            });
  }

  @Test
  void aRightCodeSignsInAndMarksItsStepUsed() {
    rowExists(true, 0);
    when(jdbc.update(
            ArgumentMatchers.contains("SET last_used_step"), any(SqlParameterSource.class)))
        .thenReturn(1);

    assertThat(service.verifyAtLogin("sam", GOOD_CODE, "10.0.0.1"))
        .isEqualTo(MfaService.Outcome.OK);
    verify(redis).delete("mfa-fail:sam");
    verify(audit, never())
        .recordQuietly(eq("sam"), eq(AuditAction.MFA_FAILED), any(), any(), any());
  }

  @Test
  void theSameCodeUsedTwiceIsRefusedTheSecondTime() {
    rowExists(true, TotpService.stepAt(NOW)); // that step was already accepted

    assertThat(service.verifyAtLogin("sam", GOOD_CODE, "10.0.0.1"))
        .isEqualTo(MfaService.Outcome.WRONG);
    verify(ops).increment("mfa-fail:sam");
  }

  @Test
  void aWrongCodeIsCountedAndAudited() {
    rowExists(true, 0);

    assertThat(service.verifyAtLogin("sam", "000000", "10.0.0.1"))
        .isEqualTo(MfaService.Outcome.WRONG);
    verify(ops).increment("mfa-fail:sam");
    verify(audit).recordQuietly("sam", AuditAction.MFA_FAILED, "sam", "sign-in", "10.0.0.1");
  }

  @Test
  void afterFiveWrongCodesEvenTheRightOneIsRefused() {
    when(ops.get("mfa-fail:sam")).thenReturn("5");
    rowExists(true, 0);

    assertThat(service.verifyAtLogin("sam", GOOD_CODE, "10.0.0.1"))
        .isEqualTo(MfaService.Outcome.LOCKED);
    verify(jdbc, never())
        .update(ArgumentMatchers.contains("last_used_step"), any(SqlParameterSource.class));
  }

  @Test
  void aRecoveryCodeWorksOnceAndIsAudited() {
    rowExists(true, 0);
    when(jdbc.update(
            ArgumentMatchers.contains("user_mfa_recovery SET used_at"),
            any(SqlParameterSource.class)))
        .thenReturn(1);

    assertThat(service.verifyAtLogin("sam", "abcde-fghjk", "10.0.0.1"))
        .isEqualTo(MfaService.Outcome.RECOVERY_USED);
    verify(audit).recordQuietly("sam", AuditAction.MFA_RECOVERY_USED, "sam", "sign-in", "10.0.0.1");
  }

  @Test
  void aSetupThatIsNotConfirmedDoesNotCountAsOn() {
    rowExists(false, 0);

    assertThat(service.verifyAtLogin("sam", GOOD_CODE, "10.0.0.1"))
        .isEqualTo(MfaService.Outcome.WRONG);
  }

  @Test
  void recoveryCodesIgnoreCaseDashesAndSpaces() {
    assertThat(MfaService.normalizeRecovery(" ABCDE-fghjk ")).isEqualTo("abcdefghjk");
    assertThat(MfaService.normalizeRecovery(null)).isEmpty();
  }

  @Test
  void withoutAnEncryptionKeyTheFeatureIsUnavailable() {
    MfaService off = new MfaService(jdbc, new TotpService(), new MfaSecretCipher(""), redis, audit);
    assertThat(off.isAvailable()).isFalse();
    assertThat(service.isAvailable()).isTrue();
  }
}
