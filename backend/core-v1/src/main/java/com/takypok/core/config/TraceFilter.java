package com.takypok.core.config;

import com.takypok.core.Constants;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

@Slf4j
@Component
@RequiredArgsConstructor
public class TraceFilter implements WebFilter {
  @NonNull
  @Override
  public Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
    ServerHttpRequest request = exchange.getRequest();
    ServerHttpResponse response = exchange.getResponse();

    if (String.valueOf(request.getURI()).contains("/web-socket/")) {
      return chain.filter(exchange);
    }

    if (String.valueOf(request.getURI()).contains("/images/")) {
      return chain.filter(exchange);
    }

    if (String.valueOf(request.getURI()).contains("/actuator/")) {
      return chain.filter(exchange);
    }

    String requestId =
        Optional.ofNullable(request.getHeaders().getFirst(Constants.X_REQUEST_ID))
            .orElse(UUID.randomUUID().toString());

    return ReactiveSecurityContextHolder.getContext()
        .map(
            securityContext -> {
              String userId = securityContext.getAuthentication().getName();
              ServerWebExchange decorated =
                  exchange.mutate().response(decorateResponse(request, response)).build();
              decorated.getResponse().getHeaders().add(Constants.USER_ID, userId);
              decorated.getResponse().getHeaders().add(Constants.X_REQUEST_ID, requestId);
              return Tuples.of(decorated, userId);
            })
        .defaultIfEmpty(Tuples.of(exchange, ""))
        .flatMap(
            tuple ->
                chain
                    .filter(tuple.getT1())
                    .contextWrite(
                        context ->
                            context
                                .put(Constants.X_REQUEST_ID, requestId)
                                .put(Constants.USER_ID, tuple.getT2())));
  }

  private static final Set<String> LOGGABLE_TYPES = Set.of("text", "application");
  private static final Set<String> BINARY_SUBTYPES =
      Set.of(
          "octet-stream",
          "pdf",
          "zip",
          "gzip",
          "vnd.openxmlformats-officedocument.spreadsheetml.sheet",
          "vnd.ms-excel");

  private boolean isLoggable(MediaType contentType) {
    if (contentType == null) return true;
    if (!LOGGABLE_TYPES.contains(contentType.getType())) return false;
    return !BINARY_SUBTYPES.contains(contentType.getSubtype());
  }

  private ServerHttpResponse decorateResponse(
      ServerHttpRequest request, ServerHttpResponse response) {
    return new ServerHttpResponseDecorator(response) {
      @NonNull
      @Override
      public Mono<Void> writeWith(@NonNull Publisher<? extends DataBuffer> body) {
        if (!isLoggable(getHeaders().getContentType())) {
          String type = String.valueOf(getHeaders().getContentType());
          String size =
              Optional.ofNullable(getHeaders().getFirst("Content-Length")).orElse("unknown");
          String disposition =
              Optional.ofNullable(getHeaders().getFirst("Content-Disposition")).orElse("-");
          log.info(
              "[Response to {}]: [binary] type={}, size={} bytes, disposition={}",
              request.getRemoteAddress(),
              type,
              size,
              disposition);
          return super.writeWith(body);
        }
        return DataBufferUtils.join(body)
            .flatMap(
                dataBuffer -> {
                  byte[] bytes = new byte[dataBuffer.readableByteCount()];
                  dataBuffer.read(bytes);
                  DataBufferUtils.release(dataBuffer);
                  String responseBody = new String(bytes, StandardCharsets.UTF_8);
                  log.info("[Response to {}]: {}", request.getRemoteAddress(), responseBody);
                  DataBuffer newBuffer = bufferFactory().wrap(bytes);
                  return super.writeWith(Mono.just(newBuffer));
                });
      }
    };
  }
}
