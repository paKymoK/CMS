package com.takypok.chatservice.service;

import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Picks whichever configured {@link GitDiffProvider} (GitHub, GitLab, ...) a PR/MR URL belongs to.
 */
@Service
@RequiredArgsConstructor
public class RemoteDiffService {

  private final List<GitDiffProvider> providers;

  public Mono<String> fetchDiff(String url) {
    URI uri;
    try {
      uri = URI.create(url);
    } catch (IllegalArgumentException e) {
      return Mono.error(new IllegalStateException("Malformed pull/merge request URL."));
    }

    return providers.stream()
        .filter(p -> p.supports(uri))
        .findFirst()
        .map(p -> p.fetchDiff(uri))
        .orElseGet(
            () ->
                Mono.error(
                    new IllegalStateException(
                        "URL doesn't match a configured GitHub or GitLab instance, or that"
                            + " platform's token isn't set.")));
  }
}
