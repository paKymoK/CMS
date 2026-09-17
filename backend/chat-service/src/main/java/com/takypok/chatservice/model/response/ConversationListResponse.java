package com.takypok.chatservice.model.response;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ConversationListResponse {
  private List<ConversationSummaryResponse> items;
  private boolean hasMore;
  private ZonedDateTime nextBefore;
  private UUID nextBeforeId;
}
