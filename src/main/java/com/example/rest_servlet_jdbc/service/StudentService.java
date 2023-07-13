package com.example.rest_servlet_jdbc.service;


import com.example.rest_servlet_jdbc.dto.StudentDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.rest_servlet_jdbc.dao.StudentDao;
import com.example.rest_servlet_jdbc.mapper.StudentMapper;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class StudentService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private StudentDao studentDao;
    private StudentMapper studentMapper;

    public StudentService(StudentDao studentDao, StudentMapper studentMapper) {
       this.studentDao = studentDao;
       this.studentMapper = studentMapper;
    }

    public Optional<String> handleGetRequest(String requestPath) throws SQLException {
        if(requestPath == null) {
            List<StudentDto> students = studentDao.findAll().stream().map(student -> {
                try {
                    return studentMapper.toStudentDto(student);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return null;
            }).collect(Collectors.toList());
            try {
                return Optional.ofNullable(objectMapper.writeValueAsString(students));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        } else {
            String[] pathArray = requestPath.split("/");
            if(pathArray.length > 0) {
                long id = Long.parseLong(pathArray[1]);
                StudentDto studentDto = studentMapper.toStudentDto(studentDao.findById(id).get());
                try {
                    return Optional.ofNullable(objectMapper.writeValueAsString(studentDto));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            } else {
                List<StudentDto> students = studentDao.findAll().stream().map(student -> {
                    try {
                        return studentMapper.toStudentDto(student);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    return null;
                }).collect(Collectors.toList());
                try {
                    return Optional.ofNullable(objectMapper.writeValueAsString(students));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        }
        return Optional.empty();
    }

    public void handlePostRequest(String bodyParams) throws SQLException {
        try {
            StudentDto studentDto = objectMapper.readValue(bodyParams, StudentDto.class);
            studentDao.save(studentMapper.toStudent(studentDto));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public void handlePutRequest(String requestPath, String bodyParams) throws SQLException {
        String[] pathArray = requestPath.split("/");
        Long id = Long.parseLong(pathArray[1]);
        try {
            StudentDto studentDto = objectMapper.readValue(bodyParams, StudentDto.class);
            studentDto.setId(id);
            studentDao.save(studentMapper.toStudent(studentDto));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public void handleDeleteRequest(String requestPath) throws SQLException {
        String[] pathArray = requestPath.split("/");
        Long id = Long.parseLong(pathArray[1]);
        studentDao.deleteById(id);
    }

}
