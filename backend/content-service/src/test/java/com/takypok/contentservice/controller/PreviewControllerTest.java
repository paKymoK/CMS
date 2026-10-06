package com.takypok.contentservice.controller;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.takypok.contentservice.config.PreviewTokenGuard;
import com.takypok.contentservice.model.entity.PreviewToken;
import com.takypok.contentservice.service.CaseStudyService;
import com.takypok.contentservice.service.HomeContentService;
import com.takypok.contentservice.service.PostService;
import com.takypok.contentservice.service.PreviewTokenService;
import com.takypok.core.exception.ApplicationException;
import com.takypok.core.model.Message;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * The per-item preview endpoints must take their site from the token alone. A token minted for site
 * 1 may only ever reach the services with siteId 1, and a bad/expired token must never reach them
 * at all.
 */
@ExtendWith(MockitoExtension.class)
class PreviewControllerTest {

  @Mock private PreviewTokenGuard previewTokenGuard;
  @Mock private PreviewTokenService previewTokenService;
  @Mock private HomeContentService homeContentService;
  @Mock private PostService postService;
  @Mock private CaseStudyService caseStudyService;

  private PreviewController controller() {
    return new PreviewController(
        previewTokenGuard, previewTokenService, homeContentService, postService, caseStudyService);
  }

  private static PreviewToken tokenForSite(Long siteId) {
    PreviewToken token = new PreviewToken();
    token.setToken("t");
    token.setSiteId(siteId);
    return token;
  }

  @Test
  void postPreviewUsesTheTokensSiteOnly() {
    when(previewTokenGuard.requireValidToken("t")).thenReturn(Mono.just(tokenForSite(1L)));
    when(postService.getPreviewDetailBySlug(1L, "s")).thenReturn(Mono.empty());

    StepVerifier.create(controller().getPost("s", "t")).verifyComplete();

    verify(postService).getPreviewDetailBySlug(1L, "s");
    verify(postService, never()).getPreviewDetailBySlug(2L, "s");
  }

  @Test
  void caseStudyPreviewUsesTheTokensSiteOnly() {
    when(previewTokenGuard.requireValidToken("t")).thenReturn(Mono.just(tokenForSite(1L)));
    when(caseStudyService.getPreviewDetailBySlug(1L, "s")).thenReturn(Mono.empty());

    StepVerifier.create(controller().getCaseStudy("s", "t")).verifyComplete();

    verify(caseStudyService).getPreviewDetailBySlug(1L, "s");
    verify(caseStudyService, never()).getPreviewDetailBySlug(2L, "s");
  }

  @Test
  void invalidTokenNeverReachesEitherService() {
    when(previewTokenGuard.requireValidToken("bad"))
        .thenReturn(Mono.error(new ApplicationException(Message.Application.ERROR, "nope")));

    StepVerifier.create(controller().getPost("s", "bad"))
        .expectError(ApplicationException.class)
        .verify();
    StepVerifier.create(controller().getCaseStudy("s", "bad"))
        .expectError(ApplicationException.class)
        .verify();

    verify(postService, never()).getPreviewDetailBySlug(anyLong(), anyString());
    verify(caseStudyService, never()).getPreviewDetailBySlug(anyLong(), anyString());
  }
}
