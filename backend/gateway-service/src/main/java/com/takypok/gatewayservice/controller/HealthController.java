package com.takypok.gatewayservice.controller;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class HealthController {

  private static final List<String> DEFAULT_MONITORED_SERVICES =
      List.of("auth-service", "content-service", "media-service", "chat-service");

  private final ReactorLoadBalancerExchangeFilterFunction loadBalancerFilter;

  private WebClient buildClient(String serviceId) {
    return WebClient.builder().filter(loadBalancerFilter).baseUrl("http://" + serviceId).build();
  }

  @GetMapping("/health/{serviceName}")
  public Mono<ResponseEntity<Map<String, Object>>> checkService(@PathVariable String serviceName) {
    return doHealthCheck(serviceName)
        .map(
            result -> {
              boolean isUp = "UP".equals(result.get("status"));
              return isUp
                  ? ResponseEntity.ok(result)
                  : ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(result);
            });
  }

  @GetMapping("/health")
  public Mono<ResponseEntity<Map<String, Object>>> checkAllServices(
      @RequestParam(required = false) String monitors) {
    List<String> services =
        (monitors != null && !monitors.isBlank())
            ? Arrays.stream(monitors.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList()
            : DEFAULT_MONITORED_SERVICES;
    log.info("Checking services: {}", services);
    return Flux.fromIterable(services)
        .flatMap(this::doHealthCheck)
        .collectList()
        .map(
            results -> {
              boolean allUp = results.stream().allMatch(r -> "UP".equals(r.get("status")));

              Map<String, Object> response = new LinkedHashMap<>();
              response.put("overall", allUp ? "Up" : "Degraded");
              response.put("services", results);

              return allUp
                  ? ResponseEntity.ok(response)
                  : ResponseEntity.status(HttpStatus.MULTI_STATUS).body(response);
            });
  }

  // Shared health check logic
  private Mono<Map<String, Object>> doHealthCheck(String serviceName) {
    return buildClient(serviceName)
        .get()
        .uri("/actuator/health")
        .retrieve()
        .onStatus(
            status -> !status.is2xxSuccessful(),
            response -> Mono.error(new RuntimeException("HTTP " + response.statusCode().value())))
        .bodyToMono(Map.class)
        .switchIfEmpty(Mono.error(new RuntimeException("Empty response")))
        .map(
            body -> {
              Map<String, Object> result = new LinkedHashMap<>();
              result.put("service", serviceName);
              result.put("status", body.get("status"));
              return result;
            })
        .onErrorResume(
            ex -> {
              Map<String, Object> result = new LinkedHashMap<>();
              result.put("service", serviceName);
              result.put("status", "DOWN");
              result.put("reason", ex.getMessage());
              return Mono.just(result);
            });
  }
}
