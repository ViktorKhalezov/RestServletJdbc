package com.example.rest_servlet_jdbc.entity;

import java.util.Objects;
import java.util.Set;

public class Student extends Person {

    private Integer age;

    private Set<Course> courses;

    public Student(Long id, String firstname, String lastname, Integer age, Set<Course> courses) {
        super(id, firstname, lastname);
        this.age = age;
        this.courses = courses;
    }

    public Student() {

    }

    public Integer getAge() {
        return age;
    }

    public Set<Course> getCourses() {
        return courses;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public void setCourses(Set<Course> courses) {
        this.courses = courses;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Student student = (Student) o;
        return age.equals(student.age) && courses.equals(student.courses);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), age, courses);
    }

}
