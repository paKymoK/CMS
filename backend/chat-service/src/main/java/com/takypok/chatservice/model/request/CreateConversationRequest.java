package com.takypok.chatservice.model.request;

import com.takypok.chatservice.model.ConversationType;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateConversationRequest {
  @NotNull private ConversationType type;

  private String name; // required for GROUP, ignored for DIRECT

  // Other participants, excluding the caller. Required (non-null) but may be empty for a
  // GROUP — a public channel in particular can be created solo and grown via joinChannel.
  @NotNull private List<String> participantSubs;

  // GROUP only: defaults to true (today's invite-only behavior) when omitted. false makes the
  // channel discoverable/self-joinable via browsePublicChannels + joinChannel.
  private Boolean privateChannel;
}
