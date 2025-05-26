package com.example.stockticker.services;


import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;

@Service
public class PriceCacheService {

  private final PriceService priceService;
  private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
  private volatile Map<String, String> cachedPrices = new HashMap<>();

  @Autowired
  public PriceCacheService(PriceService priceService) {
    this.priceService = priceService;
  }

  @PostConstruct
  public void startCacheUpdater() {
    scheduler.scheduleAtFixedRate(this::refreshPrices, 0, 20, TimeUnit.SECONDS);
  }

  private void refreshPrices() {
    try {
      cachedPrices = priceService.getLatestPrices();
      System.out.println("🧠 [PriceCacheService] Cache updated");
    } catch (Exception e) {
      System.err.println("❌ Failed to update price cache: " + e.getMessage());
    }
  }

  public Map<String, String> getCachedPrices() {
    return cachedPrices;
  }

  public List<String> getTrackedSymbols() {
    return priceService.getTrackedSymbols();
  }
}
