package com.takypok.chatservice.service;

import java.util.Map;
import reactor.core.publisher.Mono;

public interface FcmSenderService {
  /** No-op (logged) if the Firebase Admin SDK hasn't been initialized — see FirebaseConfig. */
  Mono<Void> send(String fcmToken, String title, String body, Map<String, String> data);
}
