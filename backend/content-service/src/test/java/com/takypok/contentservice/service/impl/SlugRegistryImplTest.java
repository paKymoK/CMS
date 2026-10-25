package com.takypok.contentservice.service.impl;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.takypok.contentservice.model.entity.ContentItem;
import com.takypok.contentservice.model.entity.ContentSlug;
import com.takypok.contentservice.repository.ContentItemRepository;
import com.takypok.contentservice.repository.ContentSlugRepository;
import com.takypok.core.exception.ApplicationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class SlugRegistryImplTest {
  @Mock private ContentSlugRepository slugs;
  @Mock private ContentItemRepository items;

  private SlugRegistryImpl registry;

  @BeforeEach
  void setUp() {
    registry = new SlugRegistryImpl(slugs, items);
  }

  private static ContentSlug row(
      long id, long itemId, String type, String scope, String slug, boolean primary) {
    ContentSlug row = new ContentSlug();
    row.setId(id);
    row.setSiteId(1L);
    row.setItemId(itemId);
    row.setContentType(type);
    row.setScope(scope);
    row.setSlug(slug);
    row.setPrimary(primary);
    return row;
  }

  private static ContentItem item(long id, String status) {
    ContentItem item = new ContentItem();
    item.setId(id);
    item.setStatus(status);
    return item;
  }

  @Test
  void anOldSlugOfAnotherItemCountsAsTakenButTheItemsOwnOldSlugDoesNot() {
    when(slugs.findBySiteIdAndContentTypeAndScopeAndSlug(1L, "post", "", "old"))
        .thenReturn(Mono.just(row(1, 7, "post", "", "old", false)));

    StepVerifier.create(registry.isAliasOfOther(1L, "post", "", "old", 8L))
        .expectNext(true)
        .verifyComplete();
    StepVerifier.create(registry.isAliasOfOther(1L, "post", "", "old", 7L))
        .expectNext(false)
        .verifyComplete();
  }

  @Test
  void aCurrentSlugIsNotAnAlias() {
    when(slugs.findBySiteIdAndContentTypeAndScopeAndSlug(1L, "post", "", "now"))
        .thenReturn(Mono.just(row(1, 7, "post", "", "now", true)));

    StepVerifier.create(registry.isAliasOfOther(1L, "post", "", "now", null))
        .expectNext(false)
        .verifyComplete();
  }

  @Test
  void anOldPostSlugRedirectsToTheCurrentOne() {
    when(slugs.findBySiteIdAndContentTypeAndScopeAndSlug(1L, "post", "", "old"))
        .thenReturn(Mono.just(row(1, 7, "post", "", "old", false)));
    when(items.findByIdAndSiteId(7L, 1L)).thenReturn(Mono.just(item(7, "PUBLISHED")));
    when(slugs.findByItemIdAndPrimaryTrue(7L))
        .thenReturn(Mono.just(row(2, 7, "post", "", "new", true)));

    StepVerifier.create(registry.currentPathOfAlias(1L, "posts", "old"))
        .expectNext("new")
        .verifyComplete();
  }

  @Test
  void anOldServiceAddressKeepsItsCategoryOnBothSides() {
    when(slugs.findBySiteIdAndContentTypeAndScopeAndSlug(1L, "service_page", "web", "dev"))
        .thenReturn(Mono.just(row(1, 7, "service_page", "web", "dev", false)));
    when(items.findByIdAndSiteId(7L, 1L)).thenReturn(Mono.just(item(7, "PUBLISHED")));
    when(slugs.findByItemIdAndPrimaryTrue(7L))
        .thenReturn(Mono.just(row(2, 7, "service_page", "cloud", "dev", true)));

    StepVerifier.create(registry.currentPathOfAlias(1L, "services", "web/dev"))
        .expectNext("cloud/dev")
        .verifyComplete();
  }

  @Test
  void anUnpublishedItemsOldSlugIsNotFound() {
    when(slugs.findBySiteIdAndContentTypeAndScopeAndSlug(1L, "post", "", "old"))
        .thenReturn(Mono.just(row(1, 7, "post", "", "old", false)));
    when(items.findByIdAndSiteId(7L, 1L)).thenReturn(Mono.just(item(7, "DRAFT")));

    StepVerifier.create(registry.currentPathOfAlias(1L, "posts", "old")).verifyComplete();
  }

  @Test
  void aCurrentSlugOrAnUnknownSectionOrAMalformedPathIsNotARedirect() {
    when(slugs.findBySiteIdAndContentTypeAndScopeAndSlug(1L, "post", "", "now"))
        .thenReturn(Mono.just(row(1, 7, "post", "", "now", true)));

    StepVerifier.create(registry.currentPathOfAlias(1L, "posts", "now")).verifyComplete();
    StepVerifier.create(registry.currentPathOfAlias(1L, "nonsense", "x")).verifyComplete();
    StepVerifier.create(registry.currentPathOfAlias(1L, "services", "no-category"))
        .verifyComplete();
    StepVerifier.create(registry.currentPathOfAlias(1L, "posts", "a/b")).verifyComplete();
  }

  @Test
  void anOldSlugOfAnotherSiteIsNotReleasedAndTheCurrentSlugCannotBeRemoved() {
    ContentSlug otherSite = row(1, 7, "post", "", "old", false);
    otherSite.setSiteId(2L);
    when(slugs.findById(1L)).thenReturn(Mono.just(otherSite));
    when(slugs.findById(2L)).thenReturn(Mono.just(row(2, 7, "post", "", "now", true)));

    StepVerifier.create(registry.removeAlias(1L, 1L))
        .expectError(ApplicationException.class)
        .verify();
    StepVerifier.create(registry.removeAlias(1L, 2L))
        .expectError(ApplicationException.class)
        .verify();
    verify(slugs, never()).delete(org.mockito.ArgumentMatchers.any());
  }

  @Test
  void anOldSlugOfTheSiteIsReleased() {
    ContentSlug alias = row(1, 7, "post", "", "old", false);
    when(slugs.findById(1L)).thenReturn(Mono.just(alias));
    when(slugs.delete(alias)).thenReturn(Mono.empty());

    StepVerifier.create(registry.removeAlias(1L, 1L)).verifyComplete();
    verify(slugs).delete(alias);
  }
}
