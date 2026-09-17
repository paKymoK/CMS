package com.takypok.core.config.logging;

import java.util.function.Supplier;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ReactiveHttpOutputMessage;
import reactor.core.publisher.Mono;

class MinimalHttpOutputMessage implements ReactiveHttpOutputMessage {

  public static MinimalHttpOutputMessage INSTANCE = new MinimalHttpOutputMessage();

  private MinimalHttpOutputMessage() {}

  @Override
  public HttpHeaders getHeaders() {
    return HttpHeaders.EMPTY;
  }

  @Override
  public DataBufferFactory bufferFactory() {
    return null;
  }

  @Override
  public void beforeCommit(Supplier<? extends Mono<Void>> action) {}

  @Override
  public boolean isCommitted() {
    return false;
  }

  @Override
  public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
    return null;
  }

  @Override
  public Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body) {
    return null;
  }

  @Override
  public Mono<Void> setComplete() {
    return null;
  }
}
