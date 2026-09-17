package com.takypok.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serial;
import java.io.Serializable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public final class ResultMessage<T> implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  private ResultStatus status;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private ResultMetaData metaData;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private T data;

  public ResultMessage(ResultStatus resultStatus, T data) {
    this.status = resultStatus;
    this.data = data;
  }

  public static <T> ResultMessage<T> success(T data) {
    return new ResultMessage<>(
        new ResultStatus(Message.Application.SUCCESS.code, Message.Application.SUCCESS.message),
        data);
  }

  public static <T> ResultMessage<T> error(Message message) {
    return new ResultMessage<>(new ResultStatus(message.code(), message.message()), null);
  }

  public static <T> ResultMessage<T> error(Message message, T data) {
    return new ResultMessage<>(new ResultStatus(message.code(), message.message()), data);
  }
}
