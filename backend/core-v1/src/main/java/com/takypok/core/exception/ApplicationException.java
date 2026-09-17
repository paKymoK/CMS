package com.takypok.core.exception;

import com.takypok.core.model.Message;
import lombok.Getter;

@Getter
public class ApplicationException extends RuntimeException {
  private final Message.Application application;
  private final String[] args;
  private final Object data;

  public ApplicationException(Message.Application application, String... args) {
    super(Message.get(application, args).message());
    this.application = application;
    this.args = args;
    this.data = null;
  }

  public ApplicationException(Message.Application application, Object data, String... args) {
    super(Message.get(application, args).message());
    this.application = application;
    this.args = args;
    this.data = data;
  }
}
