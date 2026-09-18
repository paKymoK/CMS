package com.takypok.chatservice.model;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PublicAnswerResponse {
  private String answer;
  private List<String> sources;
}
