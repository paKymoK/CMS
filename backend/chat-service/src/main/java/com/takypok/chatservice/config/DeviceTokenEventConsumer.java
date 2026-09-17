package com.takypok.chatservice.config;

import com.takypok.chatservice.model.event.DeviceTokenEvent;
import com.takypok.chatservice.repository.DeviceTokenDirectoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Upsert-only consumer feeding {@link com.takypok.chatservice.model.entity.DeviceTokenDirectory} —
 * no write path back to employee-service, this is purely a local read cache {@link
 * com.takypok.chatservice.service.FcmSenderService} looks tokens up from. {@code
 * spring.json.value.default.type} is overridden per-listener since chat-service's global Kafka
 * consumer default type is EmployeeEvent (see kafka.yml), same pattern as employee-service's
 * DepartmentEventConsumer.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DeviceTokenEventConsumer {
  private final DeviceTokenDirectoryRepository deviceTokenDirectoryRepository;

  @KafkaListener(
      topics = "${device-token-events.topic:device-token.events}",
      groupId = "${spring.application.name}",
      properties =
          "spring.json.value.default.type=com.takypok.chatservice.model.event.DeviceTokenEvent")
  public void onDeviceTokenEvent(DeviceTokenEvent event) {
    if (event == null || event.getSub() == null || event.getFcmToken() == null) {
      log.warn("Ignoring malformed device token event: {}", event);
      return;
    }
    Mono<Void> result =
        "UNREGISTERED".equals(event.getEventType())
            ? deviceTokenDirectoryRepository.deleteBySubAndFcmToken(
                event.getSub(), event.getFcmToken())
            : deviceTokenDirectoryRepository.upsert(
                event.getFcmToken(), event.getSub(), event.getPlatform());
    result
        .doOnError(
            ex ->
                log.error(
                    "Failed to process device token event for sub {}: {}",
                    event.getSub(),
                    ex.getMessage(),
                    ex))
        .onErrorResume(ex -> Mono.empty())
        .subscribe();
  }
}
