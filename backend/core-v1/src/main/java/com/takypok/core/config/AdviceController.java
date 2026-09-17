package com.takypok.core.config;

import static com.takypok.core.util.LogUtil.getExceptionMessageChain;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.takypok.core.exception.ApplicationException;
import com.takypok.core.model.Message;
import com.takypok.core.model.ResultMessage;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.resource.NoResourceFoundException;
import org.springframework.web.server.MethodNotAllowedException;
import org.springframework.web.server.MissingRequestValueException;
import org.springframework.web.server.ServerWebInputException;

@Slf4j
@RestControllerAdvice
public class AdviceController {
  @ExceptionHandler(ApplicationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResultMessage<?> handleApplicationException(ApplicationException ex) {
    log.error("ApplicationException: {}", getExceptionMessageChain(ex));
    return ResultMessage.error(
        Message.get(
            Message.Application.ERROR, Message.get(ex.getApplication(), ex.getArgs()).message()));
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResultMessage<?> handleDuplicateKeyException(DataIntegrityViolationException ex) {
    log.error("DataIntegrityViolationException: {}", getExceptionMessageChain(ex));
    return ResultMessage.error(
        Message.get(Message.Application.ERROR, ex.getRootCause().getMessage()));
  }

  @ExceptionHandler(DuplicateKeyException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResultMessage<?> handleDuplicateKeyException(DuplicateKeyException ex) {
    log.error("DuplicateKeyException: {}", getExceptionMessageChain(ex));
    return ResultMessage.error(
        Message.get(Message.Application.ERROR, ex.getRootCause().getMessage()));
  }

  @ExceptionHandler(OptimisticLockingFailureException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public ResultMessage<?> handleOptimisticLockingFailureException(
      OptimisticLockingFailureException ex) {
    log.error("OptimisticLockingFailureException: {}", getExceptionMessageChain(ex));
    return ResultMessage.error(
        Message.get(
            Message.Application.ERROR,
            "This record was changed by someone else. Please reload and try again."));
  }

  @ExceptionHandler(MethodNotAllowedException.class)
  @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
  public ResultMessage<?> handleMethodNotAllowedException(MethodNotAllowedException ex) {
    log.error("MethodNotAllowedException: {}", getExceptionMessageChain(ex));
    return ResultMessage.error(Message.get(Message.Application.ERROR, ex.getReason()));
  }

  @ExceptionHandler(MissingRequestValueException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResultMessage<?> handleMissingRequestValueException(MissingRequestValueException ex) {
    log.error("MissingRequestValueException: {}", getExceptionMessageChain(ex));
    return ResultMessage.error(Message.get(Message.Application.ERROR, ex.getMessage()));
  }

  @ExceptionHandler(WebExchangeBindException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResultMessage<?> handleWebExchangeBindException(WebExchangeBindException ex) {
    log.error("WebExchangeBindException: {}", getExceptionMessageChain(ex));
    StringBuilder message = new StringBuilder();
    ex.getFieldErrors()
        .forEach(
            fieldError ->
                message
                    .append(fieldError.getField())
                    .append(" ")
                    .append(fieldError.getDefaultMessage())
                    .append(";"));
    return ResultMessage.error(Message.get(Message.Application.ERROR, message.toString()));
  }

  @ExceptionHandler(ServerWebInputException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResultMessage<?> handleServerWebInputException(ServerWebInputException ex) {

    if (ex.getRootCause() instanceof MismatchedInputException mismatchedInputException) {
      List<JsonMappingException.Reference> paths = mismatchedInputException.getPath();

      return ResultMessage.error(
          Message.get(
              Message.Application.ERROR,
              paths.get(paths.size() - 1).getFieldName()
                  + " not match "
                  + mismatchedInputException.getTargetType()));
    }
    log.error("ServerWebInputException: {}", getExceptionMessageChain(ex));
    return ResultMessage.error(Message.get(Message.Application.ERROR, ex.getReason()));
  }

  @ExceptionHandler(IllegalStateException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResultMessage<?> handleIllegalStateException(IllegalStateException ex) {
    log.error("IllegalStateException: {}", getExceptionMessageChain(ex));
    return ResultMessage.error(Message.get(Message.Application.ERROR, ex.getMessage()));
  }

  @ExceptionHandler(NoResourceFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResultMessage<?> handleNoResourceFoundException(NoResourceFoundException ex) {
    log.error("IllegalStateException: {}", getExceptionMessageChain(ex));
    return ResultMessage.error(Message.get(Message.Application.ERROR, ex.getReason()));
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResultMessage<?> handleException(Exception ex) {
    ex.printStackTrace();
    log.error("Exception: {}", getExceptionMessageChain(ex));
    return ResultMessage.error(Message.get(Message.Application.UNKNOWN_ERROR));
  }
}
