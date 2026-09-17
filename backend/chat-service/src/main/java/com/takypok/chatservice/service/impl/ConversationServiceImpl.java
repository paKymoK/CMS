package com.takypok.chatservice.service.impl;

import com.takypok.chatservice.model.ConversationType;
import com.takypok.chatservice.model.ParticipantRole;
import com.takypok.chatservice.model.entity.Conversation;
import com.takypok.chatservice.model.entity.ConversationParticipant;
import com.takypok.chatservice.model.entity.Message;
import com.takypok.chatservice.model.event.ChatEvent;
import com.takypok.chatservice.model.request.CreateConversationRequest;
import com.takypok.chatservice.model.response.ConversationListResponse;
import com.takypok.chatservice.model.response.ConversationSummaryResponse;
import com.takypok.chatservice.model.response.PublicChannelResponse;
import com.takypok.chatservice.repository.ConversationParticipantRepository;
import com.takypok.chatservice.repository.ConversationRepository;
import com.takypok.chatservice.repository.MessageRepository;
import com.takypok.chatservice.service.ChatSessionRegistry;
import com.takypok.chatservice.service.ConversationService;
import com.takypok.core.model.authentication.User;
import java.time.ZonedDateTime;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConversationServiceImpl implements ConversationService {
  private static final int DEFAULT_PAGE_SIZE = 50;
  private static final int MAX_PAGE_SIZE = 100;

  private static final String UNREAD_COUNT_SQL =
      """
      SELECT m.conversation_id AS conversation_id, COUNT(*) AS unread_count
      FROM message m
      JOIN conversation_participant cp
        ON cp.conversation_id = m.conversation_id AND cp.participant_sub = :sub
      WHERE m.conversation_id = ANY(:conversationIds)
        AND m.id > COALESCE(cp.last_read_message_id, 0)
      GROUP BY m.conversation_id
      """;

  private final ConversationRepository conversationRepository;
  private final ConversationParticipantRepository participantRepository;
  private final MessageRepository messageRepository;
  private final ChatSessionRegistry sessionRegistry;
  private final DatabaseClient databaseClient;

  @Override
  public Mono<Conversation> createConversation(CreateConversationRequest request, User caller) {
    if (request.getType() == ConversationType.GROUP
        && (request.getName() == null || request.getName().isBlank())) {
      return Mono.error(new IllegalStateException("A group conversation requires a name"));
    }

    if (request.getType() == ConversationType.DIRECT) {
      if (request.getParticipantSubs().size() != 1) {
        return Mono.error(
            new IllegalStateException(
                "A direct conversation requires exactly one other participant"));
      }
      String otherSub = request.getParticipantSubs().get(0);
      return conversationRepository
          .findExistingDirectConversation(caller.getSub(), otherSub)
          .switchIfEmpty(Mono.defer(() -> createNewConversation(request, caller)));
    }

    // GROUP may have zero other participants — a public channel in particular can be created
    // solo and grown later via joinChannel/addParticipants.
    return createNewConversation(request, caller);
  }

  private Mono<Conversation> createNewConversation(CreateConversationRequest request, User caller) {
    Conversation conversation = new Conversation();
    conversation.setType(request.getType());
    conversation.setName(request.getType() == ConversationType.GROUP ? request.getName() : null);
    conversation.setPrivateChannel(
        request.getType() == ConversationType.GROUP
            ? !Boolean.FALSE.equals(request.getPrivateChannel())
            : true);

    return conversationRepository
        .save(conversation)
        .flatMap(
            saved -> {
              List<String> subs = new ArrayList<>(request.getParticipantSubs());
              subs.add(caller.getSub());
              List<ConversationParticipant> participants =
                  subs.stream()
                      .distinct()
                      .map(sub -> newParticipant(saved.getId(), sub, sub.equals(caller.getSub())))
                      .toList();
              return participantRepository.saveAll(participants).then(Mono.just(saved));
            });
  }

  private ConversationParticipant newParticipant(UUID conversationId, String sub, boolean isOwner) {
    ConversationParticipant participant = new ConversationParticipant();
    participant.setConversationId(conversationId);
    participant.setParticipantSub(sub);
    participant.setRole(isOwner ? ParticipantRole.OWNER : ParticipantRole.MEMBER);
    participant.setJoinedAt(ZonedDateTime.now());
    return participant;
  }

  @Override
  public Mono<ConversationListResponse> listMyConversations(
      String callerSub, ZonedDateTime before, UUID beforeId, int size) {
    int pageSize = size <= 0 ? DEFAULT_PAGE_SIZE : Math.min(size, MAX_PAGE_SIZE);
    int fetchSize = pageSize + 1; // one extra row so we know whether there's a next page

    Flux<Conversation> pageFlux =
        before == null || beforeId == null
            ? conversationRepository.findPageForParticipant(callerSub, fetchSize)
            : conversationRepository.findPageForParticipantBefore(
                callerSub, before, beforeId, fetchSize);

    return pageFlux.collectList().flatMap(fetched -> buildPage(fetched, callerSub, pageSize));
  }

  private Mono<ConversationListResponse> buildPage(
      List<Conversation> fetched, String callerSub, int pageSize) {
    boolean hasMore = fetched.size() > pageSize;
    List<Conversation> page = hasMore ? fetched.subList(0, pageSize) : fetched;

    if (page.isEmpty()) {
      return Mono.just(new ConversationListResponse(List.of(), false, null, null));
    }

    List<UUID> conversationIds = page.stream().map(Conversation::getId).toList();
    List<Long> lastMessageIds =
        page.stream()
            .map(Conversation::getLastMessageId)
            .filter(java.util.Objects::nonNull)
            .toList();

    Mono<Map<UUID, Collection<ConversationParticipant>>> participantsByConversation =
        participantRepository
            .findByConversationIdIn(conversationIds)
            .collectMultimap(ConversationParticipant::getConversationId, p -> p);

    Mono<Map<Long, Message>> lastMessagesById =
        lastMessageIds.isEmpty()
            ? Mono.just(Map.of())
            : messageRepository.findByIdIn(lastMessageIds).collectMap(Message::getId);

    Mono<Map<UUID, Long>> unreadByConversation = countUnread(callerSub, conversationIds);

    return Mono.zip(participantsByConversation, lastMessagesById, unreadByConversation)
        .map(
            tuple -> {
              Map<UUID, Collection<ConversationParticipant>> participants = tuple.getT1();
              Map<Long, Message> lastMessages = tuple.getT2();
              Map<UUID, Long> unread = tuple.getT3();

              List<ConversationSummaryResponse> items =
                  page.stream()
                      .map(
                          c -> {
                            Collection<ConversationParticipant> parts =
                                participants.getOrDefault(c.getId(), List.of());
                            List<String> subs =
                                parts.stream()
                                    .map(ConversationParticipant::getParticipantSub)
                                    .distinct()
                                    .toList();

                            // Read/delivery ticks are only unambiguous for a DIRECT
                            // conversation — there's exactly one "other participant".
                            ConversationParticipant peer =
                                c.getType() == ConversationType.DIRECT
                                    ? parts.stream()
                                        .filter(p -> !p.getParticipantSub().equals(callerSub))
                                        .findFirst()
                                        .orElse(null)
                                    : null;

                            boolean owner =
                                parts.stream()
                                    .anyMatch(
                                        p ->
                                            p.getParticipantSub().equals(callerSub)
                                                && p.getRole() == ParticipantRole.OWNER);

                            return new ConversationSummaryResponse(
                                c.getId(),
                                c.getType(),
                                c.getName(),
                                subs,
                                c.getLastMessageId() == null
                                    ? null
                                    : lastMessages.get(c.getLastMessageId()),
                                unread.getOrDefault(c.getId(), 0L),
                                peer == null ? null : peer.getLastReadMessageId(),
                                peer == null ? null : peer.getDeliveredThroughMessageId(),
                                c.getType() == ConversationType.GROUP
                                    ? c.getPrivateChannel()
                                    : null,
                                owner);
                          })
                      .toList();

              Conversation last = page.get(page.size() - 1);
              ZonedDateTime nextBefore =
                  last.getLastMessageAt() != null ? last.getLastMessageAt() : last.getCreatedAt();

              return new ConversationListResponse(
                  items, hasMore, hasMore ? nextBefore : null, hasMore ? last.getId() : null);
            });
  }

  private Mono<Map<UUID, Long>> countUnread(String callerSub, List<UUID> conversationIds) {
    return databaseClient
        .sql(UNREAD_COUNT_SQL)
        .bind("sub", callerSub)
        .bind("conversationIds", conversationIds.toArray(new UUID[0]))
        .map(
            row ->
                new AbstractMap.SimpleEntry<>(
                    row.get("conversation_id", UUID.class), row.get("unread_count", Long.class)))
        .all()
        .collectMap(Map.Entry::getKey, Map.Entry::getValue);
  }

  @Override
  public Mono<Conversation> rename(UUID conversationId, String name, String callerSub) {
    return requireOwner(conversationId, callerSub)
        .then(conversationRepository.findById(conversationId))
        .switchIfEmpty(Mono.error(new IllegalStateException("Conversation not found")))
        .flatMap(
            conversation -> {
              conversation.setName(name);
              return conversationRepository.save(conversation);
            })
        .doOnSuccess(
            conversation ->
                notifyParticipants(
                    conversationId,
                    ChatEvent.ChatEventType.CONVERSATION_RENAMED,
                    Map.of("conversationId", conversationId, "name", name)));
  }

  @Override
  public Mono<Void> addParticipants(
      UUID conversationId, List<String> participantSubs, String callerSub) {
    List<String> subs = participantSubs.stream().distinct().toList();
    return requireOwner(conversationId, callerSub)
        .thenMany(Flux.fromIterable(subs))
        .flatMap(
            sub ->
                participantRepository
                    .findByConversationIdAndParticipantSub(conversationId, sub)
                    .switchIfEmpty(
                        Mono.defer(
                            () ->
                                participantRepository.save(
                                    newParticipant(conversationId, sub, false)))))
        .then()
        .doOnSuccess(
            v ->
                notifyParticipants(
                    conversationId,
                    ChatEvent.ChatEventType.PARTICIPANT_ADDED,
                    Map.of("conversationId", conversationId, "participantSubs", subs)));
  }

  @Override
  public Mono<Void> removeParticipant(UUID conversationId, String targetSub, String callerSub) {
    Mono<Void> authorized =
        callerSub.equals(targetSub) ? Mono.empty() : requireOwner(conversationId, callerSub);

    return authorized
        .then(
            participantRepository.deleteByConversationIdAndParticipantSub(
                conversationId, targetSub))
        .doOnSuccess(
            v ->
                notifyParticipants(
                    conversationId,
                    ChatEvent.ChatEventType.PARTICIPANT_REMOVED,
                    Map.of("conversationId", conversationId, "participantSub", targetSub)));
  }

  @Override
  public Mono<Void> markRead(UUID conversationId, String callerSub) {
    Mono<ConversationParticipant> participantMono =
        participantRepository
            .findByConversationIdAndParticipantSub(conversationId, callerSub)
            .switchIfEmpty(
                Mono.error(new IllegalStateException("Not a participant of this conversation")));
    Mono<Conversation> conversationMono =
        conversationRepository
            .findById(conversationId)
            .switchIfEmpty(Mono.error(new IllegalStateException("Conversation not found")));

    return Mono.zip(participantMono, conversationMono)
        .flatMap(
            tuple -> {
              ConversationParticipant participant = tuple.getT1();
              Conversation conversation = tuple.getT2();
              Long through = conversation.getLastMessageId();
              // No-op when there's nothing new to mark read — skipping this is what matters,
              // not just for the wasted write. Every open thread calls markRead on mount and
              // again on each new message; without this guard it broadcasts RECEIPT_UPDATED
              // unconditionally, which round-trips into the peer refetching and broadcasting
              // back, forever.
              if (through == null
                  || (participant.getLastReadMessageId() != null
                      && participant.getLastReadMessageId() >= through)) {
                return Mono.<Long>empty();
              }
              participant.setLastReadAt(ZonedDateTime.now());
              participant.setLastReadMessageId(through);
              // Having read it implies having received it — keep the two watermarks
              // consistent instead of leaving delivered behind read.
              participant.setDeliveredThroughMessageId(through);
              return participantRepository.save(participant).thenReturn(through);
            })
        .doOnSuccess(
            through -> {
              if (through != null) {
                notifyParticipants(
                    conversationId,
                    ChatEvent.ChatEventType.RECEIPT_UPDATED,
                    Map.of(
                        "conversationId", conversationId,
                        "sub", callerSub,
                        "readThroughMessageId", through,
                        "deliveredThroughMessageId", through),
                    callerSub);
              }
            })
        .then();
  }

  @Override
  public Mono<Void> notifyTyping(UUID conversationId, User caller) {
    return assertParticipant(conversationId, caller.getSub())
        .doOnSuccess(
            v ->
                notifyParticipants(
                    conversationId,
                    ChatEvent.ChatEventType.TYPING,
                    Map.of(
                        "conversationId",
                        conversationId,
                        "sub",
                        caller.getSub(),
                        "name",
                        caller.getName()),
                    caller.getSub()));
  }

  @Override
  public Mono<List<PublicChannelResponse>> browsePublicChannels(String callerSub, String search) {
    int limit = 50;
    Flux<Conversation> channels =
        search == null || search.isBlank()
            ? conversationRepository.findPublicChannelsNotJoined(callerSub, limit)
            : conversationRepository.findPublicChannelsNotJoinedMatching(
                callerSub, search.trim(), limit);

    return channels
        .flatMap(
            c ->
                participantRepository
                    .countByConversationId(c.getId())
                    .defaultIfEmpty(0L)
                    .map(count -> new PublicChannelResponse(c.getId(), c.getName(), count)))
        .collectList();
  }

  @Override
  public Mono<Void> joinChannel(UUID conversationId, String callerSub) {
    return conversationRepository
        .findById(conversationId)
        .switchIfEmpty(Mono.error(new IllegalStateException("Conversation not found")))
        .flatMap(
            conversation -> {
              if (conversation.getType() != ConversationType.GROUP
                  || Boolean.TRUE.equals(conversation.getPrivateChannel())) {
                return Mono.<Void>error(
                    new IllegalStateException("This channel is not open to join"));
              }
              return participantRepository
                  .findByConversationIdAndParticipantSub(conversationId, callerSub)
                  .switchIfEmpty(
                      Mono.defer(
                          () ->
                              participantRepository.save(
                                  newParticipant(conversationId, callerSub, false))))
                  .then();
            })
        .doOnSuccess(
            v ->
                notifyParticipants(
                    conversationId,
                    ChatEvent.ChatEventType.PARTICIPANT_ADDED,
                    Map.of("conversationId", conversationId, "participantSubs", List.of(callerSub)),
                    callerSub));
  }

  @Override
  public Mono<Void> assertParticipant(UUID conversationId, String sub) {
    return participantRepository
        .findByConversationIdAndParticipantSub(conversationId, sub)
        .switchIfEmpty(
            Mono.error(new IllegalStateException("Not a participant of this conversation")))
        .then();
  }

  private Mono<Void> requireOwner(UUID conversationId, String sub) {
    return participantRepository
        .findByConversationIdAndParticipantSub(conversationId, sub)
        .switchIfEmpty(
            Mono.error(new IllegalStateException("Not a participant of this conversation")))
        .flatMap(
            participant ->
                participant.getRole() == ParticipantRole.OWNER
                    ? Mono.<Void>empty()
                    : Mono.error(
                        new IllegalStateException("Only the owner can perform this action")));
  }

  private void notifyParticipants(
      UUID conversationId, ChatEvent.ChatEventType type, Object payload) {
    notifyParticipants(conversationId, type, payload, null);
  }

  private void notifyParticipants(
      UUID conversationId, ChatEvent.ChatEventType type, Object payload, String excludeSub) {
    participantRepository
        .findByConversationId(conversationId)
        .map(ConversationParticipant::getParticipantSub)
        .filter(sub -> !sub.equals(excludeSub))
        .collectList()
        .subscribe(
            subs -> sessionRegistry.publish(subs, new ChatEvent(type, payload)),
            error ->
                log.error(
                    "Failed to broadcast chat event {}: {}", type, error.getMessage(), error));
  }
}
