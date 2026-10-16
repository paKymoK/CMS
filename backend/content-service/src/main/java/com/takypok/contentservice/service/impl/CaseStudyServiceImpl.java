package com.takypok.contentservice.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.takypok.contentservice.model.content.types.CaseStudyFields;
import com.takypok.contentservice.model.content.types.TestimonialFields;
import com.takypok.contentservice.model.entity.ContentItem;
import com.takypok.contentservice.model.mapper.ContentItemMapper;
import com.takypok.contentservice.model.request.CaseStudyCreateRequest;
import com.takypok.contentservice.model.request.CaseStudyUpdateRequest;
import com.takypok.contentservice.model.response.CaseStudyDetailResponse;
import com.takypok.contentservice.model.response.CaseStudyResponse;
import com.takypok.contentservice.model.response.CaseStudySummaryResponse;
import com.takypok.contentservice.repository.ContentItemRepository;
import com.takypok.contentservice.service.CaseStudyService;
import com.takypok.contentservice.util.RichTextSanitizer;
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

/**
 * Case studies are content_item rows of type "case_study"; this service keeps what is specific to
 * them — slug generation/uniqueness, rich-text sanitizing, the testimonial link and the "related"
 * rail — on top of the generic table.
 */
@Component
@RequiredArgsConstructor
public class CaseStudyServiceImpl implements CaseStudyService {
  static final String TYPE = "case_study";
  private static final String TESTIMONIAL_TYPE = "testimonial";
  private static final Pattern DIACRITICS = Pattern.compile("\\p{M}");
  private static final Pattern NON_ALPHANUMERIC = Pattern.compile("[^a-z0-9]+");
  private static final int MAX_RELATED = 3;

  private final ContentItemRepository contentItemRepository;
  private final ContentItemMapper contentItemMapper;
  private final ObjectMapper objectMapper;

  @Override
  public Mono<List<CaseStudyResponse>> getAll(Long siteId) {
    return contentItemRepository
        .findAllBySiteIdAndContentTypeOrderByDisplayOrderAsc(siteId, TYPE)
        .map(this::toResponse)
        .collectList();
  }

  @Override
  public Mono<CaseStudyResponse> getById(Long id, Long siteId) {
    return findItem(id, siteId).map(this::toResponse);
  }

  @Override
  public Mono<CaseStudyResponse> create(Long siteId, CaseStudyCreateRequest request) {
    // defer: sanitizeOrReject throws, and that must surface as a Mono error, not at assembly time
    return Mono.defer(
        () ->
            save(
                siteId,
                new ContentItem(),
                request.getTitle(),
                request.getSlug(),
                toFields(request),
                request.getDisplayOrder(),
                request.getActive(),
                request.getStatus()));
  }

  @Override
  public Mono<CaseStudyResponse> update(Long siteId, CaseStudyUpdateRequest request) {
    return findItem(request.getId(), siteId)
        .flatMap(
            item ->
                Mono.defer(
                    () ->
                        save(
                            siteId,
                            item,
                            request.getTitle(),
                            request.getSlug(),
                            toFields(request),
                            request.getDisplayOrder(),
                            request.getActive(),
                            request.getStatus())));
  }

  @Override
  public Mono<Void> delete(Long id, Long siteId) {
    return findItem(id, siteId).flatMap(contentItemRepository::delete);
  }

  @Override
  public Flux<CaseStudyResponse> getPublished(Long siteId) {
    return contentItemRepository
        .findAllBySiteIdAndContentTypeAndStatusAndActiveOrderByDisplayOrderAsc(
            siteId, TYPE, "PUBLISHED", true)
        .map(this::toResponse);
  }

  @Override
  public Mono<CaseStudyDetailResponse> getPublishedDetailBySlug(Long siteId, String slug) {
    return detailOf(
        siteId,
        contentItemRepository
            .findBySlug(siteId, TYPE, slug)
            .filter(i -> "PUBLISHED".equals(i.getStatus()) && Boolean.TRUE.equals(i.getActive())),
        true);
  }

  @Override
  public Mono<CaseStudyDetailResponse> getPreviewDetailBySlug(Long siteId, String slug) {
    return detailOf(siteId, contentItemRepository.findBySlug(siteId, TYPE, slug), false);
  }

  private Mono<CaseStudyDetailResponse> detailOf(
      Long siteId, Mono<ContentItem> lookup, boolean requireActiveTestimonial) {
    return lookup
        .switchIfEmpty(
            Mono.error(new ApplicationException(Message.Application.ERROR, "Case study not found")))
        .map(this::toResponse)
        .flatMap(
            caseStudy ->
                resolveTestimonial(siteId, caseStudy.testimonialId(), requireActiveTestimonial)
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

  private Mono<ContentItem> findItem(Long id, Long siteId) {
    return contentItemRepository
        .findByIdAndSiteId(id, siteId)
        .filter(item -> TYPE.equals(item.getContentType()))
        .switchIfEmpty(
            Mono.error(
                new ApplicationException(Message.Application.ERROR, "Case study not found")));
  }

  /** Validates, resolves the slug and persists {@code item} (new, or the row being updated). */
  private Mono<CaseStudyResponse> save(
      Long siteId,
      ContentItem item,
      String title,
      String requestedSlug,
      CaseStudyFields fields,
      Integer displayOrder,
      Boolean active,
      String status) {
    fields.setBody(RichTextSanitizer.sanitizeOrReject(fields.getBody()));
    return validateTestimonial(siteId, fields.getTestimonialId())
        .then(Mono.defer(() -> resolveSlug(siteId, item.getId(), requestedSlug, title)))
        .flatMap(
            slug -> {
              fields.setSlug(slug);
              return contentItemMapper.resolveAndValidate(TYPE, objectMapper.valueToTree(fields));
            })
        .flatMap(
            data -> {
              item.setSiteId(siteId);
              item.setContentType(TYPE);
              item.setData(data);
              if (displayOrder != null) item.setDisplayOrder(displayOrder);
              else if (item.getDisplayOrder() == null) item.setDisplayOrder(0);
              if (active != null) item.setActive(active);
              else if (item.getActive() == null) item.setActive(true);
              if (status != null) item.setStatus(status);
              else if (item.getStatus() == null) item.setStatus("DRAFT");
              return contentItemRepository.save(item);
            })
        .map(this::toResponse);
  }

  private CaseStudyResponse toResponse(ContentItem item) {
    CaseStudyFields fields =
        objectMapper.convertValue(
            item.getData() != null ? item.getData() : objectMapper.createObjectNode(),
            CaseStudyFields.class);
    return CaseStudyResponse.from(item, fields);
  }

  private static CaseStudyFields toFields(CaseStudyCreateRequest r) {
    CaseStudyFields f = new CaseStudyFields();
    f.setTitle(r.getTitle());
    f.setDate(r.getDate());
    f.setImage(r.getImage());
    f.setCategory(r.getCategory());
    f.setSummary(r.getSummary());
    f.setBody(r.getBody());
    f.setResults(r.getResults());
    f.setTestimonialId(r.getTestimonialId());
    return f;
  }

  private static CaseStudyFields toFields(CaseStudyUpdateRequest r) {
    CaseStudyFields f = new CaseStudyFields();
    f.setTitle(r.getTitle());
    f.setDate(r.getDate());
    f.setImage(r.getImage());
    f.setCategory(r.getCategory());
    f.setSummary(r.getSummary());
    f.setBody(r.getBody());
    f.setResults(r.getResults());
    f.setTestimonialId(r.getTestimonialId());
    return f;
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
    return contentItemRepository
        .findByIdAndSiteId(testimonialId, siteId)
        .filter(t -> TESTIMONIAL_TYPE.equals(t.getContentType()))
        .filter(t -> !requireActive || Boolean.TRUE.equals(t.getActive()))
        .map(t -> objectMapper.convertValue(t.getData(), TestimonialFields.class))
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
    return contentItemRepository
        .findByIdAndSiteId(testimonialId, siteId)
        .filter(t -> TESTIMONIAL_TYPE.equals(t.getContentType()))
        .switchIfEmpty(
            Mono.error(
                new ApplicationException(
                    Message.Application.ERROR,
                    "Testimonial does not belong to this site: " + testimonialId)))
        .then();
  }

  /** Same-category first (excluding self), backfilled with the next most-recent others. */
  private List<CaseStudySummaryResponse> relatedTo(
      CaseStudyResponse caseStudy, List<CaseStudyResponse> published) {
    List<CaseStudyResponse> sameCategory = new ArrayList<>();
    List<CaseStudyResponse> others = new ArrayList<>();
    for (CaseStudyResponse candidate : published) {
      if (candidate.id().equals(caseStudy.id())) {
        continue;
      }
      if (candidate.category() != null && candidate.category().equals(caseStudy.category())) {
        sameCategory.add(candidate);
      } else {
        others.add(candidate);
      }
    }
    List<CaseStudyResponse> combined = new ArrayList<>(sameCategory);
    for (CaseStudyResponse candidate : others) {
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
      return slugTaken(siteId, slug, excludeId)
          .flatMap(
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
    return slugTaken(siteId, candidate, excludeId)
        .flatMap(
            taken ->
                taken
                    ? dedupeSlug(siteId, excludeId, baseSlug, attempt + 1)
                    : Mono.just(candidate));
  }

  private Mono<Boolean> slugTaken(Long siteId, String slug, Long excludeId) {
    return contentItemRepository
        .findBySlug(siteId, TYPE, slug)
        .filter(other -> excludeId == null || !other.getId().equals(excludeId))
        .hasElement();
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
