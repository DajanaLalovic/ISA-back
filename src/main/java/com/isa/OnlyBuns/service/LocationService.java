package com.isa.OnlyBuns.service;

import com.isa.OnlyBuns.irepository.ILocationRepository;
import com.isa.OnlyBuns.iservice.ILocationService;
import com.isa.OnlyBuns.model.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationService implements ILocationService {

    @Autowired
    private ILocationRepository locationRepository;

    @Override
    @Cacheable("location")
    public Location findOne(Long id) {
        System.out.println("Fetching location from DB for id: " + id);
        return locationRepository.findById(id).orElse(null);
    }

    @Override
    public List<Location> findAll() {
        return locationRepository.findAll();
    }

    @Override
    public Location save(Location location) {
        return locationRepository.save(location);
    }

    @Override
    public void delete(Long id) {
        locationRepository.deleteById(id);
    }

    @Override
    @CacheEvict(cacheNames = {"location"}, allEntries = true)
    public void removeCache() {
        System.out.println("Location cache cleared!");
    }

}
