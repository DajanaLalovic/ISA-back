package com.isa.OnlyBuns.service;


import com.isa.OnlyBuns.irepository.IUserRepository;
import com.isa.OnlyBuns.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private IUserRepository userRepository;

    public User save(User user) {return userRepository.save(user);}
}
