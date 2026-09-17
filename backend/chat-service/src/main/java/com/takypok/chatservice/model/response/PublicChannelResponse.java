package com.takypok.chatservice.model.response;

import java.util.UUID;

public record PublicChannelResponse(UUID id, String name, long memberCount) {}
