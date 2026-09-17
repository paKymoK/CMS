package com.takypok.chatservice.service;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

/**
 * Fetches the unified diff for a GitLab merge request via GitLab's `.diff` suffix route (e.g.
 * `.../-/merge_requests/45.diff`), which returns the same `diff --git a/... b/...` text format
 * CodeReviewService already expects — no JSON reconstruction needed.
 */
@Slf4j
@Component
public class GitLabDiffProvider implements GitDiffProvider {

  // Matches "/<namespace>/.../-/merge_requests/<iid>", tolerating a trailing slash. Namespaces
  // can be nested (subgroups), hence the greedy `.+` before the fixed "-/merge_requests/" marker.
  private static final Pattern MERGE_REQUEST_PATH =
      Pattern.compile("^/.+/-/merge_requests/\\d+/?$");

  private final WebClient webClient;
  private final String token;
  private final URI configuredBaseUri;

  public GitLabDiffProvider(
      WebClient.Builder webClientBuilder,
      @Value("${gitlab.base-url:}") String baseUrl,
      @Value("${gitlab.token:}") String token) {
    this.webClient =
        webClientBuilder
            .exchangeStrategies(
                ExchangeStrategies.builder()
                    .codecs(c -> c.defaultCodecs().maxInMemorySize(5 * 1024 * 1024))
                    .build())
            .build();
    this.token = token;
    this.configuredBaseUri = StringUtils.hasText(baseUrl) ? URI.create(baseUrl) : null;
  }

  @Override
  public boolean supports(URI uri) {
    return configuredBaseUri != null
        && StringUtils.hasText(token)
        && belongsToConfiguredInstance(uri, configuredBaseUri)
        && MERGE_REQUEST_PATH.matcher(uri.getPath()).matches();
  }

  @Override
  public Mono<String> fetchDiff(URI uri) {
    // Rebuild the outgoing URL from the validated scheme/host/port/path rather than reusing the
    // caller-supplied string, so any query/fragment/userinfo on the input is dropped before we
    // attach our server-side token to the request.
    String diffUrl =
        configuredBaseUri.getScheme()
            + "://"
            + uri.getHost()
            + (uri.getPort() != -1 ? ":" + uri.getPort() : "")
            + uri.getPath().replaceAll("/+$", "")
            + ".diff";

    return webClient
        .get()
        .uri(diffUrl)
        .header("PRIVATE-TOKEN", token)
        .retrieve()
        // Read as raw bytes rather than letting content-negotiation pick a String decoder — the
        // response content type isn't guaranteed to be one WebClient's default codecs recognize.
        .bodyToMono(byte[].class)
        .map(bytes -> new String(bytes, StandardCharsets.UTF_8))
        .onErrorMap(
            WebClientResponseException.class,
            e ->
                new IllegalStateException(
                    "Failed to fetch diff from GitLab (" + e.getStatusCode() + ")", e));
  }

  private boolean belongsToConfiguredInstance(URI requested, URI configured) {
    return requested.getScheme() != null
        && requested.getScheme().equals(configured.getScheme())
        && requested.getHost() != null
        && requested.getHost().equalsIgnoreCase(configured.getHost())
        && requested.getPort() == configured.getPort();
  }
}
