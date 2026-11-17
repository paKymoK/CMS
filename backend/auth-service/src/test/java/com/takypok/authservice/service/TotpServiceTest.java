package com.takypok.authservice.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class TotpServiceTest {

  // RFC 6238 appendix B, SHA-1, secret "12345678901234567890". The RFC lists 8-digit codes; ours
  // are
  // the last 6 digits of those.
  private static final byte[] RFC_SECRET =
      "12345678901234567890".getBytes(StandardCharsets.US_ASCII);

  TotpService totp = new TotpService();

  @Test
  void matchesTheRfc6238TestVectors() {
    assertThat(totp.code(RFC_SECRET, TotpService.stepAt(59))).isEqualTo("287082");
    assertThat(totp.code(RFC_SECRET, TotpService.stepAt(1111111109L))).isEqualTo("081804");
    assertThat(totp.code(RFC_SECRET, TotpService.stepAt(1111111111L))).isEqualTo("050471");
    assertThat(totp.code(RFC_SECRET, TotpService.stepAt(1234567890L))).isEqualTo("005924");
    assertThat(totp.code(RFC_SECRET, TotpService.stepAt(2000000000L))).isEqualTo("279037");
  }

  @Test
  void acceptsTheCurrentCodeAndOneStepOfClockDrift() {
    long now = 1111111109L;
    long step = TotpService.stepAt(now);
    assertThat(totp.verify(RFC_SECRET, "081804", now, 0)).isEqualTo(step);
    assertThat(totp.verify(RFC_SECRET, totp.code(RFC_SECRET, step - 1), now, 0))
        .isEqualTo(step - 1);
    assertThat(totp.verify(RFC_SECRET, totp.code(RFC_SECRET, step + 1), now, 0))
        .isEqualTo(step + 1);
  }

  @Test
  void refusesACodeFurtherOffThanTheDriftWindow() {
    long now = 1111111109L;
    long step = TotpService.stepAt(now);
    assertThat(totp.verify(RFC_SECRET, totp.code(RFC_SECRET, step - 2), now, 0)).isEqualTo(-1);
    assertThat(totp.verify(RFC_SECRET, totp.code(RFC_SECRET, step + 2), now, 0)).isEqualTo(-1);
  }

  @Test
  void aStepAlreadyUsedCannotBeUsedAgain() {
    long now = 1111111109L;
    long step = TotpService.stepAt(now);
    assertThat(totp.verify(RFC_SECRET, "081804", now, step)).isEqualTo(-1);
    assertThat(totp.verify(RFC_SECRET, "081804", now, step - 1)).isEqualTo(step);
  }

  @Test
  void refusesInputThatIsNotSixDigits() {
    assertThat(totp.verify(RFC_SECRET, null, 59, 0)).isEqualTo(-1);
    assertThat(totp.verify(RFC_SECRET, "", 59, 0)).isEqualTo(-1);
    assertThat(totp.verify(RFC_SECRET, "28708", 59, 0)).isEqualTo(-1);
    assertThat(totp.verify(RFC_SECRET, "2870820", 59, 0)).isEqualTo(-1);
    assertThat(totp.verify(RFC_SECRET, "28708a", 59, 0)).isEqualTo(-1);
    // A space in the middle is how the app may show it.
    assertThat(totp.verify(RFC_SECRET, "287 082", 59, 0)).isEqualTo(TotpService.stepAt(59));
  }

  @Test
  void base32IsRfc4648() {
    assertThat(TotpService.base32("foobar".getBytes(StandardCharsets.US_ASCII)))
        .isEqualTo("MZXW6YTBOI");
    assertThat(TotpService.base32(RFC_SECRET)).isEqualTo("GEZDGNBVGY3TQOJQGEZDGNBVGY3TQOJQ");
  }

  @Test
  void theQrLinkCarriesWhatAuthenticatorAppsNeed() {
    String uri = totp.otpauthUri("CMC Global CMS", "ndtu24", RFC_SECRET);
    assertThat(uri)
        .isEqualTo(
            "otpauth://totp/CMC%20Global%20CMS:ndtu24?secret=GEZDGNBVGY3TQOJQGEZDGNBVGY3TQOJQ"
                + "&issuer=CMC%20Global%20CMS&algorithm=SHA1&digits=6&period=30");
  }

  @Test
  void newSecretsAre160BitsAndDiffer() {
    byte[] a = totp.newSecret();
    byte[] b = totp.newSecret();
    assertThat(a).hasSize(20);
    assertThat(a).isNotEqualTo(b);
  }
}
