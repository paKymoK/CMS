package com.takypok.contentservice.model.annotation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class JsonLdValidator implements ConstraintValidator<JsonLd, String> {
  static final int MAX_LENGTH = 10_000;
  private static final ObjectMapper MAPPER = new ObjectMapper();

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null || value.isBlank()) return true;
    if (value.length() > MAX_LENGTH) return false;
    try {
      JsonNode node = MAPPER.readTree(value);
      if (node.isObject()) return true;
      if (!node.isArray() || node.isEmpty()) return false;
      for (JsonNode item : node) {
        if (!item.isObject()) return false;
      }
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}
