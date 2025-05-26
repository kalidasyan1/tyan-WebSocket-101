package com.example.stockticker.services;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class ThresholdMonitor {

  private final Map<String, Double> thresholds = Map.of(
      "AAPL", 190.00,
      "TSLA", 250.00
  );

  private final Set<String> triggered = ConcurrentHashMap.newKeySet();
  private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
  private final HttpClient httpClient = HttpClient.newHttpClient();

  @Autowired
  private PriceCacheService cacheService;

  @PostConstruct
  public void start() {
    scheduler.scheduleAtFixedRate(this::checkThresholds, 10, 20, TimeUnit.SECONDS);
  }

  private void checkThresholds() {
    Map<String, String> prices = cacheService.getCachedPrices();

    prices.forEach((symbol, strPrice) -> {
      if (!thresholds.containsKey(symbol)) return;

      try {
        double current = Double.parseDouble(strPrice);
        double threshold = thresholds.get(symbol);

        if (current >= threshold && triggered.add(symbol)) {
          System.out.printf("🚨 %s crossed threshold: %.2f ≥ %.2f%n", symbol, current, threshold);
          sendWebhookAlert(symbol, current, threshold);
        }

      } catch (Exception e) {
        System.err.println("❌ Error checking threshold for " + symbol + ": " + e.getMessage());
      }
    });
  }

  private void sendWebhookAlert(String symbol, double price, double threshold) {
    String json = """
        {
          "symbol": "%s",
          "price": %.2f,
          "threshold": %.2f,
          "message": "%s crossed the threshold!"
        }
        """.formatted(symbol, price, threshold, symbol);

    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create("http://localhost:8080/webhook/alert"))
        .header("Content-Type", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString(json))
        .build();

    httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
        .thenAccept(res -> System.out.println("✅ Webhook sent for " + symbol + ": " + res.statusCode()))
        .exceptionally(ex -> {
          System.err.println("❌ Webhook failed: " + ex.getMessage());
          return null;
        });
  }
}
