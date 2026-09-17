package com.takypok.core.config.logging;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.http.MediaType;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.http.codec.HttpMessageWriter;
import reactor.core.publisher.Mono;

class WriteToConsumer<T> implements HttpMessageWriter<T> {

  private final Consumer<T> consumer;
  private final List<MediaType> mediaTypes;

  WriteToConsumer(Consumer<T> consumer) {
    this.consumer = consumer;
    this.mediaTypes = Collections.singletonList(MediaType.ALL);
  }

  @Override
  public List<MediaType> getWritableMediaTypes() {
    return mediaTypes;
  }

  @Override
  public boolean canWrite(ResolvableType elementType, MediaType mediaType) {
    return true;
  }

  @Override
  public Mono<Void> write(
      Publisher<? extends T> inputStream,
      ResolvableType elementType,
      MediaType mediaType,
      ReactiveHttpOutputMessage message,
      Map<String, Object> hints) {
    inputStream.subscribe(new OneValueConsumption<>(consumer));
    return Mono.empty();
  }
}
