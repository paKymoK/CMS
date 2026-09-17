package com.takypok.contentservice.config.postgres;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.r2dbc.postgresql.codec.Json;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.lang.NonNull;

@Slf4j
@ReadingConverter
@RequiredArgsConstructor
public class JsonNodeReader implements Converter<Json, JsonNode> {
  private final ObjectMapper objectMapper;

  @Override
  public JsonNode convert(@NonNull Json source) {
    try {
      return objectMapper.readTree(source.asArray());
    } catch (IOException e) {
      log.error("Unable to read JsonNode: {}", e.getMessage(), e);
      throw new IllegalArgumentException(e);
    }
  }
}
