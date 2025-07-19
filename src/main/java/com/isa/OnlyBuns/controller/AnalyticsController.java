package com.isa.OnlyBuns.controller;

import com.isa.OnlyBuns.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping(value= "/api/analytics", produces = MediaType.APPLICATION_JSON_VALUE)
public class AnalyticsController {
    @Autowired
    private AnalyticsService analyticsService;

    @GetMapping(value="/all")
    @PreAuthorize("isAuthenticated()")
    public Map<String, Long> getAnalytics(Principal principal) {
        System.out.println("Trenutni korisnik: " + principal.getName());
        return analyticsService.getAnalytics();
    }

    @GetMapping(value="/statistics")
    @PreAuthorize("isAuthenticated()")
    public Map<String, Double> getStatistics(Principal principal) {
        return analyticsService.getUserActivityStatistics();
    }
}
