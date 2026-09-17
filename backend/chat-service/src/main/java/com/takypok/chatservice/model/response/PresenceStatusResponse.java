package com.takypok.chatservice.model.response;

import java.time.ZonedDateTime;

public record PresenceStatusResponse(boolean online, ZonedDateTime lastSeenAt) {}
