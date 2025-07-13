package com.isa.OnlyBuns.iservice;

import com.isa.OnlyBuns.model.Location;

import java.util.List;

public interface ILocationService {

    Location findOne(Long id);
    List<Location> findAll();
    Location save(Location location);
    void delete(Long id);
    void removeCache();
}
