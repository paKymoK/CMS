package com.takypok.authservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.takypok.authservice.model.entity.AuditAction;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

class SessionServiceTest {

  NamedParameterJdbcTemplate jdbc = mock(NamedParameterJdbcTemplate.class);
  WebSessions web = mock(WebSessions.class);
  AuditService audit = mock(AuditService.class);
  StringRedisTemplate redis = mock(StringRedisTemplate.class);
  SessionService service;

  @BeforeEach
  void setUp() {
    service = new SessionService(jdbc, web, audit, redis, new ObjectMapper());
  }

  @Test
  @SuppressWarnings("unchecked")
  void endingADeviceEndsEveryAuthorizationOfItsBrowserAndTheBrowserSession() {
    when(jdbc.queryForList(anyString(), any(SqlParameterSource.class)))
        .thenReturn(
            List.of(
                Map.of(
                    "web_session_hash",
                    "H1",
                    "user_agent",
                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) Chrome/126.0 Safari/537.36",
                    "ip",
                    "10.0.0.5")));
    when(jdbc.queryForList(anyString(), any(SqlParameterSource.class), eq(String.class)))
        .thenReturn(List.of("auth-1", "auth-2"));

    service.end("ann", "auth-1");

    var params = ArgumentCaptor.forClass(SqlParameterSource.class);
    verify(jdbc)
        .update(
            org.mockito.ArgumentMatchers.contains("DELETE FROM oauth2_authorization"),
            params.capture());
    assertThat((List<String>) params.getValue().getValue("ids"))
        .containsExactly("auth-1", "auth-2");
    verify(web).endByHash("H1");
    verify(audit).record(AuditAction.SESSION_ENDED, "ann", "Chrome on macOS, 10.0.0.5");
  }

  @Test
  void anotherPersonsSessionCannotBeEnded() {
    when(jdbc.queryForList(anyString(), any(SqlParameterSource.class))).thenReturn(List.of());

    assertThatThrownBy(() -> service.end("ann", "someone-elses"))
        .isInstanceOf(IllegalArgumentException.class);
    verify(jdbc, never()).update(anyString(), any(SqlParameterSource.class));
    verify(web, never()).endByHash(any());
    verify(audit, never()).record(any(), any(), any());
  }

  @Test
  @SuppressWarnings("unchecked")
  void aDeviceWithNoBrowserSessionOnRecordEndsOnlyItself() {
    java.util.Map<String, Object> row = new java.util.HashMap<>();
    row.put("web_session_hash", null);
    row.put("user_agent", null);
    row.put("ip", "10.0.0.5");
    when(jdbc.queryForList(anyString(), any(SqlParameterSource.class))).thenReturn(List.of(row));

    service.end("ann", "auth-9");

    var params = ArgumentCaptor.forClass(SqlParameterSource.class);
    verify(jdbc).update(org.mockito.ArgumentMatchers.contains("DELETE"), params.capture());
    assertThat((List<String>) params.getValue().getValue("ids")).containsExactly("auth-9");
    verify(web).endByHash(null);
  }

  @Test
  void endingAllSparesTheKeptBrowserAndEndsTheRestEvenWithoutTokens() {
    when(jdbc.queryForList(anyString(), any(SqlParameterSource.class), eq(String.class)))
        .thenReturn(List.of("a1", "a2"));
    when(web.endAllOf("ann", "KEEP")).thenReturn(1);

    int ended = service.endAll("ann", "KEEP", "password changed");

    assertThat(ended).isEqualTo(3);
    verify(web).endAllOf("ann", "KEEP");
    verify(audit).recordQuietly(AuditAction.SESSION_ENDED, "ann", "password changed (2 devices)");
  }

  @Test
  void endingAllWhenThereIsNothingWritesNothing() {
    when(jdbc.queryForList(anyString(), any(SqlParameterSource.class), eq(String.class)))
        .thenReturn(List.of());
    when(web.endAllOf("ann", null)).thenReturn(0);

    assertThat(service.endAll("ann", null, "x")).isZero();
    verify(audit, never()).recordQuietly(any(), any(), any());
  }

  @Test
  void aFailureToRecordADeviceNeverBreaksTokenIssuing() {
    when(jdbc.update(anyString(), any(SqlParameterSource.class)))
        .thenThrow(new org.springframework.dao.DataAccessResourceFailureException("db down"));

    service.touch("auth-1", "ann"); // must not throw
  }

  @Test
  void anUnknownIdTokenOnLogoutMatchesNothing() {
    when(jdbc.queryForList(anyString(), any(SqlParameterSource.class))).thenReturn(List.of());

    assertThat(service.onOidcLogout("not-a-token", "10.0.0.1")).isNull();
    assertThat(service.onOidcLogout(null, "10.0.0.1")).isNull();
    verify(audit, never()).recordQuietly(any(), any(), any(), any(), any());
  }
}
