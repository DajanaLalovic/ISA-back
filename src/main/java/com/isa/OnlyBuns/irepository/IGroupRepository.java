package com.isa.OnlyBuns.irepository;

import com.isa.OnlyBuns.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IGroupRepository extends JpaRepository<Group, Integer> {
    List<Group> findByMembersId(Long userId); //za pretragu
}
