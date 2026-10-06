package com.takypok.contentservice.service.impl;

import com.takypok.contentservice.model.entity.Post;
import com.takypok.contentservice.model.request.PostCreateRequest;
import com.takypok.contentservice.model.request.PostUpdateRequest;
import com.takypok.contentservice.model.response.PostDetailResponse;
import com.takypok.contentservice.model.response.PostSummaryResponse;
import com.takypok.contentservice.repository.PostRepository;
import com.takypok.contentservice.service.PostService;
import com.takypok.core.exception.ApplicationException;
import com.takypok.core.model.Message;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
  private static final Pattern DIACRITICS = Pattern.compile("\\p{M}");
  private static final Pattern NON_ALPHANUMERIC = Pattern.compile("[^a-z0-9]+");
  private static final int MAX_RELATED = 3;

  private final PostRepository postRepository;

  @Override
  public Mono<List<Post>> getAll(Long siteId) {
    return postRepository.findAllBySiteIdOrderByDisplayOrderAsc(siteId).collectList();
  }

  @Override
  public Mono<Post> getById(Long id, Long siteId) {
    return postRepository
        .findByIdAndSiteId(id, siteId)
        .switchIfEmpty(
            Mono.error(new ApplicationException(Message.Application.ERROR, "Post not found")));
  }

  @Override
  public Mono<Post> create(Long siteId, PostCreateRequest request) {
    Post post = new Post();
    post.setSiteId(siteId);
    post.setCategory(request.getCategory() != null ? request.getCategory() : "insight");
    post.setImage(request.getImage());
    post.setTitle(request.getTitle());
    post.setExcerpt(request.getExcerpt());
    post.setDate(request.getDate());
    post.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0);
    post.setActive(request.getActive() != null ? request.getActive() : true);
    post.setStatus(request.getStatus() != null ? request.getStatus() : "DRAFT");
    post.setBody(request.getBody());
    post.setAuthorName(request.getAuthorName());
    post.setAuthorRole(request.getAuthorRole());
    post.setAuthorBio(request.getAuthorBio());
    post.setAuthorAvatar(request.getAuthorAvatar());
    post.setTags(request.getTags());
    post.setFeatured(request.getFeatured() != null ? request.getFeatured() : false);
    return resolveSlug(siteId, null, request.getSlug(), request.getTitle())
        .flatMap(
            slug -> {
              post.setSlug(slug);
              return postRepository.save(post);
            });
  }

  @Override
  public Mono<Post> update(Long siteId, PostUpdateRequest request) {
    return getById(request.getId(), siteId)
        .flatMap(
            post -> {
              if (request.getCategory() != null) post.setCategory(request.getCategory());
              post.setImage(request.getImage());
              post.setTitle(request.getTitle());
              post.setExcerpt(request.getExcerpt());
              post.setDate(request.getDate());
              if (request.getDisplayOrder() != null)
                post.setDisplayOrder(request.getDisplayOrder());
              if (request.getActive() != null) post.setActive(request.getActive());
              if (request.getStatus() != null) post.setStatus(request.getStatus());
              post.setBody(request.getBody());
              post.setAuthorName(request.getAuthorName());
              post.setAuthorRole(request.getAuthorRole());
              post.setAuthorBio(request.getAuthorBio());
              post.setAuthorAvatar(request.getAuthorAvatar());
              post.setTags(request.getTags());
              if (request.getFeatured() != null) post.setFeatured(request.getFeatured());
              return resolveSlug(siteId, post.getId(), request.getSlug(), request.getTitle())
                  .flatMap(
                      slug -> {
                        post.setSlug(slug);
                        return postRepository.save(post);
                      });
            });
  }

  @Override
  public Mono<Void> delete(Long id, Long siteId) {
    return getById(id, siteId).flatMap(postRepository::delete);
  }

  @Override
  public Flux<Post> getPublished(Long siteId) {
    return postRepository.findAllBySiteIdAndStatusAndActiveOrderByDisplayOrderAsc(
        siteId, "PUBLISHED", true);
  }

  @Override
  public Mono<PostDetailResponse> getPublishedDetailBySlug(Long siteId, String slug) {
    return postRepository
        .findBySiteIdAndSlugAndStatusAndActive(siteId, slug, "PUBLISHED", true)
        .switchIfEmpty(
            Mono.error(new ApplicationException(Message.Application.ERROR, "Post not found")))
        .flatMap(
            post ->
                getPublished(siteId)
                    .collectList()
                    .map(all -> relatedTo(post, all))
                    .map(related -> PostDetailResponse.from(post, related)));
  }

  @Override
  public Mono<PostDetailResponse> getPreviewDetailBySlug(Long siteId, String slug) {
    return postRepository
        .findBySiteIdAndSlug(siteId, slug)
        .switchIfEmpty(
            Mono.error(new ApplicationException(Message.Application.ERROR, "Post not found")))
        .flatMap(
            post ->
                getPublished(siteId)
                    .collectList()
                    .map(all -> relatedTo(post, all))
                    .map(related -> PostDetailResponse.from(post, related)));
  }

  @Override
  public Mono<List<PostSummaryResponse>> searchPublished(Long siteId, String tag, String q) {
    String normalizedQuery = q != null && !q.isBlank() ? q.trim().toLowerCase() : null;
    return getPublished(siteId)
        .filter(post -> tag == null || tag.isBlank() || tagsContain(post, tag))
        .filter(
            post ->
                normalizedQuery == null
                    || ((post.getTitle() != null ? post.getTitle() : "")
                            + " "
                            + (post.getExcerpt() != null ? post.getExcerpt() : ""))
                        .toLowerCase()
                        .contains(normalizedQuery))
        .map(PostSummaryResponse::from)
        .collectList();
  }

  private boolean tagsContain(Post post, String tag) {
    if (post.getTags() == null || !post.getTags().isArray()) {
      return false;
    }
    for (var node : post.getTags()) {
      if (tag.equalsIgnoreCase(node.asText())) {
        return true;
      }
    }
    return false;
  }

  /** Same-category first (excluding self), backfilled with the next most-recent others. */
  private List<PostSummaryResponse> relatedTo(Post post, List<Post> published) {
    List<Post> sameCategory = new ArrayList<>();
    List<Post> others = new ArrayList<>();
    for (Post candidate : published) {
      if (candidate.getId().equals(post.getId())) {
        continue;
      }
      if (candidate.getCategory() != null && candidate.getCategory().equals(post.getCategory())) {
        sameCategory.add(candidate);
      } else {
        others.add(candidate);
      }
    }
    List<Post> combined = new ArrayList<>(sameCategory);
    for (Post candidate : others) {
      if (combined.size() >= MAX_RELATED) {
        break;
      }
      combined.add(candidate);
    }
    return combined.stream().limit(MAX_RELATED).map(PostSummaryResponse::from).toList();
  }

  /**
   * Blank slug on create/update → derive one from the title and de-dupe within the site (-2, -3,
   * ...). An explicitly-supplied slug that collides with a *different* post in the same site is
   * rejected outright — silently renaming a slug someone typed on purpose would break whatever link
   * they meant to publish.
   */
  private Mono<String> resolveSlug(
      Long siteId, Long excludeId, String requestedSlug, String title) {
    if (requestedSlug != null && !requestedSlug.isBlank()) {
      String slug = slugify(requestedSlug);
      Mono<Boolean> collision =
          excludeId != null
              ? postRepository.existsBySiteIdAndSlugAndIdNot(siteId, slug, excludeId)
              : postRepository.existsBySiteIdAndSlug(siteId, slug);
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
            ? postRepository.existsBySiteIdAndSlugAndIdNot(siteId, candidate, excludeId)
            : postRepository.existsBySiteIdAndSlug(siteId, candidate);
    return collision.flatMap(
        taken ->
            taken ? dedupeSlug(siteId, excludeId, baseSlug, attempt + 1) : Mono.just(candidate));
  }

  private static String slugify(String text) {
    if (text == null) {
      return "post";
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
    return result.length() > 0 ? result.toString() : "post";
  }
}
