package com.takypok.contentservice.controller;

import com.takypok.contentservice.model.response.SearchResponses;
import com.takypok.contentservice.service.SearchService;
import com.takypok.contentservice.service.SiteService;
import com.takypok.core.model.ResultMessage;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Public, unauthenticated search helpers — what the visitor's search box needs besides the results
 * themselves (those come from the listing endpoints, which apply the same correction):
 *
 * <ul>
 *   <li>{@code /v1/search/suggest}: completions and matching titles while a query is being typed;
 *   <li>{@code /v1/search/resolve}: whether a query was corrected, and to what — for the "showing
 *       results for…" line.
 * </ul>
 *
 * Published content only; site resolved from the visitor's Host header like every other public
 * read. /v1/admin/search is a different prefix and is authenticated.
 */
@RestController
@RequiredArgsConstructor
public class PublicSearchController {
  /** Types with a page of their own on the public site. */
  static final List<String> PUBLIC_TYPES = List.of("post", "case_study", "service_page");

  private static final int MAX_QUERY_LENGTH = 100;
  private static final int RESOLVE_LIMIT = 200;

  private final SearchService searchService;
  private final SiteService siteService;

  @GetMapping("/v1/search/suggest")
  public Mono<ResultMessage<SearchResponses.Suggest>> suggest(
      @RequestParam String q,
      @RequestParam(required = false) String type,
      ServerWebExchange exchange) {
    return siteService
        .resolveFromHost(exchange)
        .flatMap(site -> searchService.suggest(site.getId(), types(type), clamp(q), true))
        .map(SearchResponses.Suggest::new)
        .map(ResultMessage::success);
  }

  @GetMapping("/v1/search/resolve")
  public Mono<ResultMessage<SearchResponses.Resolve>> resolve(
      @RequestParam String q,
      @RequestParam(required = false) String type,
      @RequestParam(defaultValue = "false") boolean exact,
      ServerWebExchange exchange) {
    return siteService
        .resolveFromHost(exchange)
        .flatMap(
            site ->
                searchService.resolve(
                    site.getId(), types(type), clamp(q), true, !exact, RESOLVE_LIMIT))
        .map(SearchResponses.Resolve::from)
        .map(ResultMessage::success);
  }

  /** The requested types (comma-separated), limited to the public ones; none asked means all. */
  static List<String> types(String type) {
    if (type == null || type.isBlank()) {
      return PUBLIC_TYPES;
    }
    List<String> requested =
        Arrays.stream(type.split(",")).map(String::trim).filter(PUBLIC_TYPES::contains).toList();
    return requested.isEmpty() ? PUBLIC_TYPES : requested;
  }

  private static String clamp(String q) {
    return q.length() > MAX_QUERY_LENGTH ? q.substring(0, MAX_QUERY_LENGTH) : q;
  }
}
