package com.takypok.contentservice.service.impl;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.takypok.contentservice.model.entity.PostTemplate;
import com.takypok.contentservice.model.request.PostTemplateCreateRequest;
import com.takypok.contentservice.repository.PostTemplateRepository;
import com.takypok.contentservice.service.PostTemplateService;
import com.takypok.contentservice.util.RichTextSanitizer;
import com.takypok.core.exception.ApplicationException;
import com.takypok.core.model.Message;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class PostTemplateServiceImpl implements PostTemplateService {
  private final PostTemplateRepository postTemplateRepository;

  @Override
  public Mono<List<PostTemplate>> getAll(Long siteId) {
    return postTemplateRepository.findAllBySiteIdOrderByNameAsc(siteId).collectList();
  }

  @Override
  public Mono<PostTemplate> create(Long siteId, PostTemplateCreateRequest request) {
    // defer: the sanitizer and layout check throw, and that must surface as a Mono error
    return Mono.defer(
        () -> {
          PostTemplate template = new PostTemplate();
          template.setSiteId(siteId);
          template.setName(request.getName().trim());
          template.setDescription(request.getDescription());
          template.setLayout(PostServiceImpl.validLayout(request.getLayout()));
          template.setCategory(request.getCategory());
          template.setTags(
              request.getTags() != null ? request.getTags() : JsonNodeFactory.instance.arrayNode());
          template.setBody(RichTextSanitizer.sanitizeOrReject(request.getBody()));
          return postTemplateRepository.save(template);
        });
  }

  @Override
  public Mono<Void> delete(Long id, Long siteId) {
    return postTemplateRepository
        .findByIdAndSiteId(id, siteId)
        .switchIfEmpty(
            Mono.error(new ApplicationException(Message.Application.ERROR, "Template not found")))
        .flatMap(postTemplateRepository::delete);
  }
}
