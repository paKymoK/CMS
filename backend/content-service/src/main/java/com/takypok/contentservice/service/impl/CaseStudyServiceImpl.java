package com.takypok.contentservice.service.impl;

import com.takypok.contentservice.model.entity.CaseStudy;
import com.takypok.contentservice.model.request.CaseStudyCreateRequest;
import com.takypok.contentservice.model.request.CaseStudyUpdateRequest;
import com.takypok.contentservice.model.response.CaseStudyDetailResponse;
import com.takypok.contentservice.model.response.CaseStudySummaryResponse;
import com.takypok.contentservice.repository.CaseStudyRepository;
import com.takypok.contentservice.repository.TestimonialRepository;
import com.takypok.contentservice.service.CaseStudyService;
import com.takypok.core.exception.ApplicationException;
import com.takypok.core.model.Message;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class CaseStudyServiceImpl implements CaseStudyService {
  private static final Pattern DIACRITICS = Pattern.compile("\\p{M}");
  private static final Pattern NON_ALPHANUMERIC = Pattern.compile("[^a-z0-9]+");
  private static final int MAX_RELATED = 3;

  private final CaseStudyRepository caseStudyRepository;
  private final TestimonialRepository testimonialRepository;

  @Override
  public Mono<List<CaseStudy>> getAll(Long siteId) {
    return caseStudyRepository.findAllBySiteIdOrderByDisplayOrderAsc(siteId).collectList();
  }

  @Override
  public Mono<CaseStudy> getById(Long id, Long siteId) {
    return caseStudyRepository
        .findByIdAndSiteId(id, siteId)
        .switchIfEmpty(
            Mono.error(
                new ApplicationException(Message.Application.ERROR, "Case study not found")));
  }

  @Override
  public Mono<CaseStudy> create(Long siteId, CaseStudyCreateRequest request) {
    CaseStudy caseStudy = new CaseStudy();
    caseStudy.setSiteId(siteId);
    caseStudy.setDate(request.getDate());
    caseStudy.setImage(request.getImage());
    caseStudy.setTitle(request.getTitle());
    caseStudy.setCategory(request.getCategory());
    caseStudy.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0);
    caseStudy.setActive(request.getActive() != null ? request.getActive() : true);
    caseStudy.setStatus(request.getStatus() != null ? request.getStatus() : "DRAFT");
    caseStudy.setBody(request.getBody());
    caseStudy.setSummary(request.getSummary());
    caseStudy.setResults(request.getResults());
    return validateTestimonial(siteId, request.getTestimonialId())
        .then(Mono.defer(() -> resolveSlug(siteId, null, request.getSlug(), request.getTitle())))
        .flatMap(
            slug -> {
              caseStudy.setSlug(slug);
              caseStudy.setTestimonialId(request.getTestimonialId());
              return caseStudyRepository.save(caseStudy);
            });
  }

  @Override
  public Mono<CaseStudy> update(Long siteId, CaseStudyUpdateRequest request) {
    return getById(request.getId(), siteId)
        .flatMap(
            caseStudy -> {
              caseStudy.setDate(request.getDate());
              caseStudy.setImage(request.getImage());
              caseStudy.setTitle(request.getTitle());
              caseStudy.setCategory(request.getCategory());
              if (request.getDisplayOrder() != null)
                caseStudy.setDisplayOrder(request.getDisplayOrder());
              if (request.getActive() != null) caseStudy.setActive(request.getActive());
              if (request.getStatus() != null) caseStudy.setStatus(request.getStatus());
              caseStudy.setBody(request.getBody());
              caseStudy.setSummary(request.getSummary());
              caseStudy.setResults(request.getResults());
              return validateTestimonial(siteId, request.getTestimonialId())
                  .then(
                      Mono.defer(
                          () ->
                              resolveSlug(
                                  siteId,
                                  caseStudy.getId(),
                                  request.getSlug(),
                                  request.getTitle())))
                  .flatMap(
                      slug -> {
                        caseStudy.setSlug(slug);
                        caseStudy.setTestimonialId(request.getTestimonialId());
                        return caseStudyRepository.save(caseStudy);
                      });
            });
  }

  @Override
  public Mono<Void> delete(Long id, Long siteId) {
    return getById(id, siteId).flatMap(caseStudyRepository::delete);
  }

  @Override
  public Flux<CaseStudy> getPublished(Long siteId) {
    return caseStudyRepository.findAllBySiteIdAndStatusAndActiveOrderByDisplayOrderAsc(
        siteId, "PUBLISHED", true);
  }

  @Override
  public Mono<CaseStudyDetailResponse> getPublishedDetailBySlug(Long siteId, String slug) {
    return caseStudyRepository
        .findBySiteIdAndSlugAndStatusAndActive(siteId, slug, "PUBLISHED", true)
        .switchIfEmpty(
            Mono.error(new ApplicationException(Message.Application.ERROR, "Case study not found")))
        .flatMap(
            caseStudy ->
                resolveTestimonial(siteId, caseStudy.getTestimonialId(), true)
                    .flatMap(
                        testimonial ->
                            getPublished(siteId)
                                .collectList()
                                .map(all -> relatedTo(caseStudy, all))
                                .map(
                                    related ->
                                        CaseStudyDetailResponse.from(
                                            caseStudy, testimonial.orElse(null), related))));
  }

  @Override
  public Mono<CaseStudyDetailResponse> getPreviewDetailBySlug(Long siteId, String slug) {
    return caseStudyRepository
        .findBySiteIdAndSlug(siteId, slug)
        .switchIfEmpty(
            Mono.error(new ApplicationException(Message.Application.ERROR, "Case study not found")))
        .flatMap(
            caseStudy ->
                resolveTestimonial(siteId, caseStudy.getTestimonialId(), false)
                    .flatMap(
                        testimonial ->
                            getPublished(siteId)
                                .collectList()
                                .map(all -> relatedTo(caseStudy, all))
                                .map(
                                    related ->
                                        CaseStudyDetailResponse.from(
                                            caseStudy, testimonial.orElse(null), related))));
  }

  /**
   * Empty Optional (never a Mono error/empty) whenever there's no testimonialId, the id doesn't
   * resolve for this site, or the testimonial isn't active — a missing/unpublished quote just omits
   * that section of the page rather than failing the whole case study.
   */
  private Mono<Optional<CaseStudyDetailResponse.TestimonialSummary>> resolveTestimonial(
      Long siteId, Long testimonialId, boolean requireActive) {
    if (testimonialId == null) {
      return Mono.just(Optional.empty());
    }
    return testimonialRepository
        .findByIdAndSiteId(testimonialId, siteId)
        .filter(t -> !requireActive || Boolean.TRUE.equals(t.getActive()))
        .map(CaseStudyDetailResponse.TestimonialSummary::from)
        .map(Optional::of)
        .defaultIfEmpty(Optional.empty());
  }

  /**
   * A case study's testimonialId must belong to the SAME site — never trust a client-supplied id
   * blindly, since that would let one site's admin surface another site's testimonial content.
   */
  private Mono<Void> validateTestimonial(Long siteId, Long testimonialId) {
    if (testimonialId == null) {
      return Mono.empty();
    }
    return testimonialRepository
        .findByIdAndSiteId(testimonialId, siteId)
        .switchIfEmpty(
            Mono.error(
                new ApplicationException(
                    Message.Application.ERROR,
                    "Testimonial does not belong to this site: " + testimonialId)))
        .then();
  }

  /** Same-category first (excluding self), backfilled with the next most-recent others. */
  private List<CaseStudySummaryResponse> relatedTo(CaseStudy caseStudy, List<CaseStudy> published) {
    List<CaseStudy> sameCategory = new ArrayList<>();
    List<CaseStudy> others = new ArrayList<>();
    for (CaseStudy candidate : published) {
      if (candidate.getId().equals(caseStudy.getId())) {
        continue;
      }
      if (candidate.getCategory() != null
          && candidate.getCategory().equals(caseStudy.getCategory())) {
        sameCategory.add(candidate);
      } else {
        others.add(candidate);
      }
    }
    List<CaseStudy> combined = new ArrayList<>(sameCategory);
    for (CaseStudy candidate : others) {
      if (combined.size() >= MAX_RELATED) {
        break;
      }
      combined.add(candidate);
    }
    return combined.stream().limit(MAX_RELATED).map(CaseStudySummaryResponse::from).toList();
  }

  private Mono<String> resolveSlug(
      Long siteId, Long excludeId, String requestedSlug, String title) {
    if (requestedSlug != null && !requestedSlug.isBlank()) {
      String slug = slugify(requestedSlug);
      Mono<Boolean> collision =
          excludeId != null
              ? caseStudyRepository.existsBySiteIdAndSlugAndIdNot(siteId, slug, excludeId)
              : caseStudyRepository.existsBySiteIdAndSlug(siteId, slug);
      return collision.flatMap(
          taken ->
              taken
                  ? Mono.error(
                      new ApplicationException(
                          Message.Application.ERROR, "Slug already in use: " + slug))
                  : Mono.just(slug));
    }
    return dedupeSlug(siteId, excludeId, slugify(title), 1);
  }

  private Mono<String> dedupeSlug(Long siteId, Long excludeId, String baseSlug, int attempt) {
    String candidate = attempt == 1 ? baseSlug : baseSlug + "-" + attempt;
    Mono<Boolean> collision =
        excludeId != null
            ? caseStudyRepository.existsBySiteIdAndSlugAndIdNot(siteId, candidate, excludeId)
            : caseStudyRepository.existsBySiteIdAndSlug(siteId, candidate);
    return collision.flatMap(
        taken ->
            taken ? dedupeSlug(siteId, excludeId, baseSlug, attempt + 1) : Mono.just(candidate));
  }

  private static String slugify(String text) {
    if (text == null) {
      return "case-study";
    }
    String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
    String withoutDiacritics = DIACRITICS.matcher(normalized).replaceAll("");
    String slug = NON_ALPHANUMERIC.matcher(withoutDiacritics.toLowerCase()).replaceAll("-");
    slug = slug.replaceAll("^-+|-+$", "");
    String[] words = slug.split("-");
    int wordLimit = Math.min(words.length, 7);
    StringBuilder result = new StringBuilder();
    for (int i = 0; i < wordLimit; i++) {
      if (result.length() > 0) {
        result.append("-");
      }
      result.append(words[i]);
    }
    return result.length() > 0 ? result.toString() : "case-study";
  }
}
