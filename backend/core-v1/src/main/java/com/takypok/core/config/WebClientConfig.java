package com.takypok.core.config;

import static java.lang.Math.toIntExact;
import static java.time.Duration.ofSeconds;
import static java.util.Objects.requireNonNullElse;
import static java.util.concurrent.TimeUnit.SECONDS;

import com.takypok.core.config.logging.LogFilterFunction;
import com.takypok.core.config.logging.LoggingClientHttpConnector;
import io.netty.channel.ChannelOption;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import jakarta.annotation.PostConstruct;
import java.util.Objects;
import javax.net.ssl.SSLException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class WebClientConfig {
  private static final Integer DEFAULT_CONNECTION_TIMEOUT = 180;
  private static final Integer DEFAULT_READ_TIMEOUT = 300;
  private final WebClient.Builder webClientBuilder;

  @PostConstruct
  private void initWebClient() {
    webClientBuilder.filter(new LogFilterFunction());
  }

  private WebClient createStandardWebClient(
      String baseUrl, Integer connectionTimeOut, Integer readTimeOut) throws SSLException {

    return webClientBuilder
        .clientConnector(
            connector(
                requireNonNullElse(connectionTimeOut, DEFAULT_CONNECTION_TIMEOUT),
                requireNonNullElse(readTimeOut, DEFAULT_READ_TIMEOUT)))
        .baseUrl(baseUrl)
        .build();
  }

  public ClientHttpConnector connector(
      Integer optionalConnectionTimeOut, Integer optionalReadTimeOut) throws SSLException {
    final int connectionTimeOut = Objects.requireNonNullElse(optionalConnectionTimeOut, 5);
    final int readTimeOut = Objects.requireNonNullElse(optionalReadTimeOut, 10);
    SslContext sslContext =
        SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();

    final HttpClient httpClient =
        HttpClient.create()
            .secure(t -> t.sslContext(sslContext))
            .option(
                ChannelOption.CONNECT_TIMEOUT_MILLIS,
                toIntExact(ofSeconds(connectionTimeOut).toMillis()))
            .responseTimeout(ofSeconds(readTimeOut))
            .doOnConnected(
                conn ->
                    conn.addHandlerLast(new ReadTimeoutHandler(readTimeOut, SECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(readTimeOut, SECONDS)));

    return new LoggingClientHttpConnector(new ReactorClientHttpConnector(httpClient));
  }

  // Test WebClient
  @Bean
  public WebClient testWebClient() throws SSLException {
    return createStandardWebClient("test", 10, 10);
  }
}
