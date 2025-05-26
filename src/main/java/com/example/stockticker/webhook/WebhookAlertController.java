package com.example.stockticker.webhook;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/webhook")
public class WebhookAlertController {

  @PostMapping("/alert")
  public ResponseEntity<String> receiveAlert(@RequestBody Map<String, Object> payload) {
    System.out.println("🚨 ALERT RECEIVED:");
    payload.forEach((k, v) -> System.out.println(k + ": " + v));
    return ResponseEntity.ok("Alert received");
  }
}
