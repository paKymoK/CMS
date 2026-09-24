package com.takypok.chatservice.config;

import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "ai.rag.enabled", havingValue = "true", matchIfMissing = false)
public class AiConfig {

  // Backs the public CMS assistant (PublicAssistantService) only. Deliberately has no
  // defaultSystem here — the system prompt varies per site (language + persona), so it's set
  // per-request instead of baked into the shared bean.
  @Bean
  public ChatClient publicAssistantChatClient(AnthropicChatModel model) {
    return ChatClient.builder(model).build();
  }
}
