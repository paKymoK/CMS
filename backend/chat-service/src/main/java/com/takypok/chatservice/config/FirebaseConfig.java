package com.takypok.chatservice.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import java.io.FileInputStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Initializes the Firebase Admin SDK from a service-account credential file, following the same
 * "env var, no default, required in real deployments" convention as auth-service's LDAP secrets
 * (see auth-service/docker-compose.yml). Until a real credential is provided, FCM sending is a
 * deliberate no-op (logged, not thrown) rather than failing service startup — see {@link
 * com.takypok.chatservice.service.FcmSenderService}.
 *
 * <p>PLACEHOLDER: set FIREBASE_CREDENTIALS_PATH to the absolute path of a Firebase service-account
 * JSON key file (Firebase console → Project settings → Service accounts → Generate new private key)
 * before deploying. Nothing is configured here by default.
 */
@Component
@Slf4j
public class FirebaseConfig {
  @Value("${FIREBASE_CREDENTIALS_PATH:}")
  private String credentialsPath;

  @PostConstruct
  public void init() {
    if (!StringUtils.hasText(credentialsPath)) {
      log.warn(
          "FIREBASE_CREDENTIALS_PATH is not set — FCM push sending is disabled until a real "
              + "Firebase service-account credential is configured.");
      return;
    }
    try (FileInputStream serviceAccount = new FileInputStream(credentialsPath)) {
      if (FirebaseApp.getApps().isEmpty()) {
        FirebaseOptions options =
            FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();
        FirebaseApp.initializeApp(options);
        log.info("Firebase Admin SDK initialized from {}", credentialsPath);
      }
    } catch (Exception e) {
      log.error(
          "Failed to initialize Firebase Admin SDK from {} — FCM push sending is disabled: {}",
          credentialsPath,
          e.getMessage(),
          e);
    }
  }
}
