package com.takypok.contentservice.model.content;

/**
 * Marker interface for content_item.data's type-specific fields. Every content type implements this
 * with real Bean Validation annotations (e.g. FaqFields). See ContentTypeKey for how an
 * implementation is matched to content_item.content_type.
 */
public interface ContentFields {}
