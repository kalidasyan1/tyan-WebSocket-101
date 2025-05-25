package com.example.stockticker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.concurrent.*;

@Component
public class StockWebSocketHandler extends TextWebSocketHandler {

  private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
  private final List<String> stocks = List.of("AAPL", "MSFT", "GOOGL", "AMZN", "META", "TSLA", "NVDA");

  @Autowired
  private AlphaVantageClient alphaVantageClient;

  @Override
  public void afterConnectionEstablished(WebSocketSession session) {
    System.out.println("WebSocket connected: " + session.getId());
    scheduler.scheduleAtFixedRate(() -> {
      Map<String, String> livePrices = new ConcurrentHashMap<>();
      CountDownLatch latch = new CountDownLatch(stocks.size());

      for (String symbol : stocks) {
        alphaVantageClient.getPrice(symbol)
            .doOnSubscribe(sub -> System.out.println("Requesting " + symbol))
            .subscribe(json -> {
              System.out.println("Received price for " + symbol + ": " + json);
              String price = parsePriceFromJson(json, symbol);
              if (price != null) {
                livePrices.put(symbol, price);
              }
              latch.countDown();
            }, error -> {
              System.err.println("Failed to fetch price for " + symbol + ": " + error.getMessage());
              latch.countDown();
            });
      }

      try {
        latch.await(5, TimeUnit.SECONDS);
        StringBuilder jsonBuilder = new StringBuilder("{");
        for (String s : stocks) {
          String price = livePrices.getOrDefault(s, "null");
          jsonBuilder.append("\"").append(s).append("\":")
              .append(price).append(",");
        }
        if (!stocks.isEmpty()) {
          jsonBuilder.setLength(jsonBuilder.length() - 1); // remove last comma
        }
        jsonBuilder.append("}");

        session.sendMessage(new TextMessage(jsonBuilder.toString()));
      } catch (Exception e) {
        e.printStackTrace();
      }

    }, 0, 15, TimeUnit.SECONDS); // Respect Alpha Vantage rate limits
  }

  // Helper to extract "05. price" field from Alpha Vantage JSON
  private String parsePriceFromJson(String json, String symbol) {
    try {
      String[] lines = json.split("\"05. price\"\\s*:\\s*\"");
      if (lines.length > 1) {
        String price = lines[1].split("\"")[0];
        return String.format("%.2f", Double.parseDouble(price));
      }
    } catch (Exception e) {
      System.err.println("Error parsing price for " + symbol + ": " + e.getMessage());
    }
    return null;
  }
}
