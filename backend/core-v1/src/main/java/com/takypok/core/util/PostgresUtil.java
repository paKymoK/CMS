package com.takypok.core.util;

import static com.takypok.core.config.ConfigObjectMapper.objectMapper;

import com.fasterxml.jackson.databind.JsonNode;
import io.r2dbc.postgresql.codec.Json;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PostgresUtil {
  public static final String CLAZZ_NAME = "_clazz";

  public static JsonNode readTree(Json source) {
    try {
      return objectMapper().readTree(source.asArray());
    } catch (IOException e) {
      log.error("An error occurred while trying to read tree: {}", e.getMessage(), e);
      throw new IllegalArgumentException(e);
    }
  }
}
