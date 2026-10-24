package com.takypok.contentservice.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.takypok.contentservice.audit.AuditAction;
import com.takypok.contentservice.audit.ContentAuditEntry;
import com.takypok.contentservice.audit.ContentItemWriter;
import com.takypok.contentservice.model.mapper.ContentItemMapper;
import com.takypok.contentservice.model.response.AuditResponses;
import com.takypok.contentservice.repository.ContentAuditRepository;
import com.takypok.contentservice.repository.ContentItemRepository;
import com.takypok.contentservice.search.SearchQuery;
import com.takypok.contentservice.service.ContentAuditService;
import com.takypok.core.exception.ApplicationException;
import com.takypok.core.model.Message;
import java.time.ZoneId;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ContentAuditServiceImpl implements ContentAuditService {
  private static final int MAX_PAGE_SIZE = 100;

  /** The list view never loads the (large) snapshots; the detail view does. */
  private static final String[] SUMMARY_COLUMNS = {
    "id",
    "siteId",
    "itemId",
    "contentType",
    "action",
    "title",
    "actor",
    "actorName",
    "occurredAt",
    "changed"
  };

  private final R2dbcEntityTemplate template;
  private final ContentAuditRepository audits;
  private final ContentItemRepository items;
  private final ContentItemMapper mapper;
  private final ContentItemWriter writer;

  @Override
  public Mono<AuditResponses.Page> list(Long siteId, Filter filter, int page, int size) {
    int pageSize = Math.min(Math.max(1, size), MAX_PAGE_SIZE);
    Criteria criteria;
    try {
      criteria = criteria(siteId, filter);
    } catch (IllegalArgumentException e) {
      return Mono.error(new ApplicationException(Message.Application.ERROR, e.getMessage()));
    }
    Mono<Long> total = template.count(Query.query(criteria), ContentAuditEntry.class);
    return total.flatMap(
        count -> {
          int totalPages = Math.max(1, (int) Math.ceil(count / (double) pageSize));
          int current = Math.min(Math.max(1, page), totalPages);
          Query query =
              Query.query(criteria)
                  .columns(SUMMARY_COLUMNS)
                  .sort(Sort.by(Sort.Order.desc("occurredAt"), Sort.Order.desc("id")))
                  .limit(pageSize)
                  .offset((long) (current - 1) * pageSize);
          return template
              .select(ContentAuditEntry.class)
              .matching(query)
              .all()
              .map(AuditResponses.Entry::from)
              .collectList()
              .map(
                  entries ->
                      new AuditResponses.Page(entries, current, pageSize, count, totalPages));
        });
  }

  private static Criteria criteria(Long siteId, Filter filter) {
    Criteria criteria = Criteria.where("siteId").is(siteId);
    if (filter.itemId() != null) {
      criteria = criteria.and("itemId").is(filter.itemId());
    }
    if (filter.contentType() != null && !filter.contentType().isBlank()) {
      criteria = criteria.and("contentType").is(filter.contentType().trim());
    }
    if (filter.action() != null && !filter.action().isBlank()) {
      criteria = criteria.and("action").is(parseAction(filter.action()).name());
    }
    if (filter.actor() != null && !filter.actor().isBlank()) {
      criteria =
          criteria
              .and("actorName")
              .like("%" + SearchQuery.likeEscape(filter.actor().trim()) + "%")
              .ignoreCase(true);
    }
    ZoneId zone = ZoneId.systemDefault();
    if (filter.from() != null) {
      criteria =
          criteria
              .and("occurredAt")
              .greaterThanOrEquals(filter.from().atStartOfDay(zone).toOffsetDateTime());
    }
    if (filter.to() != null) {
      criteria =
          criteria
              .and("occurredAt")
              .lessThan(filter.to().plusDays(1).atStartOfDay(zone).toOffsetDateTime());
    }
    return criteria;
  }

  private static AuditAction parseAction(String action) {
    try {
      return AuditAction.valueOf(action.trim().toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException(
          "Unknown action \""
              + action
              + "\" — use one of "
              + Arrays.toString(AuditAction.values()));
    }
  }

  @Override
  public Mono<AuditResponses.Detail> get(Long siteId, Long entryId) {
    return find(siteId, entryId).map(AuditResponses.Detail::from);
  }

  private Mono<ContentAuditEntry> find(Long siteId, Long entryId) {
    return audits
        .findByIdAndSiteId(entryId, siteId)
        .switchIfEmpty(
            Mono.error(
                new ApplicationException(Message.Application.ERROR, "Audit entry not found")));
  }

  @Override
  public Mono<AuditResponses.Restored> restore(Long siteId, Long entryId) {
    return find(siteId, entryId)
        .flatMap(
            entry -> {
              JsonNode target = entry.getAfterData();
              if (target == null || !target.has("data")) {
                return Mono.error(
                    new ApplicationException(
                        Message.Application.ERROR,
                        "This entry has no content to restore. A deleted item is not brought back —"
                            + " create a new item from its last version instead."));
              }
              String contentType = target.path("contentType").asText(entry.getContentType());
              return items
                  .findByIdAndSiteId(entry.getItemId(), siteId)
                  .switchIfEmpty(
                      Mono.error(
                          new ApplicationException(
                              Message.Application.ERROR,
                              "This item has been deleted, so it can't be restored in place —"
                                  + " create a new item from this version instead.")))
                  .flatMap(
                      existing ->
                          mapper
                              .resolveAndValidate(contentType, target.get("data"))
                              .flatMap(
                                  data -> {
                                    // The content comes back; whether it is published stays as now.
                                    existing.setData(data);
                                    existing.setDisplayOrder(target.path("displayOrder").asInt(0));
                                    return writer.restore(existing);
                                  }))
                  .map(saved -> new AuditResponses.Restored(saved.getId(), contentType));
            });
  }
}
