package com.example.stockticker;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
public class StockWebSocketConfig implements WebSocketConfigurer {
  @Override
  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    registry.addHandler(new StockWebSocketHandler(), "/ws").setAllowedOrigins("*");
  }
}
