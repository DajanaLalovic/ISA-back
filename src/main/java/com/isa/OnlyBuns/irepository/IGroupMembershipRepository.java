package com.isa.OnlyBuns.irepository;

import com.isa.OnlyBuns.model.ChatMessage;
import com.isa.OnlyBuns.model.GroupMembership;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IGroupMembershipRepository extends JpaRepository<GroupMembership, Integer> {
    Optional<GroupMembership> findByGroupIdAndUserId(Integer groupId, Long userId);
    List<GroupMembership> findAllByUserId(Long userId);
    List<GroupMembership> findAllByGroupId(Integer groupId);
    @Modifying
    @Transactional
    @Query("DELETE FROM GroupMembership gm WHERE gm.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);

}

