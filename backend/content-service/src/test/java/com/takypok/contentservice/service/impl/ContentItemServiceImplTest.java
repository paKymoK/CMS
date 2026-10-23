package com.takypok.contentservice.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import org.springframework.dao.OptimisticLockingFailureException;
import reactor.core.publisher.Mono;
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

  @Test
  void updatingFromAnOutOfDateCopyIsRejectedAndNothingIsSaved() {
    ContentItemServiceImpl service =
        new ContentItemServiceImpl(contentItemRepository, contentItemMapper, contentItemWriter);
    ContentItem current = new ContentItem();
    current.setId(1L);
    current.setContentType("service_page");
    current.setVersion(4);
    when(contentItemRepository.findByIdAndSiteId(1L, 1L)).thenReturn(Mono.just(current));
    ContentItemUpdateRequest request = new ContentItemUpdateRequest();
    request.setId(1L);
    request.setContentType("service_page");
    request.setVersion(3);

    StepVerifier.create(service.update(1L, request))
        .expectError(OptimisticLockingFailureException.class)
        .verify();

    verify(contentItemWriter, never()).save(any(ContentItem.class));
  }

  @Test
  void twoIndustryPagesCannotShareASlug() {
    ContentItemServiceImpl service =
        new ContentItemServiceImpl(contentItemRepository, contentItemMapper, contentItemWriter);
    com.fasterxml.jackson.databind.node.ObjectNode data =
        new com.fasterxml.jackson.databind.ObjectMapper().createObjectNode().put("slug", "bfsi");
    ContentItem existing = new ContentItem();
    existing.setId(5L);
    existing.setData(data);
    when(contentItemMapper.resolveAndValidate("industry_page", data)).thenReturn(Mono.just(data));
    when(contentItemRepository.findAllBySiteIdAndContentTypeOrderByDisplayOrderAsc(
            1L, "industry_page"))
        .thenReturn(reactor.core.publisher.Flux.just(existing));
    ContentItemCreateRequest request = new ContentItemCreateRequest();
    request.setContentType("industry_page");
    request.setData(data);

    StepVerifier.create(service.create(1L, request))
        .expectErrorMatches(
            e -> e instanceof ApplicationException && e.getMessage().contains("another industry"))
        .verify();

    verify(contentItemWriter, never()).save(any(ContentItem.class));
  }

  private ContentItem aboutPage(long id) {
    ContentItem item = new ContentItem();
    item.setId(id);
    item.setContentType("about_page");
    return item;
  }

  @Test
  void aSiteCannotGetASecondAboutPage() {
    ContentItemServiceImpl service =
        new ContentItemServiceImpl(contentItemRepository, contentItemMapper, contentItemWriter);
    com.fasterxml.jackson.databind.node.ObjectNode data =
        new com.fasterxml.jackson.databind.ObjectMapper().createObjectNode().put("heroTitle", "H");
    when(contentItemMapper.resolveAndValidate("about_page", data)).thenReturn(Mono.just(data));
    when(contentItemRepository.findAllBySiteIdAndContentTypeOrderByDisplayOrderAsc(
            1L, "about_page"))
        .thenReturn(reactor.core.publisher.Flux.just(aboutPage(5L)));
    ContentItemCreateRequest request = new ContentItemCreateRequest();
    request.setContentType("about_page");
    request.setData(data);

    StepVerifier.create(service.create(1L, request))
        .expectErrorMatches(
            e -> e instanceof ApplicationException && e.getMessage().contains("already has its"))
        .verify();

    verify(contentItemWriter, never()).save(any(ContentItem.class));
  }

  @Test
  void aSiteWithoutAnAboutPageMayCreateOne() {
    ContentItemServiceImpl service =
        new ContentItemServiceImpl(contentItemRepository, contentItemMapper, contentItemWriter);
    com.fasterxml.jackson.databind.node.ObjectNode data =
        new com.fasterxml.jackson.databind.ObjectMapper().createObjectNode().put("heroTitle", "H");
    when(contentItemMapper.resolveAndValidate("about_page", data)).thenReturn(Mono.just(data));
    when(contentItemRepository.findAllBySiteIdAndContentTypeOrderByDisplayOrderAsc(
            1L, "about_page"))
        .thenReturn(reactor.core.publisher.Flux.empty());
    when(contentItemWriter.save(any(ContentItem.class)))
        .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
    ContentItemCreateRequest request = new ContentItemCreateRequest();
    request.setContentType("about_page");
    request.setData(data);

    StepVerifier.create(service.create(1L, request))
        .assertNext(
            item ->
                org.junit.jupiter.api.Assertions.assertEquals("about_page", item.getContentType()))
        .verifyComplete();
  }

  @Test
  void anotherItemCannotBeTurnedIntoASecondAboutPage() {
    ContentItemServiceImpl service =
        new ContentItemServiceImpl(contentItemRepository, contentItemMapper, contentItemWriter);
    com.fasterxml.jackson.databind.node.ObjectNode data =
        new com.fasterxml.jackson.databind.ObjectMapper().createObjectNode().put("heroTitle", "H");
    ContentItem other = new ContentItem();
    other.setId(9L);
    other.setContentType("faq");
    when(contentItemRepository.findByIdAndSiteId(9L, 1L)).thenReturn(Mono.just(other));
    when(contentItemMapper.resolveAndValidate("about_page", data)).thenReturn(Mono.just(data));
    when(contentItemRepository.findAllBySiteIdAndContentTypeOrderByDisplayOrderAsc(
            1L, "about_page"))
        .thenReturn(reactor.core.publisher.Flux.just(aboutPage(5L)));
    ContentItemUpdateRequest request = new ContentItemUpdateRequest();
    request.setId(9L);
    request.setContentType("about_page");
    request.setData(data);

    StepVerifier.create(service.update(1L, request))
        .expectErrorMatches(
            e -> e instanceof ApplicationException && e.getMessage().contains("already has its"))
        .verify();

    verify(contentItemWriter, never()).save(any(ContentItem.class));
  }

  @Test
  void aGenericContentTypeCannotBeArchivedOrScheduled() {
    ContentItemServiceImpl service =
        new ContentItemServiceImpl(contentItemRepository, contentItemMapper, contentItemWriter);
    ContentItemCreateRequest create = new ContentItemCreateRequest();
    create.setContentType("service_page");
    create.setStatus("ARCHIVED");
    StepVerifier.create(service.create(1L, create))
        .expectError(ApplicationException.class)
        .verify();

    ContentItemUpdateRequest update = new ContentItemUpdateRequest();
    update.setId(1L);
    update.setContentType("service_page");
    update.setStatus("SCHEDULED");
    StepVerifier.create(service.update(1L, update))
        .expectError(ApplicationException.class)
        .verify();

    verify(contentItemWriter, never()).save(any(ContentItem.class));
  }
}
