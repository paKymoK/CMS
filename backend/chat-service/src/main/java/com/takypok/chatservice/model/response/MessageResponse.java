package com.takypok.chatservice.model.response;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.takypok.chatservice.model.entity.Message;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

// @JsonUnwrapped keeps the wire shape flat (id/content/... alongside reactions/replyCount)
// instead of nesting the message under a "message" key, so the frontend's existing ChatMessage
// shape only needs new fields added, not a restructure.
@Getter
@AllArgsConstructor
public class MessageResponse {
  @JsonUnwrapped private Message message;
  private List<ReactionSummary> reactions;

  // Always 0 for a reply itself (no nested threads) and for a just-sent message.
  private long replyCount;
}
