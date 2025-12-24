package com.velocitypowered.proxy.util.metrics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import java.util.concurrent.atomic.AtomicLong;

class MetricsServiceTest {

  @Test
  void testCounterIncrement() {
    MetricsService service = new MetricsService();
    AtomicLong counter = service.counter("test_counter");
    
    assertEquals(0, counter.get());
    counter.incrementAndGet();
    assertEquals(1, counter.get());
  }

  @Test
  void testGauge() {
    MetricsService service = new MetricsService();
    service.gauge("test_gauge", () -> 42);

    String output = service.scrape();
    assertTrue(output.contains("# TYPE test_gauge gauge"));
    assertTrue(output.contains("test_gauge 42"));
  }

  @Test
  void testScrapeFormat() {
    MetricsService service = new MetricsService();
    service.counter("my_counter").set(10);
    service.gauge("my_gauge", () -> 5.5);

    String output = service.scrape();
    
    // Check Counter
    assertTrue(output.contains("# TYPE my_counter counter"));
    assertTrue(output.contains("my_counter 10"));

    // Check Gauge
    assertTrue(output.contains("# TYPE my_gauge gauge"));
    assertTrue(output.contains("my_gauge 5.5"));
  }
}
