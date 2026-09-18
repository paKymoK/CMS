package com.takypok.chatservice.model;

import com.takypok.chatservice.model.assistant.AssistantTurn;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * Stateless by design — the widget keeps its own conversation history (in memory or sessionStorage)
 * and resends it each call, rather than the server persisting a session keyed by an authenticated
 * user's sub. An anonymous site visitor has no sub to key a session on.
 */
@Getter
@Setter
public class PublicAskRequest {
  private String site;
  private String question;
  private List<AssistantTurn> history;
}
