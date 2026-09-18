package com.takypok.chatservice.controller;

import com.takypok.chatservice.config.AssistantRateLimiter;
import com.takypok.chatservice.config.CmsAdminGuard;
import com.takypok.chatservice.model.PublicAnswerResponse;
import com.takypok.chatservice.model.PublicAskRequest;
import com.takypok.chatservice.service.PublicAssistantService;
import com.takypok.chatservice.service.SiteIngestionService;
import com.takypok.core.exception.ApplicationException;
import com.takypok.core.model.Message;
import com.takypok.core.model.ResultMessage;
import java.net.InetSocketAddress;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Public marketing assistant for the CMC Global sites — unauthenticated by design (anonymous site
 * visitors), rate-limited by IP, one call per question (client keeps its own history). See
 * PublicAssistantService/SiteIngestionService for the per-site RAG and ingestion logic; see core-v1
 * SecurityConfig for the POST /v1/assistant/ask permitAll carve-out.
 */
@RestController
@RequestMapping("/v1/assistant")
@RequiredArgsConstructor
@ConditionalOnProperty(name = "ai.rag.enabled", havingValue = "true", matchIfMissing = false)
public class PublicAssistantController {

  private final PublicAssistantService publicAssistantService;
  private final SiteIngestionService siteIngestionService;
  private final AssistantRateLimiter rateLimiter;
  private final CmsAdminGuard cmsAdminGuard;

  @PostMapping("/ask")
  public Mono<PublicAnswerResponse> ask(
      @RequestBody PublicAskRequest request,
      ServerWebExchange exchange,
      Authentication authentication) {
    if (!StringUtils.hasText(request.getSite()) || !StringUtils.hasText(request.getQuestion())) {
      return Mono.error(
          new ApplicationException(Message.Application.ERROR, "site and question are required"));
    }

    // Anonymous, unauthenticated requests are rate-limited by IP; an authenticated caller (an
    // internal test account, per the decision log) is exempt — never throttle internal testing.
    // core-v1's SecurityConfig doesn't disable anonymous auth, so an unauthenticated request on
    // this permitAll endpoint still arrives with a non-null AnonymousAuthenticationToken, not a
    // null Authentication — check the concrete type, not just non-null.
    boolean isRealUser =
        authentication != null
            && authentication.isAuthenticated()
            && !(authentication instanceof AnonymousAuthenticationToken);
    Mono<Void> gate =
        isRealUser ? Mono.empty() : rateLimiter.checkAnonymous(remoteAddress(exchange));

    return gate.then(
        publicAssistantService.ask(request.getSite(), request.getQuestion(), request.getHistory()));
  }

  @PostMapping("/ingest")
  public Mono<ResultMessage<Integer>> ingest(
      @RequestParam String site, Authentication authentication) {
    return cmsAdminGuard
        .requireSiteAccess(authentication, site)
        .then(siteIngestionService.ingest(site))
        .map(ResultMessage::success);
  }

  // Server-observed remote address only — never a client-supplied header (X-Forwarded-For etc.
  // would let a caller spoof a fresh IP on every request and bypass the rate limit entirely).
  private String remoteAddress(ServerWebExchange exchange) {
    InetSocketAddress address = exchange.getRequest().getRemoteAddress();
    return address == null ? "unknown" : address.getAddress().getHostAddress();
  }
}
