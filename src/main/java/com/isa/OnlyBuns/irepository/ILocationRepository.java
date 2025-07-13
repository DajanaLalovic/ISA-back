package com.isa.OnlyBuns.irepository;

import com.isa.OnlyBuns.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ILocationRepository extends JpaRepository<Location, Long>
{
}
