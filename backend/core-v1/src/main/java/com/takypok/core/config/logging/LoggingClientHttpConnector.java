package com.takypok.core.config.logging;

import java.net.URI;
import java.util.function.Function;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ClientHttpRequest;
import org.springframework.http.client.reactive.ClientHttpResponse;
import reactor.core.publisher.Mono;

public class LoggingClientHttpConnector implements ClientHttpConnector {
  private final ClientHttpConnector delegate;

  public LoggingClientHttpConnector(ClientHttpConnector delegate) {
    this.delegate = delegate;
  }

  @Override
  public Mono<ClientHttpResponse> connect(
      HttpMethod method, URI uri, Function<? super ClientHttpRequest, Mono<Void>> requestCallback) {
    return this.delegate.connect(method, uri, requestCallback).map(LoggingClientHttpResponse::new);
  }
}
