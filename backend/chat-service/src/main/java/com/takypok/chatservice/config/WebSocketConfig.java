package com.takypok.chatservice.config;

import com.takypok.chatservice.websocket.ChatWebSocketHandler;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;

@Configuration
@RequiredArgsConstructor
public class WebSocketConfig {

  @Bean
  public HandlerMapping chatHandlerMapping(ChatWebSocketHandler chatWebSocketHandler) {
    Map<String, WebSocketHandler> map = new HashMap<>();
    map.put("/web-socket/chat", chatWebSocketHandler);
    return new SimpleUrlHandlerMapping(map, 1);
  }

  @Bean
  public WebSocketHandlerAdapter chatWebSocketHandlerAdapter() {
    return new WebSocketHandlerAdapter();
  }
}
