package com.takypok.contentservice.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.takypok.contentservice.audit.ContentItemWriter;
import com.takypok.contentservice.model.entity.ContentItem;
import com.takypok.contentservice.model.mapper.ContentItemMapper;
import com.takypok.contentservice.model.request.ContentItemCreateRequest;
import com.takypok.contentservice.model.request.ContentItemUpdateRequest;
import com.takypok.contentservice.repository.ContentItemRepository;
import com.takypok.core.exception.ApplicationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class ContentItemServiceImplTest {
  @Mock private ContentItemRepository contentItemRepository;
  @Mock private ContentItemMapper contentItemMapper;
  @Mock private ContentItemWriter contentItemWriter;

  @Test
  void caseStudiesCannotBeCreatedThroughTheGenericPath() {
    ContentItemServiceImpl service =
        new ContentItemServiceImpl(contentItemRepository, contentItemMapper, contentItemWriter);
    ContentItemCreateRequest request = new ContentItemCreateRequest();
    request.setContentType("case_study");

    StepVerifier.create(service.create(1L, request))
        .expectError(ApplicationException.class)
        .verify();

    verify(contentItemWriter, never()).save(any(ContentItem.class));
  }

  @Test
  void postsCannotBeCreatedThroughTheGenericPath() {
    ContentItemServiceImpl service =
        new ContentItemServiceImpl(contentItemRepository, contentItemMapper, contentItemWriter);
    ContentItemCreateRequest request = new ContentItemCreateRequest();
    request.setContentType("post");

    StepVerifier.create(service.create(1L, request))
        .expectError(ApplicationException.class)
        .verify();

    verify(contentItemWriter, never()).save(any(ContentItem.class));
  }

  @Test
  void caseStudiesCannotBeUpdatedThroughTheGenericPath() {
    ContentItemServiceImpl service =
        new ContentItemServiceImpl(contentItemRepository, contentItemMapper, contentItemWriter);
    ContentItemUpdateRequest request = new ContentItemUpdateRequest();
    request.setId(1L);
    request.setContentType("case_study");

    StepVerifier.create(service.update(1L, request))
        .expectError(ApplicationException.class)
        .verify();

    verify(contentItemWriter, never()).save(any(ContentItem.class));
  }
}
