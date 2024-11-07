package com.isa.OnlyBuns.irepository;

import com.isa.OnlyBuns.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IPersonRepository extends JpaRepository<Person, Integer> {


}
