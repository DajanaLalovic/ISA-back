package com.isa.OnlyBuns.controller;

import com.isa.OnlyBuns.dto.PersonDTO;
import com.isa.OnlyBuns.model.Person;
import com.isa.OnlyBuns.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "api/person")
public class PersonController {
    @Autowired
    private PersonService personService;

    @PostMapping(consumes = "application/json")
    public ResponseEntity<PersonDTO> savePerson(@RequestBody PersonDTO personDTO) {

        Person person = new Person();
        person.setName(personDTO.getName());
        person.setEmail(personDTO.getEmail());

        person = personService.save(person);
        return new ResponseEntity<>(new PersonDTO(person), HttpStatus.CREATED);
    }
}
