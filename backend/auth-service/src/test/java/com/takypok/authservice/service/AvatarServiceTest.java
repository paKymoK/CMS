package com.takypok.authservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

class AvatarServiceTest {

  NamedParameterJdbcTemplate jdbc = mock(NamedParameterJdbcTemplate.class);
  AuditService audit = mock(AuditService.class);
  AvatarService service = new AvatarService(jdbc, audit);

  private static final byte[] PNG = {
    (byte) 0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A, 0, 0, 0, 0
  };
  private static final byte[] JPEG = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0, 0, 0};
  private static final byte[] WEBP = {'R', 'I', 'F', 'F', 1, 2, 3, 4, 'W', 'E', 'B', 'P'};

  @Test
  void recognisesPngJpegAndWebpByTheirFirstBytes() {
    assertThat(AvatarService.sniff(PNG)).isEqualTo("image/png");
    assertThat(AvatarService.sniff(JPEG)).isEqualTo("image/jpeg");
    assertThat(AvatarService.sniff(WEBP)).isEqualTo("image/webp");
  }

  @Test
  void refusesSvgGifHtmlAndTooShortFiles() {
    assertThat(AvatarService.sniff("<svg xmlns=\"http://www.w3.org/2000/svg\"/>".getBytes()))
        .isNull();
    assertThat(AvatarService.sniff("GIF89a......".getBytes())).isNull();
    assertThat(AvatarService.sniff("<html><script>alert(1)</script>".getBytes())).isNull();
    assertThat(AvatarService.sniff(new byte[] {(byte) 0xFF, (byte) 0xD8})).isNull();
  }

  @Test
  void aBadFileIsRefusedBeforeTheDatabaseIsTouched() {
    byte[] tooBig = new byte[AvatarService.MAX_BYTES + 1];
    System.arraycopy(PNG, 0, tooBig, 0, PNG.length);

    assertThatThrownBy(() -> service.save("john", tooBig))
        .isInstanceOf(IllegalArgumentException.class);
    assertThatThrownBy(() -> service.save("john", "not an image".getBytes()))
        .isInstanceOf(IllegalArgumentException.class);
    assertThatThrownBy(() -> service.save("john", new byte[0]))
        .isInstanceOf(IllegalArgumentException.class);
    verify(jdbc, never()).update(anyString(), any(SqlParameterSource.class));
  }

  @Test
  void aGoodPictureIsStoredAndAudited() {
    service.save("john", PNG);

    verify(jdbc)
        .update(
            org.mockito.ArgumentMatchers.contains("INSERT INTO user_avatar"),
            any(SqlParameterSource.class));
    verify(audit)
        .record(
            com.takypok.authservice.model.entity.AuditAction.PROFILE_UPDATED,
            "john",
            "fields: avatar");
  }
}
