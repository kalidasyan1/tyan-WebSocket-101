package com.example.demo;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.config.annotation.*;

import java.time.LocalTime;
import java.util.concurrent.*;

@Configuration
@EnableWebSocket
public class WebSocketHandler implements WebSocketConfigurer {

  @Override
  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    registry.addHandler(new MyHandler(), "/ws").setAllowedOrigins("*");
  }

  static class MyHandler extends TextWebSocketHandler {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
      System.out.println("WebSocket connected");

      scheduler.scheduleAtFixedRate(() -> {
        try {
          String time = LocalTime.now().withNano(0).toString();
          session.sendMessage(new TextMessage("WS message at " + time));
        } catch (Exception e) {
          System.out.println("Error sending WebSocket message: " + e.getMessage());
        }
      }, 0, 3, TimeUnit.SECONDS);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
      System.out.println("Received: " + message.getPayload());
    }
  }
}
