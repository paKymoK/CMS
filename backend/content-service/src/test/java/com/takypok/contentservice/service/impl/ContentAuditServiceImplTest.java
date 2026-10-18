package com.takypok.contentservice.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.takypok.contentservice.audit.ContentAuditEntry;
import com.takypok.contentservice.audit.ContentItemWriter;
import com.takypok.contentservice.model.entity.ContentItem;
import com.takypok.contentservice.model.mapper.ContentItemMapper;
import com.takypok.contentservice.repository.ContentAuditRepository;
import com.takypok.contentservice.repository.ContentItemRepository;
import com.takypok.core.exception.ApplicationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class ContentAuditServiceImplTest {
  private final ObjectMapper json = new ObjectMapper();

  @Mock private R2dbcEntityTemplate template;
  @Mock private ContentAuditRepository audits;
  @Mock private ContentItemRepository items;
  @Mock private ContentItemMapper mapper;
  @Mock private ContentItemWriter writer;

  private ContentAuditServiceImpl service;

  @BeforeEach
  void setUp() {
    service = new ContentAuditServiceImpl(template, audits, items, mapper, writer);
    // Validation is ContentItemMapperTest's job: pass the data through unchanged.
    lenient()
        .when(mapper.resolveAndValidate(any(), any()))
        .thenAnswer(invocation -> Mono.just((JsonNode) invocation.getArgument(1)));
  }

  private ContentAuditEntry entry(String action, String before, String after) throws Exception {
    ContentAuditEntry entry = new ContentAuditEntry();
    entry.setId(9L);
    entry.setSiteId(1L);
    entry.setItemId(5L);
    entry.setContentType("post");
    entry.setAction(action);
    entry.setBeforeData(before == null ? null : json.readTree(before));
    entry.setAfterData(after == null ? null : json.readTree(after));
    return entry;
  }

  private ContentItem item(String status, String data) throws Exception {
    ContentItem item = new ContentItem();
    item.setId(5L);
    item.setSiteId(1L);
    item.setContentType("post");
    item.setData(json.readTree(data));
    item.setStatus(status);
    item.setActive(false);
    item.setDisplayOrder(7);
    return item;
  }

  @Test
  void anUnknownEntryCannotBeRestored() {
    when(audits.findByIdAndSiteId(9L, 1L)).thenReturn(Mono.empty());

    StepVerifier.create(service.restore(1L, 9L)).expectError(ApplicationException.class).verify();

    verify(writer, never()).restore(any());
  }

  @Test
  void aDeleteEntryHasNothingToRestoreInPlace() throws Exception {
    String last =
        "{\"contentType\":\"post\",\"data\":{\"title\":\"Gone\"},\"status\":\"DRAFT\",\"active\":true,\"displayOrder\":0}";
    when(audits.findByIdAndSiteId(9L, 1L)).thenReturn(Mono.just(entry("DELETE", last, null)));

    StepVerifier.create(service.restore(1L, 9L))
        .expectErrorSatisfies(
            error ->
                assertTrue(error.getMessage().contains("create a new item"), error.getMessage()))
        .verify();

    verify(writer, never()).restore(any());
  }

  @Test
  void anItemThatHasSinceBeenDeletedIsNotRecreatedByARestore() throws Exception {
    String after =
        "{\"contentType\":\"post\",\"data\":{\"title\":\"Old\"},\"status\":\"DRAFT\",\"active\":true,\"displayOrder\":0}";
    when(audits.findByIdAndSiteId(9L, 1L)).thenReturn(Mono.just(entry("UPDATE", null, after)));
    when(items.findByIdAndSiteId(5L, 1L)).thenReturn(Mono.empty());

    StepVerifier.create(service.restore(1L, 9L))
        .expectErrorSatisfies(
            error -> assertTrue(error.getMessage().contains("deleted"), error.getMessage()))
        .verify();

    verify(writer, never()).restore(any());
  }

  @Test
  void restoringPutsTheOldContentBackButKeepsTheCurrentStatus() throws Exception {
    String after =
        "{\"contentType\":\"post\",\"data\":{\"title\":\"Old title\"},\"status\":\"DRAFT\",\"active\":true,\"displayOrder\":2}";
    when(audits.findByIdAndSiteId(9L, 1L)).thenReturn(Mono.just(entry("UPDATE", null, after)));
    ContentItem live = item("PUBLISHED", "{\"title\":\"New title\"}");
    when(items.findByIdAndSiteId(5L, 1L)).thenReturn(Mono.just(live));
    when(writer.restore(live)).thenReturn(Mono.just(live));

    StepVerifier.create(service.restore(1L, 9L))
        .assertNext(
            restored -> {
              assertEquals(5L, restored.itemId());
              assertEquals("post", restored.contentType());
            })
        .verifyComplete();

    assertEquals("Old title", live.getData().path("title").asText());
    assertEquals(2, live.getDisplayOrder());
    assertTrue(live.getActive());
    assertEquals("PUBLISHED", live.getStatus(), "a restore must not change whether it is live");
  }
}
