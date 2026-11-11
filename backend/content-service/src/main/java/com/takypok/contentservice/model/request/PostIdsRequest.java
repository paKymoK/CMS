package com.takypok.contentservice.model.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/** A set of posts to ask about before acting on them; at most 100 at a time. */
@Getter
@Setter
public class PostIdsRequest {
  @NotEmpty
  @Size(max = 100)
  private List<Long> ids;
}
