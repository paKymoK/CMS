package com.takypok.contentservice.service.impl;

import com.takypok.contentservice.model.entity.ContentItem;
import com.takypok.contentservice.model.entity.ContentSlug;
import com.takypok.contentservice.repository.ContentItemRepository;
import com.takypok.contentservice.repository.ContentSlugRepository;
import com.takypok.contentservice.service.SlugRegistry;
import com.takypok.core.exception.ApplicationException;
import com.takypok.core.model.Message;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class SlugRegistryImpl implements SlugRegistry {
  /** The public URL section of each type with a detail page, as the website routes it. */
  private static final Map<String, String> TYPE_BY_SECTION =
      Map.of(
          "posts", PostServiceImpl.TYPE,
          "case-studies", CaseStudyServiceImpl.TYPE,
          "industries", IndustryPageServiceImpl.TYPE,
          "services", ServiceCatalogServiceImpl.PAGE);

  private final ContentSlugRepository slugs;
  private final ContentItemRepository items;

  @Override
  public Mono<Boolean> isAliasOfOther(
      Long siteId, String contentType, String scope, String slug, Long excludeId) {
    return slugs
        .findBySiteIdAndContentTypeAndScopeAndSlug(siteId, contentType, scope, slug)
        .filter(row -> !row.isPrimary())
        .filter(row -> excludeId == null || !row.getItemId().equals(excludeId))
        .hasElement();
  }

  @Override
  public Mono<ContentItem> findPublishedByAlias(
      Long siteId, String contentType, String scope, String slug) {
    return slugs
        .findBySiteIdAndContentTypeAndScopeAndSlug(siteId, contentType, scope, slug)
        .filter(row -> !row.isPrimary())
        .flatMap(row -> items.findByIdAndSiteId(row.getItemId(), siteId))
        .filter(item -> "PUBLISHED".equals(item.getStatus()));
  }

  @Override
  public Mono<String> currentPathOfAlias(Long siteId, String section, String path) {
    String type = TYPE_BY_SECTION.get(section);
    if (type == null || path == null || path.isBlank()) return Mono.empty();
    boolean scoped = ServiceCatalogServiceImpl.PAGE.equals(type);
    int cut = path.indexOf('/');
    if (scoped && cut <= 0) return Mono.empty();
    if (!scoped && cut >= 0) return Mono.empty();
    String scope = scoped ? path.substring(0, cut) : "";
    String slug = scoped ? path.substring(cut + 1) : path;
    return findPublishedByAlias(siteId, type, scope, slug)
        .flatMap(item -> slugs.findByItemIdAndPrimaryTrue(item.getId()))
        .map(ContentSlug::path);
  }

  @Override
  public Flux<ContentSlug> forItem(Long siteId, Long itemId) {
    return slugs.findAllBySiteIdAndItemIdOrderByPrimaryDescIdAsc(siteId, itemId);
  }

  @Override
  public Mono<Void> removeAlias(Long siteId, Long slugId) {
    return slugs
        .findById(slugId)
        .filter(row -> siteId.equals(row.getSiteId()))
        .switchIfEmpty(
            Mono.error(new ApplicationException(Message.Application.ERROR, "Slug not found")))
        .flatMap(
            row ->
                row.isPrimary()
                    ? Mono.<Void>error(
                        new ApplicationException(
                            Message.Application.ERROR, "The current slug cannot be removed"))
                    : slugs.delete(row));
  }
}
