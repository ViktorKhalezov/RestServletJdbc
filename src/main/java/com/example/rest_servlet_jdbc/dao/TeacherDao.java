package com.example.rest_servlet_jdbc.dao;


import com.example.rest_servlet_jdbc.entity.Teacher;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface TeacherDao {

    Teacher save(Teacher teacher) throws SQLException;

    Optional<Teacher> findById(Long id) throws SQLException;

    List<Teacher> findAll() throws SQLException;

    void deleteById(Long id) throws SQLException;

    Optional<Teacher> findByFullName(String firstname, String lastname) throws SQLException;

}
