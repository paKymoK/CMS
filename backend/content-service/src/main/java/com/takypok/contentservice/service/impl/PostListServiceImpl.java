package com.takypok.contentservice.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.takypok.contentservice.model.response.PostListResponses;
import com.takypok.contentservice.repository.PostListRepository;
import com.takypok.contentservice.service.PostListService;
import com.takypok.core.exception.ApplicationException;
import com.takypok.core.model.Message;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PostListServiceImpl implements PostListService {

  static final int DEFAULT_SIZE = 20;
  static final int MAX_SIZE = 100;
  private static final int MAX_QUERY = 200;
  private static final List<String> STATUSES =
      List.of("DRAFT", "PUBLISHED", "SCHEDULED", "ARCHIVED");

  private final PostListRepository repository;
  private final ObjectMapper objectMapper;

  @Override
  public Mono<PostListResponses.Page> page(
      Long siteId,
      String q,
      String status,
      String category,
      Integer year,
      Integer month,
      String sort,
      Integer page,
      Integer size) {
    return Mono.defer(
        () -> {
          String statusValue = blankToNull(status);
          if (statusValue != null && !STATUSES.contains(statusValue)) {
            throw invalid("Unknown status: " + statusValue);
          }
          if (month != null && (month < 1 || month > 12)) {
            throw invalid("The month must be 1 to 12");
          }
          if (month != null && year == null) {
            throw invalid("Choose a year with the month");
          }
          if (year != null && (year < 1970 || year > 2200)) {
            throw invalid("The year is not valid");
          }
          String query = blankToNull(q);
          if (query != null && query.length() > MAX_QUERY) {
            query = query.substring(0, MAX_QUERY);
          }
          int safePage = page == null ? 1 : Math.max(page, 1);
          int safeSize = size == null ? DEFAULT_SIZE : Math.min(Math.max(size, 1), MAX_SIZE);
          PostListRepository.Filter filter =
              new PostListRepository.Filter(query, statusValue, blankToNull(category), year, month);
          return repository
              .page(siteId, filter, parseSort(sort), safePage, safeSize)
              .map(
                  result ->
                      new PostListResponses.Page(
                          result.rows().stream().map(this::toRow).toList(),
                          safePage,
                          safeSize,
                          result.total(),
                          (int) ((result.total() + safeSize - 1) / safeSize)));
        });
  }

  @Override
  public Mono<PostListResponses.Facets> facets(Long siteId) {
    return Mono.zip(
            repository.statusCounts(siteId),
            repository.categories(siteId),
            repository.years(siteId))
        .map(
            t -> {
              Map<String, Long> counts = new LinkedHashMap<>();
              STATUSES.forEach(s -> counts.put(s, t.getT1().getOrDefault(s, 0L)));
              return new PostListResponses.Facets(
                  counts,
                  t.getT2().stream()
                      .map(c -> new PostListResponses.CategoryCount(c.name(), c.count()))
                      .toList(),
                  t.getT3());
            });
  }

  static PostListRepository.Sort parseSort(String sort) {
    if (sort == null || sort.isBlank()) return PostListRepository.Sort.UPDATED_DESC;
    try {
      return PostListRepository.Sort.valueOf(sort.trim().toUpperCase());
    } catch (IllegalArgumentException ex) {
      throw invalid("Unknown sort: " + sort);
    }
  }

  private PostListResponses.Row toRow(PostListRepository.Row r) {
    return new PostListResponses.Row(
        r.id(),
        r.title(),
        r.slug(),
        r.category(),
        tagsOf(r.tagsJson()),
        r.authorName(),
        r.status(),
        r.publishAt() == null ? null : r.publishAt().toInstant(),
        r.updatedAt() == null ? null : r.updatedAt().toInstant(),
        r.updatedBy(),
        r.version());
  }

  private List<String> tagsOf(String json) {
    if (json == null || json.isBlank()) return List.of();
    try {
      JsonNode node = objectMapper.readTree(json);
      if (!node.isArray()) return List.of();
      List<String> tags = new ArrayList<>();
      node.forEach(
          t -> {
            if (t.isTextual() && !t.asText().isBlank()) tags.add(t.asText());
          });
      return tags;
    } catch (Exception ex) {
      return List.of();
    }
  }

  private static String blankToNull(String s) {
    return s == null || s.isBlank() ? null : s.trim();
  }

  private static ApplicationException invalid(String message) {
    return new ApplicationException(Message.Application.ERROR, message);
  }
}
