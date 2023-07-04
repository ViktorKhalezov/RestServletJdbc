package com.example.rest_servlet_jdbc.mapper;

import com.example.rest_servlet_jdbc.dao.CourseDao;
import com.example.rest_servlet_jdbc.dto.StudentDto;
import com.example.rest_servlet_jdbc.entity.Course;
import com.example.rest_servlet_jdbc.entity.Student;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class StudentMapper {

    private CourseDao courseDao;

    public StudentMapper(CourseDao courseDao) {
        this.courseDao = courseDao;
    }

    public StudentDto toStudentDto(Student student) throws SQLException {
        StudentDto studentDto = new StudentDto();
        studentDto.setId(student.getId());
        studentDto.setFirstname(student.getFirstname());
        studentDto.setLastname(student.getLastname());
        studentDto.setAge(student.getAge());
        Set<String> courses = new HashSet<>();
        Set<Course> coursesFromStudent = student.getCourses();

        if(coursesFromStudent != null && coursesFromStudent.size() > 0) {
            for (Course course : coursesFromStudent) {
                courses.add(course.getTitle());
            }
        }

        studentDto.setCourses(courses);
        return studentDto;
    }

    public Student toStudent(StudentDto studentDto) throws SQLException {
        Student student = new Student();
        if(studentDto.getId() != null) {
            student.setId(studentDto.getId());
        }
        student.setFirstname(studentDto.getFirstname());
        student.setLastname(studentDto.getLastname());
        student.setAge(studentDto.getAge());
        Set<Course> courses = new HashSet<>();
        Set<String> coursesFromDto = studentDto.getCourses();

        if(coursesFromDto != null && coursesFromDto.size() > 0) {
            for (String course : coursesFromDto) {
                courses.add(courseDao.findByTitle(course).get());
            }
        }

        student.setCourses(courses);
        return student;
    }

}



