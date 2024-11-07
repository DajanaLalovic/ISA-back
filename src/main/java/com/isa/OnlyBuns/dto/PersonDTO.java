package com.isa.OnlyBuns.dto;


import com.isa.OnlyBuns.model.Person;

public class PersonDTO {
    private Integer id;
    private String name;
    private String email;

    public PersonDTO() {}

    public PersonDTO(Person person) {
        this.id = person.getId();
        this.name = person.getName();
        this.email = person.getEmail();
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}
