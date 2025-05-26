package com.example.stockticker;

import com.example.stockticker.clients.websocketAPI.FinnhubWebSocketClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;


@SpringBootApplication
public class StockTickerApplication {

  @Autowired
  private FinnhubWebSocketClient finnhubClient;

  public static void main(String[] args) {
    SpringApplication.run(StockTickerApplication.class, args);
  }

  @EventListener(ApplicationReadyEvent.class)
  public void connectFinnhub() throws Exception {
    finnhubClient.connect("d0ptqg9r01qgccubbo30d0ptqg9r01qgccubbo3g");
  }
}
