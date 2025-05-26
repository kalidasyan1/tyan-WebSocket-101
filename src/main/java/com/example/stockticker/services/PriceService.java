package com.example.stockticker.services;

import com.example.stockticker.clients.restAPI.AlphaVantageClient;
import com.example.stockticker.clients.websocketAPI.FinnhubWebSocketClient;
import java.util.*;
import java.util.concurrent.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PriceService {

  private final FinnhubWebSocketClient finnhub;
  private final AlphaVantageClient alpha;
  private final List<String> stocks = List.of("AAPL", "MSFT", "GOOGL", "AMZN", "META", "TSLA", "NVDA");

  @Autowired
  public PriceService(FinnhubWebSocketClient finnhub, AlphaVantageClient alpha) {
    this.finnhub = finnhub;
    this.alpha = alpha;
  }

  public List<String> getTrackedSymbols() {
    return stocks;
  }

  public Map<String, String> getLatestPrices() {
    final Map<String, String> prices = new ConcurrentHashMap<>();

    if (!finnhub.isStale(19_000)) {
      prices.putAll(finnhub.getLatestPricesSnapshot());
      System.out.println("✅ [PriceService] Using Finnhub live prices");
    } else {
      System.out.println("⚠️  [PriceService] Finnhub stale. Polling Alpha Vantage...");
      CountDownLatch latch = new CountDownLatch(stocks.size());

      for (String symbol : stocks) {
        alpha.getPrice(symbol).subscribe(json -> {
          String price = parsePriceFromJson(json, symbol);
          if (price != null) {
            System.out.printf("📈 [PriceService] %s: %s%n", symbol, price);
            prices.put(symbol, price);
          }
          latch.countDown();
        }, err -> {
          System.err.println("AlphaVantage error for " + symbol + ": " + err.getMessage());
          latch.countDown();
        });
      }

      try {
        latch.await(5, TimeUnit.SECONDS);
      } catch (InterruptedException ignored) {}
    }

    return prices;
  }

  private String parsePriceFromJson(String json, String symbol) {
    try {
      System.out.println("Parsing Alpha Vantage response for " + symbol + ": " + json);
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
