package com.takypok.core.config;

import com.takypok.core.Constants;
import io.micrometer.context.ContextRegistry;
import io.micrometer.context.ContextSnapshotFactory;
import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Hooks;

@Configuration
@ConditionalOnClass({ContextRegistry.class, ContextSnapshotFactory.class})
public class MdcContextPropagationConfiguration {
  public MdcContextPropagationConfiguration() {
    registerMDC(Constants.X_REQUEST_ID);
    registerMDC(Constants.USER_ID);
    Hooks.enableAutomaticContextPropagation();
  }

  private void registerMDC(String key) {
    ContextRegistry.getInstance()
        .registerThreadLocalAccessor(
            key, () -> MDC.get(key), value -> MDC.put(key, value), () -> MDC.remove(key));
  }
}
