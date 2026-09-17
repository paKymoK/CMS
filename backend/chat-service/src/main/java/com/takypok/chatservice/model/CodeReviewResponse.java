package com.takypok.chatservice.model;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CodeReviewResponse {
  private String review;
  private List<String> rulesConsidered;
}
