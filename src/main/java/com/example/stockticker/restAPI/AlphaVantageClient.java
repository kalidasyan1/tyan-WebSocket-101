package com.example.stockticker.restAPI;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@Service
public class AlphaVantageClient {

  private static final String API_KEY = "6YEZEDSQPKDQGA6H";
  private static final String BASE_URL = "https://www.alphavantage.co";

  private final WebClient webClient = WebClient.builder().baseUrl(BASE_URL).build();

  public Mono<String> getPrice(String symbol) {
    System.out.println("Fetching price for symbol: " + symbol);
    return webClient.get()
        .uri(uriBuilder -> uriBuilder.path("/query")
            .queryParam("function", "GLOBAL_QUOTE")
            .queryParam("symbol", symbol)
            .queryParam("apikey", API_KEY)
            .build())
        .retrieve()
        .bodyToMono(String.class);
  }
}
