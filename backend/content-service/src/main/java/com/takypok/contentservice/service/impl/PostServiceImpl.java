package com.takypok.contentservice.service.impl;

import com.takypok.contentservice.model.entity.Post;
import com.takypok.contentservice.model.request.PostCreateRequest;
import com.takypok.contentservice.model.request.PostUpdateRequest;
import com.takypok.contentservice.repository.PostRepository;
import com.takypok.contentservice.service.PostService;
import com.takypok.core.exception.ApplicationException;
import com.takypok.core.model.Message;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
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
    return postRepository.save(post);
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
              return postRepository.save(post);
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
}
