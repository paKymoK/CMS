package com.takypok.chatservice.controller;

import static com.takypok.core.util.AuthenticationUtil.getUserInfo;

import com.takypok.chatservice.model.response.PublicChannelResponse;
import com.takypok.chatservice.service.ConversationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat/channels")
public class ChannelController {
  private final ConversationService conversationService;

  @GetMapping
  public Mono<List<PublicChannelResponse>> browse(
      @RequestParam(required = false) String search, Authentication authentication) {
    return conversationService.browsePublicChannels(getUserInfo(authentication).getSub(), search);
  }
}
