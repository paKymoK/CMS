package com.takypok.chatservice.config;

import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaChatOptions;
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

  // disableThinking() matters a lot here: Qwen3 models (the default OLLAMA_CHAT_MODEL) emit a
  // long chain-of-thought block before their real answer unless told not to — the same reason
  // the old CRM assistant's Ollama client both set this option AND started its system prompt
  // with "/no_think". On modest local hardware that reasoning pass alone can blow well past a
  // 10-minute read timeout for a single RAG answer.
  @Bean
  @ConditionalOnProperty(name = "ai.chat.provider", havingValue = "ollama")
  public ChatClient publicAssistantChatClientOllama(OllamaChatModel model) {
    return ChatClient.builder(model)
        .defaultOptions(OllamaChatOptions.builder().disableThinking().build())
        .build();
  }
}
