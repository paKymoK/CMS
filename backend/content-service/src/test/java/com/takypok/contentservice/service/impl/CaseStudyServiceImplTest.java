package com.takypok.contentservice.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.takypok.contentservice.model.entity.CaseStudy;
import com.takypok.contentservice.model.entity.Testimonial;
import com.takypok.contentservice.model.request.CaseStudyCreateRequest;
import com.takypok.contentservice.repository.CaseStudyRepository;
import com.takypok.contentservice.repository.TestimonialRepository;
import com.takypok.core.exception.ApplicationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * Cross-site-safety tests for CaseStudyServiceImpl — see PostServiceImplTest for the same on Post.
 */
@ExtendWith(MockitoExtension.class)
class CaseStudyServiceImplTest {

  @Mock private CaseStudyRepository caseStudyRepository;
  @Mock private TestimonialRepository testimonialRepository;

  @Test
  void publishedDetailLookupNeverLeaksAnotherSitesCaseStudyForTheSameSlug() {
    CaseStudyServiceImpl service =
        new CaseStudyServiceImpl(caseStudyRepository, testimonialRepository);
    CaseStudy siteOnes = new CaseStudy();
    siteOnes.setId(1L);
    siteOnes.setSiteId(1L);
    siteOnes.setSlug("shared-slug");
    siteOnes.setTitle("Site 1's case study");
    siteOnes.setCategory("cloud");

    when(caseStudyRepository.findBySiteIdAndSlugAndStatusAndActive(
            1L, "shared-slug", "PUBLISHED", true))
        .thenReturn(Mono.just(siteOnes));
    when(caseStudyRepository.findAllBySiteIdAndStatusAndActiveOrderByDisplayOrderAsc(
            1L, "PUBLISHED", true))
        .thenReturn(Flux.just(siteOnes));
    when(caseStudyRepository.findBySiteIdAndSlugAndStatusAndActive(
            2L, "shared-slug", "PUBLISHED", true))
        .thenReturn(Mono.empty());

    StepVerifier.create(service.getPublishedDetailBySlug(1L, "shared-slug"))
        .assertNext(detail -> assertEquals("Site 1's case study", detail.title()))
        .verifyComplete();

    StepVerifier.create(service.getPublishedDetailBySlug(2L, "shared-slug"))
        .expectError(ApplicationException.class)
        .verify();
  }

  @Test
  void creatingACaseStudyWithAnotherSitesTestimonialIdIsRejected() {
    CaseStudyServiceImpl service =
        new CaseStudyServiceImpl(caseStudyRepository, testimonialRepository);
    // testimonial 5 belongs to site 1, not site 2
    when(testimonialRepository.findByIdAndSiteId(5L, 2L)).thenReturn(Mono.empty());

    CaseStudyCreateRequest request = new CaseStudyCreateRequest();
    request.setTitle("A case study");
    request.setTestimonialId(5L);

    StepVerifier.create(service.create(2L, request))
        .expectError(ApplicationException.class)
        .verify();

    verify(caseStudyRepository, never()).save(any(CaseStudy.class));
  }

  @Test
  void creatingACaseStudyWithItsOwnSitesTestimonialSucceeds() {
    CaseStudyServiceImpl service =
        new CaseStudyServiceImpl(caseStudyRepository, testimonialRepository);
    Testimonial ownTestimonial = new Testimonial();
    ownTestimonial.setId(5L);
    ownTestimonial.setSiteId(1L);
    when(testimonialRepository.findByIdAndSiteId(5L, 1L)).thenReturn(Mono.just(ownTestimonial));
    when(caseStudyRepository.existsBySiteIdAndSlug(1L, "a-case-study"))
        .thenReturn(Mono.just(false));
    when(caseStudyRepository.save(any(CaseStudy.class)))
        .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

    CaseStudyCreateRequest request = new CaseStudyCreateRequest();
    request.setTitle("A case study");
    request.setTestimonialId(5L);

    StepVerifier.create(service.create(1L, request)).expectNextCount(1).verifyComplete();
  }
}
