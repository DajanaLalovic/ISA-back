package com.isa.OnlyBuns.irepository;

import com.isa.OnlyBuns.model.User;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Lock;

import java.time.LocalDateTime;
import java.util.List;

public interface IUserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    User findByActivationToken(String activationToken);
    void deleteById(Long id);
    List<User> findByLastLoginBefore(LocalDateTime date);
    User deleteUserByUsername(String username);
    List<User> findFollowingById(Long id);
    List<User> findByFollowers_IdAndLastLoginAfter(Long userId, LocalDateTime startTime);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT u FROM User u WHERE u.id = :id")
    User findByIdWithLock(@Param("id") Long id);
   // User findById(int id);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM user_following WHERE user_id = :userId OR following_id = :userId", nativeQuery = true)
    void deleteFollowRelationsForUser(@Param("userId") Long userId);

}