package com.takypok.chatservice.model.assistant;

import com.takypok.chatservice.model.ImageRef;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** One turn in an assistant session's transcript — JSON-serialized into the Redis message list. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AssistantTurn {
  public enum Role {
    USER,
    ASSISTANT
  }

  private Role role;
  private String content;

  // Only ever populated on ASSISTANT turns — the source document filenames the RAG advisor
  // grounded the answer in.
  private List<String> sources;

  // Only ever populated on ASSISTANT turns — images referenced by the retrieved chunks.
  private List<ImageRef> images;

  private ZonedDateTime ts;
}
