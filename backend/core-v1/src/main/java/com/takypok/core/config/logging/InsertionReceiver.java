package com.takypok.core.config.logging;

import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.web.reactive.function.BodyInserter;

public interface InsertionReceiver<T> {

  T receiveValue(BodyInserter<?, ? extends ReactiveHttpOutputMessage> bodyInserter);

  static <T> InsertionReceiver<T> forClass(Class<T> clazz) {
    return new SimpleValueReceiver<>(clazz);
  }
}
