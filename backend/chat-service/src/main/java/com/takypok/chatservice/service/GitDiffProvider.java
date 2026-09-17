package com.takypok.chatservice.service;

import java.net.URI;
import reactor.core.publisher.Mono;

/** Fetches a unified diff from a specific git hosting platform (GitHub, GitLab, ...). */
public interface GitDiffProvider {

  /** Whether this provider is configured (base URL + token set) and owns this PR/MR URL. */
  boolean supports(URI uri);

  /** Fetches the diff text for a URL this provider has already confirmed it supports. */
  Mono<String> fetchDiff(URI uri);
}
