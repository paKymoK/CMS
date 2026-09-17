package com.takypok.chatservice.repository;

import com.takypok.chatservice.model.entity.ConversationParticipant;
import java.util.Collection;
import java.util.UUID;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ConversationParticipantRepository
    extends R2dbcRepository<ConversationParticipant, UUID> {

  Flux<ConversationParticipant> findByConversationId(UUID conversationId);

  Flux<ConversationParticipant> findByConversationIdIn(Collection<UUID> conversationIds);

  Mono<ConversationParticipant> findByConversationIdAndParticipantSub(
      UUID conversationId, String participantSub);

  Flux<ConversationParticipant> findByParticipantSub(String participantSub);

  Mono<Void> deleteByConversationIdAndParticipantSub(UUID conversationId, String participantSub);

  Mono<Long> countByConversationId(UUID conversationId);

  // The WHERE guard (not just the GREATEST in SET) is what makes this a true no-op — including
  // returning 0 affected rows — when the watermark hasn't actually advanced. Every GET on the
  // message list calls this; without the guard it always reports 1 row affected and the caller
  // broadcasts a RECEIPT_UPDATED regardless, which round-trips back into another fetch on the
  // peer's side and repeats forever.
  @Query(
      """
      UPDATE conversation_participant
      SET delivered_through_message_id = :messageId
      WHERE conversation_id = :conversationId AND participant_sub = :sub
        AND (delivered_through_message_id IS NULL OR delivered_through_message_id < :messageId)
      """)
  Mono<Integer> bumpDeliveredThrough(UUID conversationId, String sub, Long messageId);
}
