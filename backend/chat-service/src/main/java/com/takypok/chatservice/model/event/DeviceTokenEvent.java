package com.takypok.chatservice.model.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * chat-service's own copy of employee-service's DeviceTokenEvent — each consumer deserializes into
 * its own local DTO (see kafka.yml), never the producer's class.
 */
@Getter
@Setter
@NoArgsConstructor
public class DeviceTokenEvent {
  private String eventType;
  private String sub;
  private String platform;
  private String fcmToken;
}
