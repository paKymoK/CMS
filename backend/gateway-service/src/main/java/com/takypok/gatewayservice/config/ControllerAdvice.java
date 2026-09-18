package com.takypok.gatewayservice.config;

import static com.takypok.core.util.LogUtil.getExceptionMessageChain;

import com.takypok.core.model.Message;
import com.takypok.core.model.ResultMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ControllerAdvice {
  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResultMessage<?> handleValidationExceptions(Exception ex) {
    log.error("Exception: ", ex);
    return ResultMessage.error(
        Message.get(Message.Application.ERROR, getExceptionMessageChain(ex).toString()));
  }
}
