package com.isa.OnlyBuns.controller;


import com.isa.OnlyBuns.iservice.ILocationService;
import com.isa.OnlyBuns.model.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/locations")

public class LocationController {

    @Autowired
    private ILocationService locationService;

    // Endpoint za dohvat jedne lokacije (koristi keš)
    @GetMapping("/{id}")
    public Location getLocation(@PathVariable Long id) {
        return locationService.findOne(id);
    }

    // Endpoint za brisanje keša
    @DeleteMapping("/cache")
    public void clearCache() {
        locationService.removeCache();
    }
}
