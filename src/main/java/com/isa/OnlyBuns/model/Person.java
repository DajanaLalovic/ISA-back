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
    private Integer id;

    @Column(name= "name", nullable = false)
    private String name;

   // @Column(nullable = false)
   // private String address;

    @Column(name= "email", nullable = false, unique = true)
    private String email;

    public Person() { super();}

    public Person(String name, String email) {
        super();
        this.name = name;
       // this.address = address;
        this.email = email;
    }

    // Getteri i setteri
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Person p = (Person) o;
        if (p.email == null || email == null) {
            return false;
        }
        return Objects.equals(email, p.email);
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
