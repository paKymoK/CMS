package com.takypok.chatservice.repository;

import com.takypok.chatservice.model.entity.Message;
import java.util.Collection;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

public interface MessageRepository extends R2dbcRepository<Message, Long> {

  // Excludes thread replies (parent_message_id set) — those only ever surface via
  // findByConversationIdAndParentMessageIdOrderByIdAsc, inside an opened thread panel.
  Flux<Message> findByConversationIdAndParentMessageIdIsNullOrderByIdDesc(
      UUID conversationId, Pageable pageable);

  Flux<Message> findByConversationIdAndParentMessageIdIsNullAndIdLessThanOrderByIdDesc(
      UUID conversationId, Long beforeId, Pageable pageable);

  Flux<Message> findByConversationIdAndParentMessageIdOrderByIdAsc(
      UUID conversationId, Long parentMessageId);

  Flux<Message> findByIdIn(Collection<Long> ids);

  // `query` here is a pre-built tsquery expression (e.g. "verif:* & auto:*"), assembled in
  // MessageServiceImpl.buildPrefixQuery — not raw user text. to_tsquery still only ever
  // receives it as a bound function argument, never string-concatenated, so there's no
  // injection surface even though we're constructing the expression ourselves now.
  // Also excludes thread replies, same as the top-level list queries above.
  @Query(
      """
      SELECT * FROM message
      WHERE conversation_id = :conversationId
        AND parent_message_id IS NULL
        AND search_vector @@ to_tsquery('english', :query)
      ORDER BY id DESC
      LIMIT :limit
      """)
  Flux<Message> searchInConversation(UUID conversationId, String query, int limit);
}
