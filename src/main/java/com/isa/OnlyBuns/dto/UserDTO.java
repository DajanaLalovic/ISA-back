package com.isa.OnlyBuns.dto;

import com.isa.OnlyBuns.model.User;

import java.time.LocalDateTime;
<<<<<<< HEAD
=======
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
>>>>>>> e4c44f4f93959ac83d7a55e0479a68d6f0615e35

public class UserDTO {
    private Long id;
    private String name;
    private String surname;
    private String username;
    private String password;
    private Boolean active;
    private String email;
    private String activationToken;
    private String street;
    private String number;
    private String city;
    private String postalCode;
    private String country;
    private Long postCount;
    private Long followingCount;
    private LocalDateTime lastLogin;
    private String role; // Dodajte polje za ulogu korisnika
    private LocalDateTime activationSentAt;
    private Set<Long> following = new HashSet<>();
    private Set<Long> followers = new HashSet<>();


    public UserDTO() {
        this.postCount = 0L;
        this.followingCount = 0L;
    }

    public UserDTO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.active = user.getIsActive();
        if (user.getAddress() != null) {
            this.street = user.getAddress().getStreet();
            this.number = user.getAddress().getNumber();
            this.city = user.getAddress().getCity();
            this.postalCode = user.getAddress().getPostalCode();
            this.country = user.getAddress().getCountry();
        }
        this.postCount = user.getPostCount();
        this.followingCount = user.getFollowingCount();
        this.role = user.getRole().toString(); // Pretvorite `UserRole` u string
        this.lastLogin = user.getLastLogin();
        this.activationSentAt=user.getActivationSentAt();
        this.following=user.getFollowing().stream().map(User::getId).collect(Collectors.toSet());
        this.followers=user.getFollowers().stream().map(User::getId).collect(Collectors.toSet());

    }

    public UserDTO(Long id, String username, Boolean active, String street, String number, String city, String postalCode, String country, Long postCount, Long followingCount,LocalDateTime activationSentAt,Set<Long> following,Set<Long> followers) {
        this.id = id;
        this.username = username;
        this.active = active;
        this.street = street;
        this.number = number;
        this.city = city;
        this.postalCode = postalCode;
        this.country = country;
        this.postCount = postCount;
        this.followingCount = followingCount;
        this.activationSentAt=activationSentAt;
        this.following=following;
        this.followers=followers;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this. surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getIsActive() {
        return active;
    }
    public void setIsActive(Boolean active) {
        this.active = active;
    }

    public String getActivationToken() {
        return activationToken;
    }

    public void setActivationToken(String activationToken) {
        this.activationToken = activationToken;
    }
    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
    public Long getPostCount() {return postCount;}
    public void setPostCount(Long postCount) {this.postCount = postCount;}

    public Long getFollowingCount() {return followingCount;}
    public void setFollowingCount(Long followingCount) {this.followingCount = followingCount;}

    public LocalDateTime getActivationSentAt() {return activationSentAt;}
    public void setActivationSentAt(LocalDateTime activationSentAt) {this.activationSentAt=activationSentAt;}

    public Set<Long> getFollowing(){return following;}
    public void setFollowing(Set<Long> following){this.following=following;}

    public Set<Long> getFollowers(){return followers;}
    public void setFollowers(Set<Long> followers){this.followers=followers;}
}

