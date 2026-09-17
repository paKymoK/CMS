package com.takypok.chatservice.config;

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

  @Bean
  public ChatClient chatClient(OllamaChatModel model) {
    return ChatClient.builder(model).defaultSystem(SYSTEM_PROMPT).build();
  }
}
