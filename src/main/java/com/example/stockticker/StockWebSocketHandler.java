package com.example.stockticker;

import com.example.stockticker.restAPI.AlphaVantageClient;
import com.example.stockticker.websocketAPI.FinnhubWebSocketClient;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;


@Component
public class StockWebSocketHandler extends TextWebSocketHandler {

  private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
  private final List<String> stocks = List.of("AAPL", "MSFT", "GOOGL", "AMZN", "META", "TSLA", "NVDA");

  private final FinnhubWebSocketClient finnhub;
  private final AlphaVantageClient alpha;

  @Autowired
  public StockWebSocketHandler(FinnhubWebSocketClient finnhub, AlphaVantageClient alpha) {
    this.finnhub = finnhub;
    this.alpha = alpha;
  }

  @Override
  public void afterConnectionEstablished(WebSocketSession session) {
    System.out.println("WebSocket connected: " + session.getId());

    scheduler.scheduleAtFixedRate(() -> {
      final Map<String, String> prices = new ConcurrentHashMap<>();

      if (!finnhub.isStale(19_000)) { // 15s without trade = stale
        prices.putAll(finnhub.getLatestPricesSnapshot());
        System.out.println("✅ Using Finnhub live prices");
      } else {
        System.out.println("⚠️ Finnhub stale or down. Polling Alpha Vantage...");
        CountDownLatch latch = new CountDownLatch(stocks.size());

        for (String symbol : stocks) {
          alpha.getPrice(symbol).subscribe(json -> {
            String price = parsePriceFromJson(json, symbol);
            if (price != null) {
              prices.put(symbol, price);
            }
            latch.countDown();
          }, err -> {
            System.err.println("AlphaVantage error for " + symbol);
            latch.countDown();
          });
        }

        try {
          latch.await(5, TimeUnit.SECONDS);
        } catch (InterruptedException ignored) {
        }
      }

      try {
        StringBuilder json = new StringBuilder("{");
        for (String s : stocks) {
          String price = prices.getOrDefault(s, "null");
          json.append("\"").append(s).append("\":").append(price).append(",");
        }
        if (!stocks.isEmpty()) {
          json.setLength(json.length() - 1);
        }
        json.append("}");

        session.sendMessage(new TextMessage(json.toString()));
      } catch (Exception e) {
        e.printStackTrace();
      }
    }, 0, 20, TimeUnit.SECONDS); // send to browser every 15 seconds
  }

  private String parsePriceFromJson(String json, String symbol) {
    try {
      String[] parts = json.split("\"05. price\"\\s*:\\s*\"");
      if (parts.length > 1) {
        return String.format("%.2f", Double.parseDouble(parts[1].split("\"")[0]));
      }
    } catch (Exception e) {
      System.err.println("Failed to parse Alpha Vantage response for " + symbol);
    }
    return null;
  }
}
