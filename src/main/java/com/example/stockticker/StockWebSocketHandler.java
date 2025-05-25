package com.example.stockticker;

import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.*;
import java.util.concurrent.*;

public class StockWebSocketHandler extends TextWebSocketHandler {

  private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
  private final Map<String, Double> stockPrices = new HashMap<>();
  private final List<String> stocks = List.of("AAPL", "MSFT", "GOOGL", "AMZN", "META", "TSLA", "NVDA");

  public StockWebSocketHandler() {
    stocks.forEach(s -> stockPrices.put(s, Double.valueOf(100.0 + Math.random() * 100))); // initialize mock prices
  }

  @Override
  public void afterConnectionEstablished(WebSocketSession session) {
    scheduler.scheduleAtFixedRate(() -> {
      try {
        stocks.forEach(s -> stockPrices.compute(s, (k, v) -> Double.valueOf((v + (Math.random() - 0.5)))));

        StringBuilder json = new StringBuilder("{");
        for (String s : stocks) {
          json.append("\"").append(s).append("\":")
              .append(String.format("%.2f", stockPrices.get(s))).append(",");
        }
        json.setLength(json.length() - 1); // remove last comma
        json.append("}");

        session.sendMessage(new TextMessage(json.toString()));
      } catch (Exception e) {
        e.printStackTrace();
      }
    }, 0, 3, TimeUnit.SECONDS);
  }
}
