package com.takypok.chatservice.model.response;

import java.util.List;

// Carries the full list of reactor subs (not just a count) so each client can work out "mine"
// itself — a single broadcast/response fans out to multiple viewers, and whether the caller's
// own sub is in the list is inherently viewer-specific, not something the server can bake in once.
public record ReactionSummary(String emoji, List<String> subs) {}
