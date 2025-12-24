package com.velocitypowered.proxy.antibot;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.collect.Sets;
import com.velocitypowered.proxy.config.VelocityConfiguration;
import java.net.InetAddress;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.velocitypowered.proxy.util.metrics.MetricsService;

/**
 * Service that handles Anti-Bot protection logic.
 * Tracks connections per IP and allows/denies requests based on configuration.
 */
public class AntiBotService {

  private static final Logger logger = LogManager.getLogger(AntiBotService.class);
  private final VelocityConfiguration config;
  private final MetricsService metricsService;
  private final Map<InetAddress, AtomicInteger> connectionsPerIp = new ConcurrentHashMap<>();
  private final Set<String> whitelist;
  private final Set<String> blacklist;
  private final Cache<InetAddress, Long> challengePending;
  private final Cache<InetAddress, Boolean> challengePassed;

  public AntiBotService(VelocityConfiguration config, MetricsService metricsService) {
    this.config = config;
    this.metricsService = metricsService;
    this.whitelist = Sets.newHashSet(config.getAntiBot().getWhitelist());
    this.blacklist = Sets.newHashSet(config.getAntiBot().getBlacklist());
    
    this.challengePending = Caffeine.newBuilder()
        .expireAfterWrite(config.getAntiBot().getChallengeRepeat(), TimeUnit.MILLISECONDS)
        .build();
    this.challengePassed = Caffeine.newBuilder()
        .expireAfterWrite(10, TimeUnit.MINUTES) // Verified IPs stay valid for 10 min
        .build();
  }

  /**
   * Checks if a connection from the given address should be allowed.
   *
   * @param address The remote address of the connection.
   * @return true if allowed, false otherwise.
   */
  public boolean allowConnection(InetAddress address) {
    if (!config.getAntiBot().isEnabled()) {
      return true;
    }

    String ip = address.getHostAddress();

    // 1. Check Blacklist (Fast Fail)
    if (blacklist.contains(ip)) {
      metricsService.counter("antibot_blocked_connections").incrementAndGet();
      return false;
    }

    // 2. Check Whitelist (Fast Pass)
    if (whitelist.contains(ip)) {
      return true;
    }

    // 3. Challenge Mode (Greylist)
    if (config.getAntiBot().isChallengeEnabled()) {
       if (challengePassed.getIfPresent(address) == null) {
         // Not verified yet. Check if pending.
         Long firstAttempt = challengePending.getIfPresent(address);
         if (firstAttempt == null) {
           // First time seeing this IP. Add to pending and reject.
           challengePending.put(address, System.currentTimeMillis());
           return false; // REJECT (Challenge started)
         } else {
           // Seen before. Check cooldown.
           long timeDiff = System.currentTimeMillis() - firstAttempt;
           if (timeDiff < config.getAntiBot().getChallengeCooldown()) {
             metricsService.counter("antibot_blocked_connections").incrementAndGet();
             return false; // REJECT (Too fast retry)
           }
           // Passed challenge
           challengePassed.put(address, true);
           challengePending.invalidate(address);
         }
       }
    }

    // 4. Check Connection Limit
    AtomicInteger count = connectionsPerIp.computeIfAbsent(address, k -> new AtomicInteger(0));
    int current = count.incrementAndGet();

    if (current > config.getAntiBot().getMaxConnectionsPerIp()) {
      // Decrement immediately since we are rejecting
      count.decrementAndGet();
      metricsService.counter("antibot_blocked_connections").incrementAndGet();
      if (current % 10 == 0) { // Log every 10th block to avoid spam
        logger.warn("Native Anti-Bot: Blocked connection from {} (Max connections reached: {})", ip, current);
      }
      return false;
    }

    return true;
  }

  /**
   * Called when a connection is closed to decrement the counter.
   *
   * @param address The remote address.
   */
  public void onConnectionClosed(InetAddress address) {
    if (!config.getAntiBot().isEnabled()) {
      return;
    }

    connectionsPerIp.computeIfPresent(address, (k, v) -> {
      int newVal = v.decrementAndGet();
      return newVal <= 0 ? null : v; // Remove entry if 0 to save memory
    });
  }
}
