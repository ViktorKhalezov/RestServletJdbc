package com.example.rest_servlet_jdbc.dto;


import java.util.Set;

public class StudentDto {

    private Long id;

    private String firstname;

    private String lastname;

    private Integer age;

    private Set<String> courses;

    public StudentDto(Long id, String firstname, String lastname, Integer age, Set<String> courses) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.age = age;
        this.courses = courses;
    }

    public StudentDto() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Set<String> getCourses() {
        return courses;
    }

    public void setCourses(Set<String> courses) {
        this.courses = courses;
    }
}
