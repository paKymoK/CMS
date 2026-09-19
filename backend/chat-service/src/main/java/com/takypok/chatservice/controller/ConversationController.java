package com.takypok.chatservice.controller;

import static com.takypok.core.util.AuthenticationUtil.getUserInfo;

import com.takypok.chatservice.model.entity.Conversation;
import com.takypok.chatservice.model.request.AddParticipantsRequest;
import com.takypok.chatservice.model.request.CreateConversationRequest;
import com.takypok.chatservice.model.request.RenameConversationRequest;
import com.takypok.chatservice.model.response.ConversationListResponse;
import com.takypok.chatservice.service.ConversationService;
import jakarta.validation.Valid;
import java.time.ZonedDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat/conversations")
public class ConversationController {
  private final ConversationService conversationService;

  @PostMapping
  public Mono<Conversation> create(
      @RequestBody @Valid CreateConversationRequest request, Authentication authentication) {
    return conversationService.createConversation(request, getUserInfo(authentication));
  }

  @GetMapping
  public Mono<ConversationListResponse> list(
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
          ZonedDateTime before,
      @RequestParam(required = false) UUID beforeId,
      @RequestParam(defaultValue = "50") int size,
      Authentication authentication) {
    return conversationService.listMyConversations(
        getUserInfo(authentication).getSub(), before, beforeId, size);
  }

  @PatchMapping("/{id}")
  public Mono<Conversation> rename(
      @PathVariable UUID id,
      @RequestBody @Valid RenameConversationRequest request,
      Authentication authentication) {
    return conversationService.rename(id, request.getName(), getUserInfo(authentication).getSub());
  }

  @PostMapping("/{id}/members")
  public Mono<Void> addMembers(
      @PathVariable UUID id,
      @RequestBody @Valid AddParticipantsRequest request,
      Authentication authentication) {
    return conversationService.addParticipants(
        id, request.getParticipantSubs(), getUserInfo(authentication).getSub());
  }

  @DeleteMapping("/{id}/members/{sub}")
  public Mono<Void> removeMember(
      @PathVariable UUID id, @PathVariable String sub, Authentication authentication) {
    return conversationService.removeParticipant(id, sub, getUserInfo(authentication).getSub());
  }

  @PostMapping("/{id}/read")
  public Mono<Void> markRead(@PathVariable UUID id, Authentication authentication) {
    return conversationService.markRead(id, getUserInfo(authentication).getSub());
  }

  @PostMapping("/{id}/typing")
  public Mono<Void> typing(@PathVariable UUID id, Authentication authentication) {
    return conversationService.notifyTyping(id, getUserInfo(authentication));
  }

  @PostMapping("/{id}/join")
  public Mono<Void> join(@PathVariable UUID id, Authentication authentication) {
    return conversationService.joinChannel(id, getUserInfo(authentication).getSub());
  }
}
