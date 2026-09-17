package com.takypok.chatservice.model.event;

public record ChatEvent(ChatEventType type, Object payload) {
  public enum ChatEventType {
    MESSAGE_CREATED,
    PARTICIPANT_ADDED,
    PARTICIPANT_REMOVED,
    CONVERSATION_RENAMED,
    TYPING,
    PRESENCE_CHANGED,
    RECEIPT_UPDATED,
    REACTION_UPDATED
  }
}
