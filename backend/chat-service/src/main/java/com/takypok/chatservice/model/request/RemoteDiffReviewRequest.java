package com.takypok.chatservice.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** A GitHub pull request or GitLab merge request URL to fetch and review the diff of. */
@Getter
@Setter
@NoArgsConstructor
public class RemoteDiffReviewRequest {
  @NotBlank private String url;
}
