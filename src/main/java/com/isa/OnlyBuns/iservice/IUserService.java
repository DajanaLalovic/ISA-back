package com.isa.OnlyBuns.iservice;

import com.isa.OnlyBuns.dto.UserDTO;
import com.isa.OnlyBuns.model.User;

import java.util.List;

public interface IUserService {
    User findById(Long id);
    User findById1(Long id);
    User findByUsername(String username);
    List<User> findAll ();
    User save(UserDTO userDTO);
    User convertToUser(UserDTO userDTO);
    UserDTO convertToDTO(User user);
    User findByActivationToken(String activationToken);
    void updateUser(User user);
}
