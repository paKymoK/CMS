package com.takypok.chatservice.model.response;

import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AssistantSessionResponse {
  private String id;
  private String application;
  private String title;
  private ZonedDateTime createdAt;
  private ZonedDateTime updatedAt;
}
