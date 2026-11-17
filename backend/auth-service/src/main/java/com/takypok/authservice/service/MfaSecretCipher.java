package com.takypok.authservice.service;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Encrypts the authenticator secrets stored in {@code user_mfa} (AES-256-GCM). The account name is
 * bound in as associated data, so a ciphertext copied to another account does not decrypt. With no
 * key configured the feature is off ({@link #isConfigured()}); there is no built-in key.
 */
@Component
public class MfaSecretCipher {

  private static final int IV_BYTES = 12;
  private static final int TAG_BITS = 128;

  private final SecretKeySpec key;
  private final SecureRandom random = new SecureRandom();

  public MfaSecretCipher(@Value("${auth.mfa.encryption-key:}") String base64Key) {
    if (base64Key == null || base64Key.isBlank()) {
      this.key = null;
      return;
    }
    byte[] raw;
    try {
      raw = Base64.getDecoder().decode(base64Key.trim());
    } catch (IllegalArgumentException ex) {
      throw new IllegalStateException("AUTH_MFA_ENCRYPTION_KEY is not valid base64", ex);
    }
    if (raw.length != 32) {
      throw new IllegalStateException(
          "AUTH_MFA_ENCRYPTION_KEY must be 32 bytes (openssl rand -base64 32), got " + raw.length);
    }
    this.key = new SecretKeySpec(raw, "AES");
  }

  public boolean isConfigured() {
    return key != null;
  }

  public String encrypt(String sub, byte[] secret) {
    requireKey();
    try {
      byte[] iv = new byte[IV_BYTES];
      random.nextBytes(iv);
      Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
      cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(TAG_BITS, iv));
      cipher.updateAAD(sub.getBytes(StandardCharsets.UTF_8));
      byte[] encrypted = cipher.doFinal(secret);
      return Base64.getEncoder()
          .encodeToString(
              ByteBuffer.allocate(iv.length + encrypted.length).put(iv).put(encrypted).array());
    } catch (GeneralSecurityException ex) {
      throw new IllegalStateException("Could not encrypt the secret", ex);
    }
  }

  /**
   * @throws IllegalStateException when the value is not ours or the key changed
   */
  public byte[] decrypt(String sub, String stored) {
    requireKey();
    try {
      byte[] all = Base64.getDecoder().decode(stored);
      Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
      cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(TAG_BITS, all, 0, IV_BYTES));
      cipher.updateAAD(sub.getBytes(StandardCharsets.UTF_8));
      return cipher.doFinal(all, IV_BYTES, all.length - IV_BYTES);
    } catch (GeneralSecurityException | IllegalArgumentException ex) {
      throw new IllegalStateException("The stored secret cannot be read (wrong key?)", ex);
    }
  }

  private void requireKey() {
    if (key == null) {
      throw new IllegalStateException("Two-step verification is not set up (no encryption key)");
    }
  }
}
