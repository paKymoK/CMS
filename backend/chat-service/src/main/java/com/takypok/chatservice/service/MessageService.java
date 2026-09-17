package com.takypok.chatservice.service;

import com.takypok.chatservice.model.request.SendMessageRequest;
import com.takypok.chatservice.model.response.MessageResponse;
import com.takypok.core.model.authentication.User;
import java.util.List;
import java.util.UUID;
import reactor.core.publisher.Mono;

public interface MessageService {

  Mono<MessageResponse> sendMessage(UUID conversationId, SendMessageRequest request, User sender);

  Mono<List<MessageResponse>> listMessages(
      UUID conversationId, Long beforeId, int size, String callerSub);

  /** Full-history search within one conversation, backed by Postgres full-text search. */
  Mono<List<MessageResponse>> searchMessages(
      UUID conversationId, String query, int limit, String callerSub);

  /** Adds the reaction if callerSub hasn't used that emoji on this message yet, else removes it. */
  Mono<Void> toggleReaction(UUID conversationId, Long messageId, String emoji, String callerSub);

  /** Oldest-first replies to one top-level message. */
  Mono<List<MessageResponse>> fetchReplies(
      UUID conversationId, Long parentMessageId, String callerSub);
}
