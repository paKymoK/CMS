package com.takypok.contentservice.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.takypok.contentservice.model.entity.BlockLibraryItem;
import com.takypok.contentservice.model.request.BlockLibraryCreateRequest;
import com.takypok.contentservice.repository.BlockLibraryRepository;
import com.takypok.contentservice.service.BlockLibraryService;
import com.takypok.core.exception.ApplicationException;
import com.takypok.core.model.Message;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class BlockLibraryServiceImpl implements BlockLibraryService {
  private final BlockLibraryRepository blockLibraryRepository;
  private final ObjectMapper objectMapper;

  @Override
  public Mono<List<BlockLibraryItem>> getAll(Long siteId, String kind) {
    return blockLibraryRepository.findAllBySiteIdAndKindOrderByNameAsc(siteId, kind).collectList();
  }

  @Override
  public Mono<BlockLibraryItem> create(Long siteId, BlockLibraryCreateRequest request) {
    BlockLibraryItem item = new BlockLibraryItem();
    item.setSiteId(siteId);
    item.setKind(request.getKind());
    item.setName(request.getName().trim());
    item.setDescription(request.getDescription());
    item.setBlocks(withoutIds(objectMapper.valueToTree(request.getBlocks())));
    return blockLibraryRepository.save(item);
  }

  /**
   * The request's blocks went through the typed model (unknown props are gone); the ids belong to
   * the page they came from.
   */
  private static JsonNode withoutIds(JsonNode blocks) {
    ArrayNode out = ((ArrayNode) blocks).deepCopy();
    out.forEach(b -> ((ObjectNode) b).remove("id"));
    return out;
  }

  @Override
  public Mono<Void> delete(Long id, Long siteId) {
    return blockLibraryRepository
        .findByIdAndSiteId(id, siteId)
        .switchIfEmpty(
            Mono.error(new ApplicationException(Message.Application.ERROR, "Item not found")))
        .flatMap(blockLibraryRepository::delete);
  }
}
