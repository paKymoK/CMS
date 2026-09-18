package com.takypok.gatewayservice.config;

import io.netty.channel.ChannelOption;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Raises the inbound TCP accept queue so a burst of concurrent clients isn't reset before the
 * server gets a chance to accept them — default backlog is small enough to bite under load test
 * concurrency well below what the JVM/OS can otherwise handle.
 */
@Configuration
public class NettyServerConfig {

  @Bean
  public WebServerFactoryCustomizer<NettyReactiveWebServerFactory> nettyServerCustomizer() {
    return factory ->
        factory.addServerCustomizers(
            httpServer -> httpServer.option(ChannelOption.SO_BACKLOG, 2048));
  }
}
