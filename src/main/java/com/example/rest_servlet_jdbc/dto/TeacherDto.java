package com.example.rest_servlet_jdbc.dto;


import java.util.Set;

public class TeacherDto {

    private Long id;

    private String firstname;

    private String lastname;

    private String faculty;

    private Set<String> courses;

    public TeacherDto(Long id, String firstname, String lastname, String faculty, Set<String> courses) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.faculty = faculty;
        this.courses = courses;
    }

    public TeacherDto() {

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

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public Set<String> getCourses() {
        return courses;
    }

    public void setCourses(Set<String> courses) {
        this.courses = courses;
    }
}
