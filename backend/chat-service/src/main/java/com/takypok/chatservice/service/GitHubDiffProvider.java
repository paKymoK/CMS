package com.takypok.chatservice.service;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
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
 * Fetches the unified diff for a GitHub pull request via the REST API's diff media type (`Accept:
 * application/vnd.github.v3.diff` on `GET /repos/{owner}/{repo}/pulls/{number}`), which returns the
 * same `diff --git a/... b/...` text format CodeReviewService expects.
 *
 * <p>github.com is served from the separate api.github.com host; GitHub Enterprise Server keeps the
 * same host but prefixes the API with /api/v3 — both are handled below.
 */
@Slf4j
@Component
public class GitHubDiffProvider implements GitDiffProvider {

  // GitHub has no nested namespaces (unlike GitLab subgroups), so owner/repo is always two
  // segments.
  private static final Pattern PULL_REQUEST_PATH =
      Pattern.compile("^/(?<owner>[^/]+)/(?<repo>[^/]+)/pull/(?<number>\\d+)/?$");

  private final WebClient webClient;
  private final String token;
  private final URI configuredBaseUri;

  public GitHubDiffProvider(
      WebClient.Builder webClientBuilder,
      @Value("${github.base-url:https://github.com}") String baseUrl,
      @Value("${github.token:}") String token) {
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
        && PULL_REQUEST_PATH.matcher(uri.getPath()).matches();
  }

  @Override
  public Mono<String> fetchDiff(URI uri) {
    Matcher matcher = PULL_REQUEST_PATH.matcher(uri.getPath());
    if (!matcher.matches()) {
      return Mono.error(new IllegalStateException("Not a GitHub pull request URL."));
    }
    String owner = matcher.group("owner");
    String repo = matcher.group("repo");
    String number = matcher.group("number");

    boolean cloud = "github.com".equalsIgnoreCase(uri.getHost());
    String apiUrl =
        cloud
            ? "https://api.github.com/repos/" + owner + "/" + repo + "/pulls/" + number
            : configuredBaseUri.getScheme()
                + "://"
                + uri.getHost()
                + (uri.getPort() != -1 ? ":" + uri.getPort() : "")
                + "/api/v3/repos/"
                + owner
                + "/"
                + repo
                + "/pulls/"
                + number;

    return webClient
        .get()
        .uri(apiUrl)
        .header("Authorization", "Bearer " + token)
        .header("Accept", "application/vnd.github.v3.diff")
        .header("X-GitHub-Api-Version", "2022-11-28")
        .retrieve()
        // Read as raw bytes rather than letting content-negotiation pick a String decoder — the
        // `application/vnd.github.v3.diff` content type isn't one WebClient's default codecs
        // recognize.
        .bodyToMono(byte[].class)
        .map(bytes -> new String(bytes, StandardCharsets.UTF_8))
        .onErrorMap(
            WebClientResponseException.class,
            e ->
                new IllegalStateException(
                    "Failed to fetch diff from GitHub (" + e.getStatusCode() + ")", e));
  }

  private boolean belongsToConfiguredInstance(URI requested, URI configured) {
    return requested.getScheme() != null
        && requested.getScheme().equals(configured.getScheme())
        && requested.getHost() != null
        && requested.getHost().equalsIgnoreCase(configured.getHost())
        && requested.getPort() == configured.getPort();
  }
}
