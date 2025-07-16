package com.isa.OnlyBuns.irepository;

import com.isa.OnlyBuns.model.ChatMessage;
import com.isa.OnlyBuns.model.GroupMembership;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IGroupMembershipRepository extends JpaRepository<GroupMembership, Integer> {
    Optional<GroupMembership> findByGroupIdAndUserId(Integer groupId, Long userId);
    List<GroupMembership> findAllByUserId(Long userId);
    List<GroupMembership> findAllByGroupId(Integer groupId);
}

