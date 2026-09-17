package com.takypok.chatservice.model;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AnswerResponse {
  private String answer;
  private List<String> sources;
  private List<ImageRef> images;
}
