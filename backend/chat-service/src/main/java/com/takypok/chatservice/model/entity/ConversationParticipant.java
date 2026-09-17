package com.takypok.chatservice.model.entity;

import com.takypok.chatservice.model.ParticipantRole;
import java.time.ZonedDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@NoArgsConstructor
@Table("conversation_participant")
public class ConversationParticipant {
  @Id private UUID id;

  @Column("conversation_id")
  private UUID conversationId;

  @Column("participant_sub")
  private String participantSub;

  private ParticipantRole role;

  @Column("joined_at")
  private ZonedDateTime joinedAt;

  @Column("last_read_at")
  private ZonedDateTime lastReadAt;

  // The unread count compares message.id against this instead of created_at against last_read_at,
  // so it can reuse the existing (conversation_id, id) index instead of needing a new one — ids
  // are monotonic within a conversation, timestamps across instances aren't guaranteed to be.
  @Column("last_read_message_id")
  private Long lastReadMessageId;

  // Advances on send (if this participant is online per PresenceService) or on their next
  // REST message-list fetch (reconnect catch-up) — whichever happens first.
  @Column("delivered_through_message_id")
  private Long deliveredThroughMessageId;
}
