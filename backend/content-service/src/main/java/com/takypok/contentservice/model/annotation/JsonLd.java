package com.takypok.contentservice.model.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A page's structured-data (JSON-LD) text: blank, or JSON that is an object or a list of objects,
 * at most {@link JsonLdValidator#MAX_LENGTH} characters.
 */
@Documented
@Constraint(validatedBy = JsonLdValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonLd {
  String message() default
      "must be valid JSON-LD: a JSON object or a list of objects, up to 10000 characters";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
