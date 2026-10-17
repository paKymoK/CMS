package com.takypok.contentservice.model.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.takypok.contentservice.model.content.ContentFields;
import com.takypok.core.exception.ApplicationException;
import com.takypok.core.model.Message;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Resolves content_item.data against the ContentFields class registered for its content_type and
 * validates it with the standard Bean Validator — the same explicit "convert, then
 * validator.validate() by hand" pattern Workflow's TicketMapper uses for Ticket.detail, since
 * {@code @Valid} can't cascade into a raw JsonNode. Nothing reaches ContentItemRepository without
 * passing through here first.
 */
@Component
@RequiredArgsConstructor
public class ContentItemMapper {
  private final ObjectMapper objectMapper;
  private final Validator validator;
  private final Map<String, Class<? extends ContentFields>> contentFieldsRegistry;

  /**
   * Rejects a content type with no registered {@code @ContentTypeKey} class, then validates {@code
   * data} against that class. The Java classes are the only type registry (there is no content_type
   * table), so a new type needs code and an unknown key can never be stored.
   *
   * @return the canonical, validated JSON for this content type — re-serialized from the resolved
   *     ContentFields object, so a type's persisted data never carries fields it doesn't declare,
   *     even though the shared ObjectMapper otherwise ignores unknown properties on the way in (see
   *     core-v1's ConfigObjectMapper).
   */
  public Mono<JsonNode> resolveAndValidate(String contentType, JsonNode data) {
    return Mono.fromCallable(() -> validate(contentType, data));
  }

  private JsonNode validate(String contentType, JsonNode data) {
    Class<? extends ContentFields> targetClass = contentFieldsRegistry.get(contentType);
    if (targetClass == null) {
      throw new ApplicationException(
          Message.Application.ERROR, "Unknown content type: " + contentType);
    }
    JsonNode source = data != null ? data : objectMapper.createObjectNode();

    ContentFields fields;
    try {
      fields = objectMapper.convertValue(source, targetClass);
    } catch (IllegalArgumentException e) {
      throw new ApplicationException(
          Message.Application.ERROR, "data: invalid shape for content type " + contentType);
    }

    Set<ConstraintViolation<ContentFields>> violations = validator.validate(fields);
    if (!violations.isEmpty()) {
      String message =
          violations.stream()
              .map(v -> v.getPropertyPath() + ": " + v.getMessage())
              .collect(Collectors.joining(", "));
      throw new ApplicationException(Message.Application.ERROR, "data: " + message);
    }

    return objectMapper.valueToTree(fields);
  }
}
