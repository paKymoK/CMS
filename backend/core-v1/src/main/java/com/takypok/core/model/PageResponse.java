package com.takypok.core.model;

import java.util.List;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class PageResponse<T> {
  private List<T> content;
  private long page;
  private long size;
  private long totalElements;
  private long totalPages;
}
