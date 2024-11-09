package com.isa.OnlyBuns.iservice;

import com.isa.OnlyBuns.model.Role;

import java.util.List;


public interface IRoleService {
    Role findById(Long id);
    List<Role> findByName(String name);
}
