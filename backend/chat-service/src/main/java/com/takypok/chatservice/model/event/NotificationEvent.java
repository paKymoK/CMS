package com.takypok.chatservice.model.event;

import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** chat-service's own copy of employee-service's NotificationEvent (see kafka.yml). */
@Getter
@Setter
@NoArgsConstructor
public class NotificationEvent {
  private String title;
  private String body;
  private List<String> recipientSubs;
  private Map<String, String> data;
}
