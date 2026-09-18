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
  private static final String SYSTEM_PROMPT =
      """
        /no_think

        Bạn là trợ lý hỗ trợ người dùng sử dụng hệ thống CRM.
        Bạn PHẢI trả lời HOÀN TOÀN bằng tiếng Việt, tuyệt đối không dùng bất kỳ ngôn ngữ nào khác.
        Không được bịa đặt thông tin. Trả lời ngắn gọn, rõ ràng, bằng tiếng Việt.
        """;

  // Untouched — still backs the pre-existing employee-facing CRM assistant (AssistantService)
  // and CodeReviewService, neither of which is part of the public CMS chatbot this platform
  // added. Left as Ollama/qwen3.5 exactly as forked; see docs/cms-platform-plan.md's Phase 5
  // status for why these weren't migrated or removed.
  @Bean
  public ChatClient chatClient(OllamaChatModel model) {
    return ChatClient.builder(model).defaultSystem(SYSTEM_PROMPT).build();
  }

  // Backs the new public CMS assistant (PublicAssistantService) only. Deliberately has no
  // defaultSystem here — the system prompt varies per site (language + persona), so it's set
  // per-request instead of baked into the shared bean.
  @Bean
  public ChatClient publicAssistantChatClient(AnthropicChatModel model) {
    return ChatClient.builder(model).build();
  }
}
