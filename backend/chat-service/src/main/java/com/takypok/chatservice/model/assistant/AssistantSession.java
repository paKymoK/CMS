package com.takypok.chatservice.model.assistant;

import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * An AI assistant chat thread, stored entirely in Redis — separate from the human {@code
 * Conversation}/{@code Message} entities backing the real-time chat system.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AssistantSession {
  private String id;
  private String ownerSub;
  private String title;
  private ZonedDateTime createdAt;
  private ZonedDateTime updatedAt;
}
