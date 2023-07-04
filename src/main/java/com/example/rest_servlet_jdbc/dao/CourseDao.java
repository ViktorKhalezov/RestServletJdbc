package com.example.rest_servlet_jdbc.dao;

import com.example.rest_servlet_jdbc.entity.Course;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface CourseDao {

    Course save(Course course) throws SQLException;

    Optional<Course> findById(Long id) throws SQLException;

    List<Course> findAll() throws SQLException;

    void deleteById(Long id) throws SQLException;

    Optional<Course> findByTitle(String title) throws SQLException;

}
