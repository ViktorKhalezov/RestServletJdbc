package com.example.rest_servlet_jdbc.mapper;

import com.example.rest_servlet_jdbc.dao.StudentDao;
import com.example.rest_servlet_jdbc.dao.TeacherDao;
import com.example.rest_servlet_jdbc.dto.CourseDto;
import com.example.rest_servlet_jdbc.entity.Course;
import com.example.rest_servlet_jdbc.entity.Student;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class CourseMapper {

    private TeacherDao teacherDao;
    private StudentDao studentDao;

    public CourseMapper(TeacherDao teacherDao, StudentDao studentDao) {
        this.teacherDao = teacherDao;
        this.studentDao = studentDao;
    }

    public CourseDto toCourseDto(Course course) throws SQLException {
        CourseDto courseDto = new CourseDto();
        courseDto.setId(course.getId());
        courseDto.setTitle(course.getTitle());
        courseDto.setTeacher(course.getTeacher().getFirstname() + " " + course.getTeacher().getLastname());
        Set<String> students = new HashSet<>();

        Set<Student> studentsFromCourse = course.getStudents();
        if(studentsFromCourse != null && studentsFromCourse.size() > 0) {
            for (Student student : studentsFromCourse) {
                students.add(student.getFirstname() + " " + student.getLastname());
            }
        }
        courseDto.setStudents(students);
        return courseDto;
    }

    public Course toCourse(CourseDto courseDto) throws SQLException {
        Course course = new Course();
        if(courseDto.getId() != null) {
            course.setId(courseDto.getId());
        }
        course.setTitle(courseDto.getTitle());
        String[] teacherFullName = courseDto.getTeacher().split(" ");
        course.setTeacher(teacherDao.findByFullName(teacherFullName[0], teacherFullName[1]).get());

        Set<Student> students = new HashSet<>();
        Set<String> studentsFromDto = courseDto.getStudents();

        if(studentsFromDto != null && studentsFromDto.size() > 0) {
            for (String student : studentsFromDto) {
                String[] studentFullName = student.split(" ");
                Student studentFromDB = studentDao.findByFullName(studentFullName[0], studentFullName[1]).get();
                students.add(studentFromDB);
            }
        }

        course.setStudents(students);
        return course;
    }

}
