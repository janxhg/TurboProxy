package com.velocitypowered.proxy.antibot;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.velocitypowered.proxy.config.VelocityConfiguration;
import com.velocitypowered.proxy.util.metrics.MetricsService;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AntiBotServiceTest {

  private VelocityConfiguration config;
  private VelocityConfiguration.AntiBot antiBotConfig;
  private MetricsService metricsService;
  private AntiBotService antiBotService;

  @BeforeEach
  void setUp() {
    config = mock(VelocityConfiguration.class);
    antiBotConfig = mock(VelocityConfiguration.AntiBot.class);
    metricsService = new MetricsService();
    
    when(config.getAntiBot()).thenReturn(antiBotConfig);
    // Defaults
    when(antiBotConfig.isEnabled()).thenReturn(true);
    when(antiBotConfig.getMaxConnectionsPerIp()).thenReturn(5);
    when(antiBotConfig.getWhitelist()).thenReturn(Collections.emptyList());
    when(antiBotConfig.getBlacklist()).thenReturn(Collections.emptyList());
  }

  @Test
  void testBlacklistRejection() throws UnknownHostException {
    when(antiBotConfig.getBlacklist()).thenReturn(List.of("1.2.3.4"));
    
    antiBotService = new AntiBotService(config, metricsService);
    
    InetAddress blocked = InetAddress.getByName("1.2.3.4");
    InetAddress allowed = InetAddress.getByName("5.6.7.8");

    assertFalse(antiBotService.allowConnection(blocked), "Blacklisted IP should be blocked");
    assertTrue(antiBotService.allowConnection(allowed), "Non-blacklisted IP should be allowed");
    
    // Verify metric increment
    assertTrue(metricsService.scrape().contains("antibot_blocked_connections 1"));
  }

  @Test
  void testChallengeMode() throws UnknownHostException, InterruptedException {
    when(antiBotConfig.isChallengeEnabled()).thenReturn(true);
    when(antiBotConfig.getChallengeCooldown()).thenReturn(100); // 100ms
    when(antiBotConfig.getChallengeRepeat()).thenReturn(1000);
    
    antiBotService = new AntiBotService(config, metricsService);
    InetAddress ip = InetAddress.getByName("10.0.0.1");

    // 1. First attempt: Should fail (Challenge Started)
    assertFalse(antiBotService.allowConnection(ip), "First connection should be rejected (Challenge)");

    // 2. Immediate retry: Should fail (Cooldown)
    assertFalse(antiBotService.allowConnection(ip), "Immediate retry should be rejected (Cooldown)");

    // 3. Wait for cooldown
    Thread.sleep(150);

    // 4. Retry after cooldown: Should pass
    assertTrue(antiBotService.allowConnection(ip), "Connection after cooldown should be allowed");

    // 5. Subsequent attempts: Should pass (Whitelisted/Cached)
    assertTrue(antiBotService.allowConnection(ip), "Subsequent connections should be allowed");
  }

  @Test
  void testConnectionLimit() throws UnknownHostException {
    when(antiBotConfig.getMaxConnectionsPerIp()).thenReturn(2);
    antiBotService = new AntiBotService(config, metricsService);
    InetAddress ip = InetAddress.getByName("192.168.1.50");

    assertTrue(antiBotService.allowConnection(ip));
    assertTrue(antiBotService.allowConnection(ip));
    // 3rd should fail
    assertFalse(antiBotService.allowConnection(ip), "Exceeding connection limit should block");
  }
}
