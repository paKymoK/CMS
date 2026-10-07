package com.takypok.contentservice.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.takypok.contentservice.model.entity.PostTemplate;
import com.takypok.contentservice.model.request.PostTemplateCreateRequest;
import com.takypok.contentservice.repository.PostTemplateRepository;
import com.takypok.core.exception.ApplicationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class PostTemplateServiceImplTest {

  @Mock private PostTemplateRepository repository;

  @Test
  void deleteNeverTouchesAnotherSitesTemplate() {
    PostTemplateServiceImpl service = new PostTemplateServiceImpl(repository);
    when(repository.findByIdAndSiteId(5L, 2L)).thenReturn(Mono.empty());

    StepVerifier.create(service.delete(5L, 2L)).expectError(ApplicationException.class).verify();
    verify(repository, never()).delete(any());
  }

  @Test
  void createSanitizesTheBodyAndDefaultsTheLayout() {
    PostTemplateServiceImpl service = new PostTemplateServiceImpl(repository);
    when(repository.save(any(PostTemplate.class)))
        .thenAnswer(inv -> Mono.just((PostTemplate) inv.getArgument(0)));
    PostTemplateCreateRequest request = new PostTemplateCreateRequest();
    request.setName("  Announcement ");
    request.setBody("<p style=\"color:red\">ok</p>");

    StepVerifier.create(service.create(1L, request))
        .assertNext(
            t -> {
              assertEquals("Announcement", t.getName());
              assertEquals("default", t.getLayout());
              assertFalse(t.getBody().contains("style"));
            })
        .verifyComplete();
  }
}
