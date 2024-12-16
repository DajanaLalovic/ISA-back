package com.isa.OnlyBuns.irepository;

import com.isa.OnlyBuns.model.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface IUserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    User findByActivationToken(String activationToken);
    void deleteById(Long id);
}
