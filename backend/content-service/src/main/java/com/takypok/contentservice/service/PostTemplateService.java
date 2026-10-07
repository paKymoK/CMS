package com.takypok.contentservice.service;

import com.takypok.contentservice.model.entity.PostTemplate;
import com.takypok.contentservice.model.request.PostTemplateCreateRequest;
import java.util.List;
import reactor.core.publisher.Mono;

public interface PostTemplateService {
  Mono<List<PostTemplate>> getAll(Long siteId);

  Mono<PostTemplate> create(Long siteId, PostTemplateCreateRequest request);

  Mono<Void> delete(Long id, Long siteId);
}
