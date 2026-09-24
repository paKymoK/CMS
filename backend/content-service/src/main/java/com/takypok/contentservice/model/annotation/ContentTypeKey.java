package com.takypok.contentservice.model.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a ContentFields implementation with the content_type.key it serializes for.
 *
 * <p>Deliberately different from Workflow's TicketDetail persistence, which embeds the Java class's
 * fully-qualified name directly in the stored jsonb (io.r2dbc.postgresql, via
 * TicketDetailReader/Writer's CLAZZ_NAME) — that couples every stored row to a specific Java
 * package path, so renaming or moving the class breaks deserializing old rows. Here the registry
 * (see ContentFieldsRegistry) is keyed by this stable string instead, matching content_item's own
 * content_type column, so refactoring the Java class is safe.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ContentTypeKey {
  String value();
}
