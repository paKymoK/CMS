package com.takypok.contentservice.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.takypok.contentservice.model.entity.BlockLibraryItem;
import com.takypok.contentservice.model.request.BlockLibraryCreateRequest;
import com.takypok.contentservice.repository.BlockLibraryRepository;
import com.takypok.core.config.ConfigObjectMapper;
import com.takypok.core.exception.ApplicationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class BlockLibraryServiceImplTest {
  private final ObjectMapper objectMapper = ConfigObjectMapper.objectMapper();
  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @Mock private BlockLibraryRepository repository;

  private BlockLibraryCreateRequest request(String json) throws Exception {
    return objectMapper.readValue(json, BlockLibraryCreateRequest.class);
  }

  @Test
  void aSavedEntryKeepsItsBlocksWithoutTheirIds() throws Exception {
    BlockLibraryServiceImpl service = new BlockLibraryServiceImpl(repository, objectMapper);
    when(repository.save(any(BlockLibraryItem.class)))
        .thenAnswer(inv -> Mono.just((BlockLibraryItem) inv.getArgument(0)));
    BlockLibraryCreateRequest request =
        request(
            "{\"kind\":\"BLOCK\",\"name\":\"  Our process \","
                + "\"blocks\":[{\"id\":\"b1\",\"type\":\"process\",\"props\":{\"title\":\"How\"}}]}");

    StepVerifier.create(service.create(3L, request))
        .assertNext(
            item -> {
              assertEquals("Our process", item.getName());
              assertEquals(3L, item.getSiteId());
              assertEquals("process", item.getBlocks().get(0).get("type").asText());
              assertEquals("How", item.getBlocks().get(0).at("/props/title").asText());
              assertFalse(item.getBlocks().get(0).has("id"));
            })
        .verifyComplete();
  }

  @Test
  void theRequestIsCheckedLikeAPage() throws Exception {
    // a service block and an industry block can be saved together
    assertTrue(
        validator
            .validate(
                request(
                    "{\"kind\":\"PAGE\",\"name\":\"n\",\"blocks\":["
                        + "{\"id\":\"a\",\"type\":\"process\",\"props\":{}},"
                        + "{\"id\":\"b\",\"type\":\"solutions\",\"props\":{}}]}"))
            .isEmpty());
    // an About section can be saved too
    assertTrue(
        validator
            .validate(
                request(
                    "{\"kind\":\"PAGE\",\"name\":\"n\",\"blocks\":["
                        + "{\"id\":\"a\",\"type\":\"values\",\"props\":{}}]}"))
            .isEmpty());
    // a single-use block twice
    assertFalse(
        validator
            .validate(
                request(
                    "{\"kind\":\"PAGE\",\"name\":\"n\",\"blocks\":["
                        + "{\"id\":\"a\",\"type\":\"highlights\",\"props\":{}},"
                        + "{\"id\":\"b\",\"type\":\"highlights\",\"props\":{}}]}"))
            .isEmpty());
    // a block entry holds exactly one block
    assertFalse(
        validator
            .validate(
                request(
                    "{\"kind\":\"BLOCK\",\"name\":\"n\",\"blocks\":["
                        + "{\"id\":\"a\",\"type\":\"process\",\"props\":{}},"
                        + "{\"id\":\"b\",\"type\":\"process\",\"props\":{}}]}"))
            .isEmpty());
    // nothing to save
    assertFalse(
        validator.validate(request("{\"kind\":\"PAGE\",\"name\":\"n\",\"blocks\":[]}")).isEmpty());
  }

  @Test
  void deleteNeverTouchesAnotherSitesEntry() {
    BlockLibraryServiceImpl service = new BlockLibraryServiceImpl(repository, objectMapper);
    when(repository.findByIdAndSiteId(5L, 2L)).thenReturn(Mono.empty());

    StepVerifier.create(service.delete(5L, 2L)).expectError(ApplicationException.class).verify();
    verify(repository, never()).delete(any());
  }
}
