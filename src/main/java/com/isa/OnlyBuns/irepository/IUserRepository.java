package com.isa.OnlyBuns.irepository;

import com.isa.OnlyBuns.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IUserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
