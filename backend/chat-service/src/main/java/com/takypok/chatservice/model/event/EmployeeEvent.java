package com.takypok.chatservice.model.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * Local, deliberately partial copy of employee-service's event contract published to {@code
 * employee.events} — only the fields chat-service's directory actually needs. See {@link
 * com.takypok.chatservice.config.EmployeeDirectoryEventConsumer}.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmployeeEvent {
  private String eventType;
  private String sub;
  private Payload payload;

  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Payload {
    private String name;
    private String email;
    private String avatarUrl;
    private String title;
    private String departmentName;
    private String managerSub;
  }
}
