package com.takypok.authservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Base64;
import org.junit.jupiter.api.Test;

class MfaSecretCipherTest {

  private static final String KEY = Base64.getEncoder().encodeToString(new byte[32]);
  private static final String OTHER_KEY =
      Base64.getEncoder()
          .encodeToString(
              new byte[] {
                1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23,
                24, 25, 26, 27, 28, 29, 30, 31, 32
              });

  @Test
  void withNoKeyTheFeatureIsOff() {
    MfaSecretCipher cipher = new MfaSecretCipher("");
    assertThat(cipher.isConfigured()).isFalse();
    assertThatThrownBy(() -> cipher.encrypt("sam", new byte[20]))
        .isInstanceOf(IllegalStateException.class);
  }

  @Test
  void aWrongSizedKeyFailsAtStartupNotLater() {
    assertThatThrownBy(() -> new MfaSecretCipher(Base64.getEncoder().encodeToString(new byte[16])))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("32 bytes");
    assertThatThrownBy(() -> new MfaSecretCipher("not base64 !!"))
        .isInstanceOf(IllegalStateException.class);
  }

  @Test
  void roundTripsAndDoesNotStoreThePlainSecret() {
    MfaSecretCipher cipher = new MfaSecretCipher(KEY);
    byte[] secret = "12345678901234567890".getBytes();
    String stored = cipher.encrypt("sam", secret);

    assertThat(stored).doesNotContain("MTIzNDU2");
    assertThat(cipher.decrypt("sam", stored)).isEqualTo(secret);
    // A fresh IV each time: the same secret never encrypts to the same text.
    assertThat(cipher.encrypt("sam", secret)).isNotEqualTo(stored);
  }

  @Test
  void aValueCopiedToAnotherAccountOrReadWithAnotherKeyIsRefused() {
    MfaSecretCipher cipher = new MfaSecretCipher(KEY);
    String stored = cipher.encrypt("sam", new byte[20]);

    assertThatThrownBy(() -> cipher.decrypt("alex", stored))
        .isInstanceOf(IllegalStateException.class);
    assertThatThrownBy(() -> new MfaSecretCipher(OTHER_KEY).decrypt("sam", stored))
        .isInstanceOf(IllegalStateException.class);
  }
}
