package com.takypok.contentservice.config.postgres;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.r2dbc.postgresql.codec.Json;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.lang.NonNull;

@Slf4j
@WritingConverter
@RequiredArgsConstructor
public class JsonNodeWriter implements Converter<JsonNode, Json> {
  private final ObjectMapper objectMapper;

  @Override
  public Json convert(@NonNull JsonNode source) {
    try {
      return Json.of(objectMapper.writeValueAsBytes(source));
    } catch (JsonProcessingException e) {
      log.error("Unable to write JsonNode: {}", e.getMessage(), e);
      throw new IllegalArgumentException(e);
    }
  }
}
