package com.takypok.chatservice.controller;

import com.takypok.chatservice.model.response.PresenceStatusResponse;
import com.takypok.chatservice.service.PresenceService;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat/presence")
public class PresenceController {
  private final PresenceService presenceService;

  @GetMapping
  public Mono<Map<String, PresenceStatusResponse>> status(@RequestParam List<String> subs) {
    return presenceService.statusOf(subs);
  }
}
