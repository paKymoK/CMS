package com.takypok.contentservice.model.content;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Fully-dynamic fallback tier — mirrors Workflow's GenericDetail. Used for a content_type that
 * doesn't have (or doesn't yet need) a dedicated strict ContentFields class: any field the admin
 * submits is kept as-is, with no server-side shape validation beyond being valid JSON. Reach for a
 * strict class instead as soon as a type's fields are known and worth enforcing.
 */
public class GenericContentFields implements ContentFields {
  private final Map<String, Object> fields = new LinkedHashMap<>();

  @JsonAnySetter
  public void set(String key, Object value) {
    fields.put(key, value);
  }

  @JsonAnyGetter
  public Map<String, Object> getFields() {
    return fields;
  }
}
