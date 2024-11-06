package com.isa.OnlyBuns.model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Objects;



@Entity
@Table(name = "person")
public class Person implements Serializable {

   // private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name= "name", nullable = false)
    private String name;

   // @Column(nullable = false)
   // private String address;

    @Column(name= "email", nullable = false, unique = true)
    private String email;

    public Person() {}

    public Person(String name, String email) {
        this.name = name;
       // this.address = address;
        this.email = email;
    }

    // Getteri i setteri
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

   /* public String getAddress() {
        return address;
    }*/

   /* public void setAddress(String address) {
        this.address = address;
    }*/

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person)) return false;
        Person person = (Person) o;
        return Objects.equals(id, person.id) &&
                Objects.equals(email, person.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
               // ", address='" + address + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
