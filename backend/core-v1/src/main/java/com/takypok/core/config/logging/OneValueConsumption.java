package com.takypok.core.config.logging;

import java.util.Objects;
import java.util.function.Consumer;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

class OneValueConsumption<T> implements Subscriber<T> {

  private final Consumer<T> consumer;
  private int remainedAccepts;

  public OneValueConsumption(Consumer<T> consumer) {
    this.consumer = Objects.requireNonNull(consumer);
    this.remainedAccepts = 1;
  }

  @Override
  public void onSubscribe(Subscription s) {
    s.request(1);
  }

  @Override
  public void onNext(T o) {
    if (remainedAccepts > 0) {
      consumer.accept(o);
      remainedAccepts -= 1;
    } else {
      throw new RuntimeException("No more values can be consumed");
    }
  }

  @Override
  public void onError(Throwable t) {
    throw new RuntimeException("Single value was not consumed", t);
  }

  @Override
  public void onComplete() {
    // nothing
  }
}
