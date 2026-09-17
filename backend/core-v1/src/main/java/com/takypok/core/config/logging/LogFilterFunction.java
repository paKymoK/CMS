package com.takypok.core.config.logging;

import static com.takypok.core.util.LogUtil.logObject;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.event.Level;
import org.springframework.lang.NonNull;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Slf4j
public class LogFilterFunction implements ExchangeFilterFunction {

  @NonNull
  @Override
  public Mono<ClientResponse> filter(
      @NonNull ClientRequest request, @NonNull ExchangeFunction next) {
    String prefix = request.logPrefix();
    try {
      logObject("[Api Call] " + prefix, request.method() + " " + request.url());
      logObject("[Headers] " + prefix, request.headers(), Level.DEBUG);
      logObject("[Request Body] " + prefix, extractBody(request, Object.class));
    } catch (Exception ignored) {
    }
    return next.exchange(request)
        .doOnNext(
            response -> {
              if (response.statusCode().is2xxSuccessful()) {
                logObject("[Response Status] " + prefix, String.valueOf(response.statusCode()));
                logObject("[Hash connector] " + prefix, response.logPrefix().trim());
              } else {
                logObject(
                    "[Response Status] " + prefix,
                    String.valueOf(response.statusCode()),
                    Level.ERROR);
                logObject("[Hash connector]" + prefix, response.logPrefix().trim(), Level.ERROR);
              }
            });
  }

  <T> T extractBody(ClientRequest request, Class<T> clazz) {
    InsertionReceiver<T> receiver = InsertionReceiver.forClass(clazz);
    return receiver.receiveValue(request.body());
  }
}
