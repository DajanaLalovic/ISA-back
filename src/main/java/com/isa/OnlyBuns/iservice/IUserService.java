package com.isa.OnlyBuns.iservice;

import com.isa.OnlyBuns.dto.UserDTO;
import com.isa.OnlyBuns.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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
    List<User> findUsersByLastLoginBefore(LocalDateTime date);
    void updatePassword(Long userId, String newPassword);
    User registerUser(User user);
    public void deleteUserById(Long id);
    List<User> searchUsers(String name, String surname, String email, Long minPostCount, Long maxPostCount, String sortBy, String sortOrder,int page,int size) ;
    void scheduledCleanUp();
    void deleteInactiveAccounts();
    List<User> findInactiveAccountsOlderThan();
    void unfollowUser(Long userId, String currentUsername);
    void followUser(Long userId,String currentUsername);
    boolean isFollowing(Long targetUserId, String username);
    List<User> getFollowers(Long userId);
    List<User> getFollowing(Long userId) ;

    List<Map<String, Object>> getAllBasicUserInfo();
    void updateProfile(Long userId, Map<String, Object> updateData);
    }
