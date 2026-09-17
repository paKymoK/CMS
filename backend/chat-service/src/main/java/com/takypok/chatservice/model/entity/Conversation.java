package com.takypok.chatservice.model.entity;

import com.takypok.chatservice.model.ConversationType;
import com.takypok.core.model.BaseEntity;
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
@Table("conversation")
public class Conversation extends BaseEntity {
  @Id private UUID id;
  private ConversationType type;
  private String name; // meaningful for GROUP only, null for DIRECT

  // Denormalized so the conversation list can sort/paginate by recent activity without a
  // per-conversation join against message history on every list load.
  @Column("last_message_at")
  private ZonedDateTime lastMessageAt;

  @Column("last_message_id")
  private Long lastMessageId;

  // Only meaningful for GROUP — a public one (false) can be discovered and self-joined via
  // browsePublicChannels/joinChannel instead of needing an owner invite. DIRECT stays true
  // (unused, but the column is NOT NULL).
  @Column("is_private")
  private Boolean privateChannel;
}
