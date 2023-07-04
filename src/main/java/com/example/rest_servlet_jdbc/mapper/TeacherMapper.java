package com.example.rest_servlet_jdbc.mapper;

import com.example.rest_servlet_jdbc.dao.CourseDao;
import com.example.rest_servlet_jdbc.dto.TeacherDto;
import com.example.rest_servlet_jdbc.entity.Course;
import com.example.rest_servlet_jdbc.entity.Teacher;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class TeacherMapper {

    private CourseDao courseDao;

    public TeacherMapper(CourseDao courseDao) {
        this.courseDao = courseDao;
    }


    public TeacherDto toTeacherDto(Teacher teacher) throws SQLException {
        TeacherDto teacherDto = new TeacherDto();
        teacherDto.setId(teacher.getId());
        teacherDto.setFirstname(teacher.getFirstname());
        teacherDto.setLastname(teacher.getLastname());
        teacherDto.setFaculty(teacher.getFaculty());
        Set<String> courses = new HashSet<>();
        Set<Course> coursesFromTeacher = teacher.getCourses();

        if(coursesFromTeacher != null && coursesFromTeacher.size() > 0) {
            for (Course course : coursesFromTeacher) {
                courses.add(course.getTitle());
            }
        }

        teacherDto.setCourses(courses);
        return teacherDto;
    }


    public Teacher toTeacher(TeacherDto teacherDto) throws SQLException {
        Teacher teacher = new Teacher();
        if(teacherDto.getId() != null) {
            teacher.setId(teacherDto.getId());
        }
        teacher.setFirstname(teacherDto.getFirstname());
        teacher.setLastname(teacherDto.getLastname());
        teacher.setFaculty(teacherDto.getFaculty());
        Set<Course> courses = new HashSet<>();
        Set<String> coursesFromDto = teacherDto.getCourses();

        if(coursesFromDto != null && coursesFromDto.size() > 0) {
            for (String course : coursesFromDto) {
                courses.add(courseDao.findByTitle(course).get());
            }
        }

        teacher.setCourses(courses);
        return teacher;
    }

}
