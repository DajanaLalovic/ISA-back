package com.isa.OnlyBuns.dto;

import com.isa.OnlyBuns.model.User;

public class UserDTO {
    private Integer id;
    private String username;
    private Boolean active;

    public UserDTO() {}

    public UserDTO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.active = user.getIsActive();
    }

    public UserDTO(Integer id, String username, Boolean active) {
        this.id = id;
        this.username = username;
        this.active = active;
    }

    public Integer getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public Boolean getIsActive() {
        return active;
    }


}

