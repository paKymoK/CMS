package com.takypok.chatservice.config;

import com.takypok.chatservice.model.event.EmployeeEvent;
import com.takypok.chatservice.repository.EmployeeDirectoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Upsert-only consumer feeding {@link com.takypok.chatservice.model.entity.EmployeeDirectory} — no
 * write path back to employee-service, this is purely a local read cache used to resolve display
 * data live instead of trusting the JWT's (frozen, optional) "info" claim.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class EmployeeDirectoryEventConsumer {
  private final EmployeeDirectoryRepository employeeDirectoryRepository;

  @KafkaListener(
      topics = "${employee-events.topic:employee.events}",
      groupId = "${spring.application.name}")
  public void onEmployeeEvent(EmployeeEvent event) {
    if (event == null || event.getSub() == null || event.getPayload() == null) {
      log.warn("Ignoring malformed employee event: {}", event);
      return;
    }
    EmployeeEvent.Payload payload = event.getPayload();
    employeeDirectoryRepository
        .upsert(
            event.getSub(),
            payload.getName(),
            payload.getEmail(),
            payload.getTitle(),
            payload.getDepartmentName(),
            payload.getAvatarUrl(),
            payload.getManagerSub())
        .doOnError(
            ex -> log.error("Failed to upsert employee directory for sub {}", event.getSub(), ex))
        .onErrorResume(ex -> Mono.empty())
        .subscribe();
  }
}
