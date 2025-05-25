package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

@RestController
public class PollingController {

  @GetMapping("/poll")
  public String poll() throws InterruptedException {
    TimeUnit.SECONDS.sleep(2); // Simulate delay
    return "{\"message\":\"Long poll at " + LocalTime.now().withNano(0) + "\"}";
  }
}
