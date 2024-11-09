package com.isa.OnlyBuns.irepository;

import java.util.List;
import com.isa.OnlyBuns.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IRoleRepository extends JpaRepository<Role, Long> {
    List<Role> findByName(String name);
}