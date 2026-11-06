package com.takypok.authservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.takypok.authservice.model.entity.AccountTokenPurpose;
import java.time.Duration;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

class AccountTokenServiceTest {

  NamedParameterJdbcTemplate jdbc = mock(NamedParameterJdbcTemplate.class);
  AccountTokenService service = new AccountTokenService(jdbc);

  @Test
  void theDatabaseNeverSeesTheRawToken() {
    String raw = service.issue("ann", AccountTokenPurpose.ACTIVATE, Duration.ofHours(48));

    var params = ArgumentCaptor.forClass(SqlParameterSource.class);
    verify(jdbc, org.mockito.Mockito.times(2)).update(anyString(), params.capture());
    var insert = params.getAllValues().get(1);
    assertThat(insert.getValue("hash")).isEqualTo(AccountTokenService.hash(raw));
    assertThat(String.valueOf(insert.getValue("hash"))).hasSize(64).isNotEqualTo(raw);
    assertThat(raw).hasSizeGreaterThanOrEqualTo(40).matches("[A-Za-z0-9_-]+");
  }

  @Test
  void everyTokenIsDifferent() {
    var a = service.issue("ann", AccountTokenPurpose.RESET, Duration.ofHours(1));
    var b = service.issue("ann", AccountTokenPurpose.RESET, Duration.ofHours(1));
    assertThat(a).isNotEqualTo(b);
  }

  @Test
  @SuppressWarnings("unchecked")
  void aTokenIsLookedUpByItsHashAndPurpose() {
    when(jdbc.query(anyString(), any(SqlParameterSource.class), any(RowMapper.class)))
        .thenReturn(List.of("ann"));

    assertThat(service.consume("tok", AccountTokenPurpose.ACTIVATE)).contains("ann");

    var params = ArgumentCaptor.forClass(SqlParameterSource.class);
    verify(jdbc).query(anyString(), params.capture(), any(RowMapper.class));
    assertThat(params.getValue().getValue("hash")).isEqualTo(AccountTokenService.hash("tok"));
    assertThat(params.getValue().getValue("purpose")).isEqualTo("ACTIVATE");
  }

  @Test
  void blankOrUnknownTokensGiveNothing() {
    assertThat(service.consume(null, AccountTokenPurpose.RESET)).isEmpty();
    assertThat(service.consume(" ", AccountTokenPurpose.RESET)).isEmpty();
    assertThat(service.peek("", AccountTokenPurpose.RESET)).isEmpty();
  }
}
