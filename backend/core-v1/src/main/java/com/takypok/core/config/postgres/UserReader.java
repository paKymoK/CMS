package com.takypok.core.config.postgres;

import static com.takypok.core.util.PostgresUtil.readTree;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.takypok.core.model.authentication.User;
import io.r2dbc.postgresql.codec.Json;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.lang.NonNull;

@Slf4j
@ReadingConverter
@RequiredArgsConstructor
public class UserReader implements Converter<Json, User> {
  private final ObjectMapper objectMapper;

  @Override
  public User convert(@NonNull Json source) {
    final JsonNode tree = readTree(source);
    try {
      return objectMapper.convertValue(tree, User.class);
    } catch (Exception ex) {
      log.error(ex.getMessage(), ex);
      return null;
    }
  }
}
