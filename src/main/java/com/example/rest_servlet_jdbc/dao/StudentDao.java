package com.example.rest_servlet_jdbc.dao;

import com.example.rest_servlet_jdbc.entity.Student;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface StudentDao {

    Student save(Student student) throws SQLException;

    Optional<Student> findById(Long id) throws SQLException;

    List<Student> findAll() throws SQLException;

    void deleteById(Long id) throws SQLException;

    Optional<Student> findByFullName(String firstname, String lastname) throws SQLException;

}
