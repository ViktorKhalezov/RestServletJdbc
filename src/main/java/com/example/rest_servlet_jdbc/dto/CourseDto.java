package com.example.rest_servlet_jdbc.dto;

import java.util.Set;

public class CourseDto {

    private Long id;

    private String title;

    private Set<String> students;

    private String teacher;

    public CourseDto(Long id, String title, Set<String> students, String teacher) {
        this.id = id;
        this.title = title;
        this.students = students;
        this.teacher = teacher;
    }

    public CourseDto() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Set<String> getStudents() {
        return students;
    }

    public void setStudents(Set<String> students) {
        this.students = students;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }
}
