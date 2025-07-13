package com.isa.OnlyBuns.config;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Component
@RequiredArgsConstructor
public class ActiveUserMetricsConfig {

    private final MeterRegistry meterRegistry;
    private final Map<Integer, Instant> activeUsers = new ConcurrentHashMap<>();

    // Beleži kada je korisnik poslednji put bio aktivan
    public void userActivity(Integer userId) {
        activeUsers.put(userId, Instant.now());
    }

    // Briše korisnika iz aktivnih ako se izlogovao
    public void removeUser(Integer userId) {
        activeUsers.remove(userId);
    }

    // Broji aktivne korisnike u poslednjih 5 minuta
    public int getActiveUserCount() {
        Instant now = Instant.now();
        return (int) activeUsers.values().stream()
                .filter(lastActive -> lastActive.plusSeconds(300).isAfter(now))
                .count();
    }

    @PostConstruct
    public void init() {
        Gauge.builder("app_active_users", this, ActiveUserMetricsConfig::getActiveUserCount)
                .description("Current active users")
                .register(meterRegistry);
    }
}
