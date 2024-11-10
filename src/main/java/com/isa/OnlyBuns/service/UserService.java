package com.isa.OnlyBuns.service;


import com.isa.OnlyBuns.dto.UserDTO;
import com.isa.OnlyBuns.enums.UserRole;
import com.isa.OnlyBuns.irepository.IUserRepository;
import com.isa.OnlyBuns.iservice.IRoleService;
import com.isa.OnlyBuns.iservice.IUserService;
import com.isa.OnlyBuns.model.Role;
import com.isa.OnlyBuns.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService implements IUserService {
    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private IRoleService roleService;

    public String generateActivationToken() {
        return UUID.randomUUID().toString();
    }


    @Override
    public User findByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username);
    }

    public User findById(Long id) throws AccessDeniedException {
        return userRepository.findById(id).orElseGet(null);
    }

    public List<User> findAll() throws AccessDeniedException {
        return userRepository.findAll();
    }

    @Override
    public User save(UserDTO userRequest) {
        User u = new User();
        u.setUsername(userRequest.getUsername());

        // pre nego sto postavimo lozinku u atribut hesiramo je kako bi se u bazi nalazila hesirana lozinka
        // treba voditi racuna da se koristi isi password encoder bean koji je postavljen u AUthenticationManager-u kako bi koristili isti algoritam
        u.setPassword(passwordEncoder.encode(userRequest.getPassword()));

        u.setName(userRequest.getName());
        u.setSurname(userRequest.getSurname());
        u.setIsActive(userRequest.getIsActive());
        if (u.getIsActive() == null) {
            u.setIsActive(false);  // Ili true, zavisno od tvoje logike
        }

        u.setEmail(userRequest.getEmail());
        u.setActivationToken(userRequest.getActivationToken());
        u.setRole(UserRole.USER);
        // u primeru se registruju samo obicni korisnici i u skladu sa tim im se i dodeljuje samo rola USER
     /*   u.setRoles(Collections.singleton(UserRole.USER));
        if (u.getRoles() == null || u.getRoles().isEmpty()) {
            u.setRoles(Set.of(UserRole.USER)); // Primer za dodeljivanje jedne podrazumevane uloge

        u.setRoles(new HashSet<>(Arrays.asList(UserRole.USER)));

        }*/

        return this.userRepository.save(u);
    }


    public User save(User user) {return userRepository.save(user);}


    public User convertToUser(UserDTO userDTO) {
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setName(userDTO.getName());
        user.setSurname(userDTO.getSurname());
        user.setPassword(userDTO.getPassword()); // Razmislite o enkripciji lozinke pre nego što je postavite
        user.setIsActive(userDTO.getIsActive());  // Ako želite da korisnik bude inaktiviran pri registraciji
       user.setActivationToken(userDTO.getActivationToken());
        return user;
    }
    public UserDTO convertToDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        userDTO.setName(user.getName());
        userDTO.setSurname(user.getSurname());
        // Ako želite, možete vratiti lozinku ili je sakriti
        userDTO.setPassword(user.getPassword());  // Ipak, preporučuje se da lozinku ne šaljete u DTO
        userDTO.setIsActive(user.getIsActive());  // Ako je ovo potrebno u DTO
        userDTO.setActivationToken(user.getActivationToken());
        return userDTO;
    }
    public User findByActivationToken(String activationToken) {
        return userRepository.findByActivationToken(activationToken);
    }
    public void updateUser(User user) {
        userRepository.save(user);
    }

}


