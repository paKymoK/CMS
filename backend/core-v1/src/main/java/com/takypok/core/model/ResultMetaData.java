package com.takypok.core.model;

import java.io.Serializable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ResultMetaData implements Serializable {

  private static final long serialVersionUID = 1L;

  private String requestId;

  private String signature;

  private long timestamp = System.currentTimeMillis();

  public ResultMetaData(String requestId, String signature) {
    this.requestId = requestId;
    this.signature = signature;
  }
}
