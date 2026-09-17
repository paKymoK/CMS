package com.takypok.chatservice.model.event;

import java.util.List;

/**
 * Envelope published to Redis: the event plus the subs it's meant for, so every chat-service
 * instance can filter against its own local WebSocket sessions without a DB round-trip.
 */
public record ChatBroadcast(List<String> subs, ChatEvent event) {}
