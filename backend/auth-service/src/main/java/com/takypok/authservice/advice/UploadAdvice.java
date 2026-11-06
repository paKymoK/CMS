package com.takypok.authservice.advice;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.ModelAndView;

/**
 * A picture over the container's upload limit fails before the controller runs, and the catch-all
 * advice would answer with JSON. The profile page is the only upload, so send the user back to it
 * with the size message instead.
 */
@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class UploadAdvice {

  @ExceptionHandler(MaxUploadSizeExceededException.class)
  public ModelAndView tooBig() {
    return new ModelAndView("redirect:/profile?avatarError=size");
  }
}
