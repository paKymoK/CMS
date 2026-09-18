package com.takypok.gatewayservice.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.StringUtils;

public class ReactorNettyIoWorkerCountEnvironmentPostProcessor implements EnvironmentPostProcessor {

  @Override
  public void postProcessEnvironment(
      ConfigurableEnvironment environment, SpringApplication application) {
    String ioWorkerCount = environment.getProperty("gateway.reactor.io-worker-count");
    if (StringUtils.hasText(ioWorkerCount)) {
      System.setProperty("reactor.netty.ioWorkerCount", ioWorkerCount);
    }
  }
}
