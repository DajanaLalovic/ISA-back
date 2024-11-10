package com.isa.OnlyBuns.dto;

import com.isa.OnlyBuns.model.User;

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

    public UserDTO() {}

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
    }

    public UserDTO(Long id, String username, Boolean active, String street, String number, String city, String postalCode, String country) {
        this.id = id;
        this.username = username;
        this.active = active;
        this.street = street;
        this.number = number;
        this.city = city;
        this.postalCode = postalCode;
        this.country = country;
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
}

