package com.takypok.contentservice.service;

import com.takypok.contentservice.model.entity.BlockLibraryItem;
import com.takypok.contentservice.model.request.BlockLibraryCreateRequest;
import java.util.List;
import reactor.core.publisher.Mono;

public interface BlockLibraryService {
  Mono<List<BlockLibraryItem>> getAll(Long siteId, String kind);

  Mono<BlockLibraryItem> create(Long siteId, BlockLibraryCreateRequest request);

  Mono<Void> delete(Long id, Long siteId);
}
