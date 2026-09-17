package com.takypok.chatservice.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.takypok.chatservice.model.Attachment;
import com.takypok.chatservice.model.MessageType;
import com.takypok.core.model.IdEntity;
import com.takypok.core.model.authentication.User;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Uses a bigserial id (via {@link IdEntity}) rather than UUID so history can be cursor-paginated.
 */
@Getter
@Setter
@NoArgsConstructor
@Table("message")
public class Message extends IdEntity {
  @Column("conversation_id")
  private UUID conversationId;

  private User sender;
  private String content;

  @Column("message_type")
  private MessageType messageType = MessageType.TEXT;

  private List<Attachment> attachments;

  // Null for a top-level message; set for a thread reply. Not nested further — a reply's own
  // replies aren't supported, matching Slack's flat (non-nested) thread model.
  @Column("parent_message_id")
  private Long parentMessageId;

  // BaseEntity.createdAt is @JsonIgnore by default; clients need it to render timestamps.
  @Override
  @JsonProperty("createdAt")
  public ZonedDateTime getCreatedAt() {
    return super.getCreatedAt();
  }
}
