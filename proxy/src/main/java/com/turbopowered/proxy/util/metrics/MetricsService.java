package com.turbopowered.proxy.util.metrics;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

/**
 * A simple, lightweight metrics registry for Prometheus-compatible text output.
 * Does not depend on heavy external libraries like Micrometer or Prometheus Client.
 */
public class MetricsService {

  private final Map<String, AtomicLong> counters = new ConcurrentHashMap<>();
  private final Map<String, Supplier<Number>> gauges = new ConcurrentHashMap<>();

  public MetricsService() {
  }

  /**
   * Registers or retrieves a counter.
   *
   * @param name The metric name (snake_case advisable).
   * @return The AtomicLong counter.
   */
  public AtomicLong counter(String name) {
    return counters.computeIfAbsent(name, k -> new AtomicLong(0));
  }

  /**
   * Registers a gauge.
   *
   * @param name The metric name.
   * @param supplier The provider of the value.
   */
  public void gauge(String name, Supplier<Number> supplier) {
    gauges.put(name, supplier);
  }

  /**
   * Scrapes all metrics and formats them in Prometheus text format (0.0.4).
   *
   * @return The formatted metrics string.
   */
  public String scrape() {
    StringBuilder sb = new StringBuilder();

    // Export Counters
    for (Map.Entry<String, AtomicLong> entry : counters.entrySet()) {
      sb.append("# TYPE ").append(entry.getKey()).append(" counter\n");
      sb.append(entry.getKey()).append(" ").append(entry.getValue().get()).append("\n");
    }

    // Export Gauges
    for (Map.Entry<String, Supplier<Number>> entry : gauges.entrySet()) {
      try {
        Number val = entry.getValue().get();
        if (val != null) {
          sb.append("# TYPE ").append(entry.getKey()).append(" gauge\n");
          sb.append(entry.getKey()).append(" ").append(val).append("\n");
        }
      } catch (Exception e) {
        // Ignore failing gauges
      }
    }

    return sb.toString();
  }
}
