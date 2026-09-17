package com.takypok.contentservice.service.impl;

import com.takypok.contentservice.model.entity.Site;
import com.takypok.contentservice.repository.SiteRepository;
import com.takypok.contentservice.service.SiteService;
import com.takypok.core.exception.ApplicationException;
import com.takypok.core.model.Message;
import java.net.InetSocketAddress;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class SiteServiceImpl implements SiteService {
  private final SiteRepository siteRepository;

  @Override
  public Mono<Site> resolveByCode(String code) {
    return siteRepository
        .findByCode(code)
        .switchIfEmpty(
            Mono.error(
                new ApplicationException(Message.Application.ERROR, "Unknown site: " + code)));
  }

  @Override
  public Mono<Site> resolveFromHost(ServerWebExchange exchange) {
    InetSocketAddress host = exchange.getRequest().getHeaders().getHost();
    String hostName = host != null ? host.getHostString() : null;
    if (hostName == null) {
      return Mono.error(new ApplicationException(Message.Application.ERROR, "Missing Host header"));
    }
    return siteRepository
        .findBySubdomain(hostName)
        .switchIfEmpty(
            Mono.error(
                new ApplicationException(
                    Message.Application.ERROR, "Unknown site host: " + hostName)));
  }
}
