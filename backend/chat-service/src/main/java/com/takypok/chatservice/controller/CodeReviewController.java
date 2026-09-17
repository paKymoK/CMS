package com.takypok.chatservice.controller;

import com.takypok.chatservice.model.CodeReviewResponse;
import com.takypok.chatservice.model.request.RemoteDiffReviewRequest;
import com.takypok.chatservice.service.CodeReviewService;
import com.takypok.chatservice.service.RemoteDiffService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/** Takes a raw `git diff` as the request body and reviews it against the anti-pattern rule set. */
@RestController
@RequestMapping("/code-review")
@RequiredArgsConstructor
@ConditionalOnProperty(name = "ai.rag.enabled", havingValue = "true", matchIfMissing = false)
public class CodeReviewController {

  private final CodeReviewService codeReviewService;
  private final RemoteDiffService remoteDiffService;

  @PostMapping(consumes = MediaType.TEXT_PLAIN_VALUE)
  public Mono<ResponseEntity<CodeReviewResponse>> review(
      @RequestBody(required = false) String diffText) {
    return codeReviewService.review(diffText).map(ResponseEntity::ok);
  }

  @PostMapping(value = "/remote", consumes = MediaType.APPLICATION_JSON_VALUE)
  public Mono<ResponseEntity<CodeReviewResponse>> reviewRemote(
      @RequestBody @Valid RemoteDiffReviewRequest request) {
    return remoteDiffService
        .fetchDiff(request.getUrl())
        .flatMap(codeReviewService::review)
        .map(ResponseEntity::ok);
  }
}
