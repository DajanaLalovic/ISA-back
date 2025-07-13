package com.isa.OnlyBuns.irepository;

import com.isa.OnlyBuns.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface IUserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    User findByActivationToken(String activationToken);
    List<User> findByLastLoginBefore(LocalDateTime date);
    User deleteUserByUsername(String username);
    List<User> findFollowingById(Long id);
    List<User> findByFollowers_IdAndLastLoginAfter(Long userId, LocalDateTime startTime);

}