package com.takypok.chatservice.model.entity;

import java.time.ZonedDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@NoArgsConstructor
@Table("user_presence")
public class UserPresence {
  @Id private String sub;

  @Column("last_seen_at")
  private ZonedDateTime lastSeenAt;
}
