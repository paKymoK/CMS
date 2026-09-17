package com.takypok.core.config.logging;

import static com.takypok.core.util.LogUtil.getRequestId;
import static com.takypok.core.util.LogUtil.logString;
import static java.lang.StackWalker.Option.RETAIN_CLASS_REFERENCE;

import com.takypok.core.Constants;
import java.nio.charset.StandardCharsets;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.event.Level;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.client.reactive.ClientHttpResponse;
import org.springframework.http.client.reactive.ClientHttpResponseDecorator;
import reactor.core.publisher.Flux;

@Slf4j
public class LoggingClientHttpResponse extends ClientHttpResponseDecorator {

  private static final DataBufferFactory bufferFactory = new DefaultDataBufferFactory();
  private final DataBuffer buffer = bufferFactory.allocateBuffer();

  public LoggingClientHttpResponse(ClientHttpResponse delegate) {
    super(delegate);
  }

  @NonNull
  @Override
  public Flux<DataBuffer> getBody() {
    StackWalker walker = StackWalker.getInstance(RETAIN_CLASS_REFERENCE);
    String CLAZZ_CALLER = "org.springframework.http.codec.DecoderHttpMessageReader";
    boolean isLogging = CLAZZ_CALLER.equals(walker.getCallerClass().getName());
    return super.getBody()
        .contextWrite(ctx -> ctx.put(Constants.X_REQUEST_ID, getRequestId()))
        .doOnNext(this.buffer::write)
        .doOnComplete(
            () -> {
              if (isLogging) {
                String prefix = "[" + getDelegate().getId() + "] ";
                String bodyStr = buffer.toString(StandardCharsets.UTF_8);
                if (getDelegate().getStatusCode().is2xxSuccessful()) {
                  logString(
                      "[Response Body] " + prefix,
                      bodyStr.replaceAll("\n", "").replaceAll("\r", ""));
                } else {
                  logString(
                      "[Response Body] " + prefix,
                      bodyStr.replaceAll("\n", "").replaceAll("\r", ""),
                      Level.ERROR);
                }
              }
            });
  }
}
