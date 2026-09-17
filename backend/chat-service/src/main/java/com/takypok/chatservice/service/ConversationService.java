package com.takypok.chatservice.service;

import com.takypok.chatservice.model.entity.Conversation;
import com.takypok.chatservice.model.request.CreateConversationRequest;
import com.takypok.chatservice.model.response.ConversationListResponse;
import com.takypok.chatservice.model.response.PublicChannelResponse;
import com.takypok.core.model.authentication.User;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import reactor.core.publisher.Mono;

public interface ConversationService {

  Mono<Conversation> createConversation(CreateConversationRequest request, User caller);

  Mono<ConversationListResponse> listMyConversations(
      String callerSub, ZonedDateTime before, UUID beforeId, int size);

  Mono<Conversation> rename(UUID conversationId, String name, String callerSub);

  Mono<Void> addParticipants(UUID conversationId, List<String> participantSubs, String callerSub);

  Mono<Void> removeParticipant(UUID conversationId, String targetSub, String callerSub);

  Mono<Void> markRead(UUID conversationId, String callerSub);

  /** Ephemeral — fans out over WS only, nothing persisted. */
  Mono<Void> notifyTyping(UUID conversationId, User caller);

  /** Public GROUP conversations {@code callerSub} hasn't already joined. */
  Mono<List<PublicChannelResponse>> browsePublicChannels(String callerSub, String search);

  /** Errors with {@link IllegalStateException} if the channel doesn't exist or isn't public. */
  Mono<Void> joinChannel(UUID conversationId, String callerSub);

  /** Errors with {@link IllegalStateException} when {@code sub} is not in the conversation. */
  Mono<Void> assertParticipant(UUID conversationId, String sub);
}
