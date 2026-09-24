package com.takypok.contentservice.model.content;

/**
 * Marker interface for content_item.data's type-specific fields — mirrors Workflow's TicketDetail
 * marker. A content type either implements this directly with real Bean Validation annotations (the
 * "strict tier", e.g. FaqFields) or falls back to {@link GenericContentFields} (the "flexible
 * tier") when it has no dedicated class yet. See ContentTypeKey for how a concrete implementation
 * is matched to a content_type.key.
 */
public interface ContentFields {}
