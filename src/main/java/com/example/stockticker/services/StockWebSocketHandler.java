package com.example.stockticker.services;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;


@Component
public class StockWebSocketHandler extends TextWebSocketHandler {

  private final PriceCacheService cacheService;

  @Autowired
  public StockWebSocketHandler(PriceCacheService cacheService) {
    this.cacheService = cacheService;
  }

  @Override
  public void afterConnectionEstablished(WebSocketSession session) {
    System.out.println("WebSocket connected: " + session.getId());

    Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
      Map<String, String> prices = cacheService.getCachedPrices();

      try {
        StringBuilder json = new StringBuilder("{");
        for (String symbol : cacheService.getTrackedSymbols()) {
          String price = prices.getOrDefault(symbol, "null");
          json.append("\"").append(symbol).append("\":").append(price).append(",");
        }
        if (!prices.isEmpty()) {
          json.setLength(json.length() - 1);
        }
        json.append("}");

        session.sendMessage(new TextMessage(json.toString()));
      } catch (Exception e) {
        e.printStackTrace();
      }
    }, 0, 20, TimeUnit.SECONDS);
  }
}
