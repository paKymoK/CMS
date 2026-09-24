package com.takypok.chatservice.config;

import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "ai.rag.enabled", havingValue = "true", matchIfMissing = false)
public class AiConfig {

  // Backs the public CMS assistant (PublicAssistantService). Deliberately has no defaultSystem
  // here — the system prompt varies per site (language + persona), so it's set per-request
  // instead of baked into the shared bean. Anthropic is the production model; set
  // ai.chat.provider=ollama to swap in a local Ollama model for testing without needing a real
  // ANTHROPIC_API_KEY. Exactly one of these two beans is ever active, so PublicAssistantService's
  // ChatClient dependency still resolves by type regardless of which is active.
  @Bean
  @ConditionalOnProperty(
      name = "ai.chat.provider",
      havingValue = "anthropic",
      matchIfMissing = true)
  public ChatClient publicAssistantChatClient(AnthropicChatModel model) {
    return ChatClient.builder(model).build();
  }

  @Bean
  @ConditionalOnProperty(name = "ai.chat.provider", havingValue = "ollama")
  public ChatClient publicAssistantChatClientOllama(OllamaChatModel model) {
    return ChatClient.builder(model).build();
  }
}
