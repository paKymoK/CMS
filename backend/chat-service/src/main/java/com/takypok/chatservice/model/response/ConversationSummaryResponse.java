package com.takypok.chatservice.model.response;

import com.takypok.chatservice.model.ConversationType;
import com.takypok.chatservice.model.entity.Message;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConversationSummaryResponse {
  private UUID id;
  private ConversationType type;
  private String name;
  private List<String> participantSubs;
  private Message lastMessage;
  private long unreadCount;

  // Read/delivery ticks are only unambiguous for a DIRECT conversation (a GROUP has no single
  // "the other participant") — null for GROUP, populated for DIRECT.
  private Long peerReadThroughMessageId;
  private Long peerDeliveredThroughMessageId;

  // GROUP only — null for DIRECT.
  private Boolean privateChannel;

  // Whether the caller is this conversation's OWNER — only an owner may invite/remove other
  // GROUP members (see ConversationServiceImpl#requireOwner). Meaningless for DIRECT.
  private boolean owner;
}
