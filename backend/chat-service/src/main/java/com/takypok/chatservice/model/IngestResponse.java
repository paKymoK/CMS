package com.takypok.chatservice.model;

import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IngestResponse {
  private List<String> ingested;
  private List<String> duplicates;
  private List<String> failed;

  /** Chunk count per successfully ingested filename. */
  private Map<String, Integer> chunkCounts;

  /** Error message per failed filename. */
  private Map<String, String> errors;
}
