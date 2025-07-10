package com.isa.OnlyBuns.irepository;

import com.isa.OnlyBuns.model.LocationMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ILocationMessageRepository extends JpaRepository<LocationMessage, Long> {
    LocationMessage findLocationMessageById(Long id);
}
