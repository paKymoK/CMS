package com.takypok.chatservice.model.entity;

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
@Table("message_reaction")
public class MessageReaction {
  @Id private UUID id;

  @Column("message_id")
  private Long messageId;

  @Column("participant_sub")
  private String participantSub;

  private String emoji;

  @Column("created_at")
  private ZonedDateTime createdAt;
}
