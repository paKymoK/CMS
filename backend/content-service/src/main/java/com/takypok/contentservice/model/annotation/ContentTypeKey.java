package com.takypok.contentservice.model.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a ContentFields implementation with the content_item.content_type value it serializes for.
 *
 * <p>The registry (see ContentFieldsRegistryConfig) is keyed by this stable string rather than the
 * Java class name, matching content_item's own content_type column, so renaming or moving the class
 * never breaks reading old rows.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ContentTypeKey {
  String value();
}
