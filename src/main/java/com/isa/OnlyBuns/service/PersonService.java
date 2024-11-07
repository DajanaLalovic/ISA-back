package com.isa.OnlyBuns.service;


import com.isa.OnlyBuns.irepository.IPersonRepository;
import com.isa.OnlyBuns.model.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PersonService {
    @Autowired
    private IPersonRepository personRepository;

    public Person save(Person person) {return personRepository.save(person);}
}
