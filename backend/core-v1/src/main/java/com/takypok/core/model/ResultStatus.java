package com.takypok.core.model;

import java.io.Serial;
import java.io.Serializable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ResultStatus implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  private String code;
  private String message;

  public ResultStatus(String code, String message) {
    this.code = code;
    this.message = message;
  }
}
