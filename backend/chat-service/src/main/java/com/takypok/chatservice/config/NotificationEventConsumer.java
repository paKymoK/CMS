package com.takypok.chatservice.config;

import com.takypok.chatservice.model.entity.DeviceTokenDirectory;
import com.takypok.chatservice.model.event.NotificationEvent;
import com.takypok.chatservice.repository.DeviceTokenDirectoryRepository;
import com.takypok.chatservice.service.FcmSenderService;
import com.takypok.chatservice.service.PresenceService;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Only recipients with no live chat WebSocket session get an actual FCM push — everyone else is
 * assumed to see the update live in-app, per {@link PresenceService#onlineSubsOf}. {@code
 * spring.json.value.default.type} is overridden per-listener since chat-service's global Kafka
 * consumer default type is EmployeeEvent (see kafka.yml).
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventConsumer {
  private final PresenceService presenceService;
  private final DeviceTokenDirectoryRepository deviceTokenDirectoryRepository;
  private final FcmSenderService fcmSenderService;

  @KafkaListener(
      topics = "${notification-events.topic:notification.events}",
      groupId = "${spring.application.name}",
      properties =
          "spring.json.value.default.type=com.takypok.chatservice.model.event.NotificationEvent")
  public void onNotificationEvent(NotificationEvent event) {
    if (event == null || event.getRecipientSubs() == null || event.getRecipientSubs().isEmpty()) {
      log.warn("Ignoring malformed notification event: {}", event);
      return;
    }
    Set<String> recipients = new HashSet<>(event.getRecipientSubs());
    presenceService
        .onlineSubsOf(recipients)
        .map(
            online -> {
              Set<String> offline = new HashSet<>(recipients);
              offline.removeAll(online);
              return offline;
            })
        .flatMapMany(deviceTokenDirectoryRepository::findBySubIn)
        .flatMap(token -> sendPush(token, event))
        .onErrorResume(
            ex -> {
              log.error("Failed to fan out notification event: {}", ex.getMessage(), ex);
              return Flux.empty();
            })
        .subscribe();
  }

  private Mono<Void> sendPush(DeviceTokenDirectory token, NotificationEvent event) {
    return fcmSenderService.send(
        token.getFcmToken(), event.getTitle(), event.getBody(), event.getData());
  }
}
