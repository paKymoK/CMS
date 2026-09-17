package com.takypok.authservice.advice;

import com.takypok.core.model.Message;
import com.takypok.core.model.ResultMessage;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Component
@ControllerAdvice
@Slf4j
public class AdviceController {

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ResultMessage<String>> handleResourceNotFoundException(Exception ex) {
    log.error("Undefined Error: ", ex);
    return new ResponseEntity<>(
        ResultMessage.error(Message.get(Message.Application.UNKNOWN_ERROR), ex.getMessage()),
        HttpStatus.FORBIDDEN);
  }

  @ExceptionHandler(DuplicateKeyException.class)
  public ResponseEntity<ResultMessage<String>> handleResourceNotFoundException(
      DuplicateKeyException ex) {
    return new ResponseEntity<>(
        ResultMessage.error(
            Message.get(Message.Application.ERROR),
            Objects.requireNonNull(ex.getRootCause()).getMessage()),
        HttpStatus.FORBIDDEN);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ResultMessage<String>> handleResourceNotFoundException(
      MethodArgumentNotValidException ex) {
    StringBuilder errorMessage = new StringBuilder();
    for (ObjectError error : ex.getAllErrors()) {
      if (error instanceof FieldError fieldError) {
        String fieldName = fieldError.getField();
        String message = fieldError.getDefaultMessage();
        if (!errorMessage.isEmpty()) {
          errorMessage.append(", ");
        }
        errorMessage.append(fieldName).append(" ").append(message);
      }
    }

    return new ResponseEntity<>(
        ResultMessage.error(Message.get(Message.Application.ERROR), errorMessage.toString()),
        HttpStatus.FORBIDDEN);
  }
}
