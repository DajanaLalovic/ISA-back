package com.isa.OnlyBuns.iservice;

import com.isa.OnlyBuns.dto.UserDTO;
import com.isa.OnlyBuns.model.User;

import java.time.LocalDateTime;
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
    List<User> searchUsers(String name, String surname, String email, Long minPostCount, Long maxPostCount, String sortBy, String sortOrder) ;
    List<User> findUsersByLastLoginBefore(LocalDateTime date);
}
