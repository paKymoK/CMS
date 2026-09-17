package com.takypok.chatservice.repository;

import com.takypok.chatservice.model.entity.MessageReaction;
import java.util.Collection;
import java.util.UUID;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MessageReactionRepository extends R2dbcRepository<MessageReaction, UUID> {

  Flux<MessageReaction> findByMessageIdIn(Collection<Long> messageIds);

  Mono<Boolean> existsByMessageIdAndParticipantSubAndEmoji(
      Long messageId, String participantSub, String emoji);

  Mono<Void> deleteByMessageIdAndParticipantSubAndEmoji(
      Long messageId, String participantSub, String emoji);
}
