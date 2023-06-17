package entity;

import entity.Entity;

import java.util.Objects;

public class Person extends Entity {

    private String firstname;
    private String lastname;


    public Person(Long id, String firstname, String lastname) {
        super(id);
        this.firstname = firstname;
        this.lastname = lastname;
    }

    public Person() {

    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Person person = (Person) o;
        return firstname.equals(person.firstname) && lastname.equals(person.lastname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), firstname, lastname);
    }
}
