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
  // Visitors hitting a host that isn't one of the 5 registered regional subdomains (no
  // subdomain at all, a bare IP, localhost in local dev) get the 'en' site rather than a
  // hard error — a public marketing homepage degrades gracefully instead of 400ing.
  private static final String DEFAULT_SITE_CODE = "en";

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
      return resolveByCode(DEFAULT_SITE_CODE);
    }
    return siteRepository
        .findBySubdomain(hostName)
        .switchIfEmpty(Mono.defer(() -> resolveByCode(DEFAULT_SITE_CODE)));
  }
}
