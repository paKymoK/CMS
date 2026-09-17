package com.takypok.chatservice.config.postgres;

import static com.takypok.core.util.PostgresUtil.readTree;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.takypok.chatservice.model.Attachment;
import io.r2dbc.postgresql.codec.Json;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.lang.NonNull;

@Slf4j
@ReadingConverter
@RequiredArgsConstructor
public class AttachmentListReader implements Converter<Json, List<Attachment>> {
  private final ObjectMapper objectMapper;

  @Override
  public List<Attachment> convert(@NonNull Json source) {
    final JsonNode tree = readTree(source);
    try {
      return objectMapper.convertValue(
          tree, new com.fasterxml.jackson.core.type.TypeReference<>() {});
    } catch (Exception ex) {
      log.error(ex.getMessage(), ex);
      return Collections.emptyList();
    }
  }
}
