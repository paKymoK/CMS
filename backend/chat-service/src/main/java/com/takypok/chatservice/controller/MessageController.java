package com.takypok.chatservice.controller;

import static com.takypok.core.util.AuthenticationUtil.getUserInfo;

import com.takypok.chatservice.model.request.SendMessageRequest;
import com.takypok.chatservice.model.request.ToggleReactionRequest;
import com.takypok.chatservice.model.response.MessageResponse;
import com.takypok.chatservice.service.EmployeeDirectoryService;
import com.takypok.chatservice.service.MessageService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat/conversations/{conversationId}/messages")
public class MessageController {
  private final MessageService messageService;
  private final EmployeeDirectoryService employeeDirectoryService;

  @PostMapping
  public Mono<MessageResponse> send(
      @PathVariable UUID conversationId,
      @RequestBody SendMessageRequest request,
      Authentication authentication) {
    return employeeDirectoryService
        .resolveActingUser(authentication.getName())
        .flatMap(sender -> messageService.sendMessage(conversationId, request, sender));
  }

  @GetMapping
  public Mono<List<MessageResponse>> list(
      @PathVariable UUID conversationId,
      @RequestParam(required = false) Long before,
      @RequestParam(defaultValue = "50") int size,
      Authentication authentication) {
    return messageService.listMessages(
        conversationId, before, size, getUserInfo(authentication).getSub());
  }

  @GetMapping("/search")
  public Mono<List<MessageResponse>> search(
      @PathVariable UUID conversationId,
      @RequestParam String q,
      @RequestParam(defaultValue = "50") int limit,
      Authentication authentication) {
    return messageService.searchMessages(
        conversationId, q, limit, getUserInfo(authentication).getSub());
  }

  @PostMapping("/{messageId}/reactions")
  public Mono<Void> toggleReaction(
      @PathVariable UUID conversationId,
      @PathVariable Long messageId,
      @RequestBody @Valid ToggleReactionRequest request,
      Authentication authentication) {
    return messageService.toggleReaction(
        conversationId, messageId, request.getEmoji(), getUserInfo(authentication).getSub());
  }

  @GetMapping("/{messageId}/replies")
  public Mono<List<MessageResponse>> replies(
      @PathVariable UUID conversationId,
      @PathVariable Long messageId,
      Authentication authentication) {
    return messageService.fetchReplies(
        conversationId, messageId, getUserInfo(authentication).getSub());
  }
}
