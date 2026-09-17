package com.takypok.core.config.logging;

import java.util.concurrent.atomic.AtomicReference;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.web.reactive.function.BodyInserter;

class SimpleValueReceiver<T> implements InsertionReceiver<T> {

  private static final Object DUMMY = new Object();

  private final Class<T> clazz;
  private final AtomicReference<Object> reference;

  SimpleValueReceiver(Class<T> clazz) {
    this.clazz = clazz;
    this.reference = new AtomicReference<>(DUMMY);
  }

  @Override
  public T receiveValue(BodyInserter<?, ? extends ReactiveHttpOutputMessage> bodyInserter) {
    demandValueFrom(bodyInserter);

    return receivedValue();
  }

  private void demandValueFrom(BodyInserter<?, ? extends ReactiveHttpOutputMessage> bodyInserter) {
    var inserter = (BodyInserter<?, ReactiveHttpOutputMessage>) bodyInserter;

    inserter.insert(
        MinimalHttpOutputMessage.INSTANCE,
        new SingleWriterContext(new WriteToConsumer<>(reference::set)));
  }

  private T receivedValue() {
    Object value = reference.get();
    reference.set(DUMMY);

    T validatedValue;

    if (value == DUMMY) {
      throw new RuntimeException("Value was not received, Check your inserter worked properly");
    } else if (!clazz.isAssignableFrom(value.getClass())) {
      throw new RuntimeException(
          "Value has unexpected type ("
              + value.getClass().getTypeName()
              + ") instead of ("
              + clazz.getTypeName()
              + ")");
    } else {
      validatedValue = clazz.cast(value);
    }

    return validatedValue;
  }
}
