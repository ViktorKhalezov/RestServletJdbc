package com.example.rest_servlet_jdbc.entity;

import java.util.Objects;
import java.util.Set;

public class Course extends Entity {

    private String title;

    private Set<Student> students;

    private Teacher teacher;


    public Course(Long id, String title, Set<Student> students, Teacher teacher) {
        super(id);
        this.title = title;
        this.students = students;
        this.teacher = teacher;
    }

    public Course() {

    }

    public String getTitle() {
        return title;
    }

    public Set<Student> getStudents() {
        return students;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setStudents(Set<Student> students) {
        this.students = students;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Course course = (Course) o;
        return title.equals(course.title) && students.equals(course.students) && teacher.equals(course.teacher);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), title, students, teacher);
    }
}
