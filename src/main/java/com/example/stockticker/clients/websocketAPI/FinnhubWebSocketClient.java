package com.example.stockticker.clients.websocketAPI;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.websocket.ClientEndpoint;
import javax.websocket.ContainerProvider;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import org.springframework.stereotype.Component;


@Component
@ClientEndpoint
public class FinnhubWebSocketClient {

  private final Map<String, String> latestPrices = new ConcurrentHashMap<>();
  private final AtomicLong lastTradeTime = new AtomicLong(System.currentTimeMillis());
  private Session session;

  public void connect(String token) throws Exception {
    String uri = "wss://ws.finnhub.io?token=" + token;
    WebSocketContainer container = ContainerProvider.getWebSocketContainer();
    container.connectToServer(this, URI.create(uri));
  }

  @OnOpen
  public void onOpen(Session session) {
    this.session = session;
    List<String> symbols = List.of("AAPL", "MSFT", "GOOGL", "AMZN", "META", "TSLA", "NVDA");
    for (String symbol : symbols) {
      session.getAsyncRemote().sendText("{\"type\":\"subscribe\",\"symbol\":\"" + symbol + "\"}");
    }
    System.out.println("✅ Connected to Finnhub");
  }

  @OnMessage
  public void onMessage(String message) {
    if (message.contains("\"type\":\"trade\"")) {
      lastTradeTime.set(System.currentTimeMillis());
      parseTrades(message);
    }
  }

  private void parseTrades(String json) {
    Pattern pattern = Pattern.compile("\"s\":\"(\\w+)\",\"p\":(\\d+\\.\\d+)");
    Matcher matcher = pattern.matcher(json);
    while (matcher.find()) {
      latestPrices.put(matcher.group(1), matcher.group(2));
    }
  }

  public boolean isStale(long timeoutMillis) {
    return System.currentTimeMillis() - lastTradeTime.get() > timeoutMillis;
  }

  public Map<String, String> getLatestPricesSnapshot() {
    return new HashMap<>(latestPrices);
  }
}
