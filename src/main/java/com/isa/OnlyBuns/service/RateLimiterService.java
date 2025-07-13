package com.isa.OnlyBuns.service;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;


@Service
public class RateLimiterService {

    private static final int MAX_REQUESTS = 5;  // Maksimalno 5 komentara po korisniku u 60 sekundi
    private static final long TIME_WINDOW_SECONDS = 60;

    private final Map<Long, List<Instant>> userRequests = new ConcurrentHashMap<>();

    public synchronized boolean allowRequest(Long userId) {
        Instant now = Instant.now();

        // Ukloni sve stare zahteve koji su van vremenskog prozora
        userRequests.put(userId, userRequests.getOrDefault(userId, List.of())
                .stream()
                .filter(time -> time.plusSeconds(TIME_WINDOW_SECONDS).isAfter(now))
                .collect(Collectors.toList()));

        // Ako korisnik već ima 5 zahteva u poslednjem minutu, odbijamo novi zahtev
        if (userRequests.get(userId).size() >= MAX_REQUESTS) {
            return false;
        }

        // Dodaj novi zahtev korisnika
        userRequests.computeIfAbsent(userId, k -> new java.util.ArrayList<>()).add(now);
        return true;
    }

}
