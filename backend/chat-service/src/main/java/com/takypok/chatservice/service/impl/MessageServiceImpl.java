package com.takypok.chatservice.service.impl;

import com.takypok.chatservice.model.entity.ConversationParticipant;
import com.takypok.chatservice.model.entity.Message;
import com.takypok.chatservice.model.entity.MessageReaction;
import com.takypok.chatservice.model.event.ChatEvent;
import com.takypok.chatservice.model.request.SendMessageRequest;
import com.takypok.chatservice.model.response.MessageResponse;
import com.takypok.chatservice.model.response.ReactionSummary;
import com.takypok.chatservice.repository.ConversationParticipantRepository;
import com.takypok.chatservice.repository.ConversationRepository;
import com.takypok.chatservice.repository.MessageReactionRepository;
import com.takypok.chatservice.repository.MessageRepository;
import com.takypok.chatservice.service.ChatSessionRegistry;
import com.takypok.chatservice.service.ConversationService;
import com.takypok.chatservice.service.MessageService;
import com.takypok.chatservice.service.PresenceService;
import com.takypok.core.model.authentication.User;
import java.time.ZonedDateTime;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageServiceImpl implements MessageService {
  private static final int MAX_PAGE_SIZE = 100;
  private static final Pattern NON_WORD = Pattern.compile("[^\\p{L}\\p{N}]+");

  private static final String REPLY_COUNT_SQL =
      """
      SELECT parent_message_id, COUNT(*) AS reply_count
      FROM message
      WHERE parent_message_id = ANY(:parentIds)
      GROUP BY parent_message_id
      """;

  private final MessageRepository messageRepository;
  private final MessageReactionRepository reactionRepository;
  private final ConversationRepository conversationRepository;
  private final ConversationParticipantRepository participantRepository;
  private final ConversationService conversationService;
  private final ChatSessionRegistry sessionRegistry;
  private final PresenceService presenceService;
  private final DatabaseClient databaseClient;

  @Override
  public Mono<MessageResponse> sendMessage(
      UUID conversationId, SendMessageRequest request, User sender) {
    return conversationService
        .assertParticipant(conversationId, sender.getSub())
        .then(
            Mono.defer(
                () -> {
                  Message message = new Message();
                  message.setConversationId(conversationId);
                  message.setSender(sender);
                  message.setContent(request.getContent());
                  message.setMessageType(request.getMessageType());
                  message.setAttachments(request.getAttachments());
                  message.setParentMessageId(request.getParentMessageId());
                  return messageRepository.save(message);
                }))
        .flatMap(
            message ->
                conversationRepository
                    .updateLastMessage(conversationId, message.getId(), message.getCreatedAt())
                    .thenReturn(message))
        .map(message -> new MessageResponse(message, List.of(), 0L))
        .doOnSuccess(
            response -> {
              broadcastMessage(response);
              markDeliveredForOnlineRecipients(
                  conversationId, sender.getSub(), response.getMessage().getId());
            });
  }

  @Override
  public Mono<List<MessageResponse>> listMessages(
      UUID conversationId, Long beforeId, int size, String callerSub) {
    int pageSize = Math.min(size <= 0 ? 50 : size, MAX_PAGE_SIZE);
    PageRequest pageRequest = PageRequest.of(0, pageSize);

    return conversationService
        .assertParticipant(conversationId, callerSub)
        .then(
            Mono.defer(
                () ->
                    (beforeId == null
                            ? messageRepository
                                .findByConversationIdAndParentMessageIdIsNullOrderByIdDesc(
                                    conversationId, pageRequest)
                            : messageRepository
                                .findByConversationIdAndParentMessageIdIsNullAndIdLessThanOrderByIdDesc(
                                    conversationId, beforeId, pageRequest))
                        .collectList()))
        .flatMap(this::enrichMessages)
        .doOnSuccess(messages -> markDeliveredOnFetch(conversationId, callerSub));
  }

  @Override
  public Mono<List<MessageResponse>> searchMessages(
      UUID conversationId, String query, int limit, String callerSub) {
    String tsQuery = buildPrefixQuery(query);
    if (tsQuery.isBlank()) {
      return Mono.just(List.of());
    }
    int cappedLimit = Math.min(limit <= 0 ? 50 : limit, MAX_PAGE_SIZE);

    return conversationService
        .assertParticipant(conversationId, callerSub)
        .then(
            Mono.defer(
                () ->
                    messageRepository
                        .searchInConversation(conversationId, tsQuery, cappedLimit)
                        .collectList()))
        .flatMap(this::enrichMessages);
  }

  @Override
  public Mono<Void> toggleReaction(
      UUID conversationId, Long messageId, String emoji, String callerSub) {
    return conversationService
        .assertParticipant(conversationId, callerSub)
        .then(
            reactionRepository.existsByMessageIdAndParticipantSubAndEmoji(
                messageId, callerSub, emoji))
        .flatMap(
            exists ->
                exists
                    ? reactionRepository.deleteByMessageIdAndParticipantSubAndEmoji(
                        messageId, callerSub, emoji)
                    : reactionRepository.save(newReaction(messageId, callerSub, emoji)).then())
        .then(Mono.defer(() -> broadcastReactionUpdate(conversationId, messageId)));
  }

  @Override
  public Mono<List<MessageResponse>> fetchReplies(
      UUID conversationId, Long parentMessageId, String callerSub) {
    return conversationService
        .assertParticipant(conversationId, callerSub)
        .then(
            Mono.defer(
                () ->
                    messageRepository
                        .findByConversationIdAndParentMessageIdOrderByIdAsc(
                            conversationId, parentMessageId)
                        .collectList()))
        // Replies don't themselves have reactions/reply-counts enriched yet — keep scope to
        // what the thread panel actually shows for now (reactions on replies is a natural
        // follow-up, not included here).
        .map(replies -> replies.stream().map(m -> new MessageResponse(m, List.of(), 0L)).toList());
  }

  private MessageReaction newReaction(Long messageId, String sub, String emoji) {
    MessageReaction reaction = new MessageReaction();
    reaction.setMessageId(messageId);
    reaction.setParticipantSub(sub);
    reaction.setEmoji(emoji);
    reaction.setCreatedAt(ZonedDateTime.now());
    return reaction;
  }

  private Mono<Void> broadcastReactionUpdate(UUID conversationId, Long messageId) {
    return reactionRepository
        .findByMessageIdIn(List.of(messageId))
        .collectList()
        .map(this::aggregate)
        .doOnSuccess(
            reactions ->
                broadcastToOthers(
                    conversationId,
                    null,
                    ChatEvent.ChatEventType.REACTION_UPDATED,
                    Map.of(
                        "conversationId", conversationId,
                        "messageId", messageId,
                        "reactions", reactions)))
        .then();
  }

  private Mono<List<MessageResponse>> enrichMessages(List<Message> messages) {
    if (messages.isEmpty()) {
      return Mono.just(List.of());
    }
    List<Long> ids = messages.stream().map(Message::getId).toList();

    Mono<Map<Long, Collection<MessageReaction>>> reactionsByMessage =
        reactionRepository
            .findByMessageIdIn(ids)
            .collectMultimap(MessageReaction::getMessageId, r -> r);
    Mono<Map<Long, Long>> replyCountsByMessage = countReplies(ids);

    return Mono.zip(reactionsByMessage, replyCountsByMessage)
        .map(
            tuple ->
                messages.stream()
                    .map(
                        m ->
                            new MessageResponse(
                                m,
                                aggregate(tuple.getT1().get(m.getId())),
                                tuple.getT2().getOrDefault(m.getId(), 0L)))
                    .toList());
  }

  private Mono<Map<Long, Long>> countReplies(List<Long> parentIds) {
    if (parentIds.isEmpty()) {
      return Mono.just(Map.of());
    }
    return databaseClient
        .sql(REPLY_COUNT_SQL)
        .bind("parentIds", parentIds.toArray(new Long[0]))
        .map(
            row ->
                new AbstractMap.SimpleEntry<>(
                    row.get("parent_message_id", Long.class), row.get("reply_count", Long.class)))
        .all()
        .collectMap(Map.Entry::getKey, Map.Entry::getValue);
  }

  private List<ReactionSummary> aggregate(Collection<MessageReaction> reactions) {
    if (reactions == null || reactions.isEmpty()) {
      return List.of();
    }
    return reactions.stream()
        .collect(
            Collectors.groupingBy(
                MessageReaction::getEmoji,
                Collectors.mapping(MessageReaction::getParticipantSub, Collectors.toList())))
        .entrySet()
        .stream()
        .map(e -> new ReactionSummary(e.getKey(), e.getValue()))
        .toList();
  }

  /**
   * Turns free-typed search-box text into a Postgres prefix-match tsquery expression, e.g. {@code
   * "verif auto"} -> {@code "verif:* & auto:*"}, so partial words match (plain
   * to_tsquery/websearch_to_tsquery only match whole lexemes). Each token is stripped down to
   * letters/digits before appending {@code :*}, since to_tsquery's own syntax characters (&, |, !,
   * (, ), :) in raw user input would otherwise throw a parse error — this is also why
   * quoted-phrase/"-exclude"/"or" search-box syntax isn't supported here, unlike
   * websearch_to_tsquery.
   */
  private String buildPrefixQuery(String query) {
    if (query == null || query.isBlank()) {
      return "";
    }
    return Arrays.stream(query.trim().split("\\s+"))
        .map(token -> NON_WORD.matcher(token).replaceAll(""))
        .filter(token -> !token.isBlank())
        .map(token -> token + ":*")
        .collect(Collectors.joining(" & "));
  }

  private void broadcastMessage(MessageResponse response) {
    Message message = response.getMessage();
    participantRepository
        .findByConversationId(message.getConversationId())
        .map(ConversationParticipant::getParticipantSub)
        .collectList()
        .subscribe(
            subs ->
                sessionRegistry.publish(
                    subs,
                    new ChatEvent(
                        ChatEvent.ChatEventType.MESSAGE_CREATED,
                        Map.of(
                            "conversationId", message.getConversationId(), "message", response))),
            error -> log.error("Failed to broadcast new message: {}", error.getMessage(), error));
  }

  /**
   * Recipients who are online right now get marked delivered immediately; anyone offline catches up
   * via {@link #markDeliveredOnFetch} the next time they load the thread.
   */
  private void markDeliveredForOnlineRecipients(
      UUID conversationId, String senderSub, Long messageId) {
    participantRepository
        .findByConversationId(conversationId)
        .map(ConversationParticipant::getParticipantSub)
        .filter(sub -> !sub.equals(senderSub))
        .collectList()
        .flatMap(presenceService::onlineSubsOf)
        .flatMapMany(Flux::fromIterable)
        .flatMap(
            sub ->
                participantRepository
                    .bumpDeliveredThrough(conversationId, sub, messageId)
                    .filter(count -> count > 0)
                    .doOnNext(
                        count ->
                            broadcastToOthers(
                                conversationId,
                                sub,
                                ChatEvent.ChatEventType.RECEIPT_UPDATED,
                                Map.of(
                                    "conversationId", conversationId,
                                    "sub", sub,
                                    "deliveredThroughMessageId", messageId))))
        .subscribe(
            v -> {},
            error -> log.error("Failed to mark delivered on send: {}", error.getMessage(), error));
  }

  private void markDeliveredOnFetch(UUID conversationId, String callerSub) {
    conversationRepository
        .findById(conversationId)
        .flatMap(
            conversation -> {
              Long through = conversation.getLastMessageId();
              if (through == null) {
                return Mono.empty();
              }
              return participantRepository
                  .bumpDeliveredThrough(conversationId, callerSub, through)
                  .filter(count -> count > 0)
                  .doOnNext(
                      count ->
                          broadcastToOthers(
                              conversationId,
                              callerSub,
                              ChatEvent.ChatEventType.RECEIPT_UPDATED,
                              Map.of(
                                  "conversationId", conversationId,
                                  "sub", callerSub,
                                  "deliveredThroughMessageId", through)));
            })
        .subscribe(
            v -> {},
            error -> log.error("Failed to mark delivered on fetch: {}", error.getMessage(), error));
  }

  private void broadcastToOthers(
      UUID conversationId, String excludeSub, ChatEvent.ChatEventType type, Object payload) {
    participantRepository
        .findByConversationId(conversationId)
        .map(ConversationParticipant::getParticipantSub)
        .filter(sub -> !sub.equals(excludeSub))
        .collectList()
        .subscribe(
            subs -> {
              if (!subs.isEmpty()) {
                sessionRegistry.publish(subs, new ChatEvent(type, payload));
              }
            },
            error -> log.error("Failed to broadcast {}: {}", type, error.getMessage(), error));
  }
}
