package com.example.rest_servlet_jdbc.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.example.rest_servlet_jdbc.dao.TeacherDao;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.rest_servlet_jdbc.dto.TeacherDto;
import com.example.rest_servlet_jdbc.mapper.TeacherMapper;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TeacherService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private TeacherDao teacherDao;
    private TeacherMapper teacherMapper;


    public TeacherService(TeacherDao teacherDao, TeacherMapper teacherMapper) {
            this.teacherDao = teacherDao;
            this.teacherMapper = teacherMapper;
    }


    public Optional<String> handleGetRequest(String requestPath) throws SQLException {
        if(requestPath == null) {
            List<TeacherDto> teachers = teacherDao.findAll().stream().map(teacher -> {
                try {
                    return teacherMapper.toTeacherDto(teacher);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return null;
            }).collect(Collectors.toList());
            try {
                return Optional.ofNullable(objectMapper.writeValueAsString(teachers));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        } else {
            String[] pathArray = requestPath.split("/");
            if(pathArray.length > 0) {
                long id = Long.parseLong(pathArray[1]);
                TeacherDto teacherDto = teacherMapper.toTeacherDto(teacherDao.findById(id).get());
                try {
                    return Optional.ofNullable(objectMapper.writeValueAsString(teacherDto));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            } else {
                List<TeacherDto> teachers = teacherDao.findAll().stream().map(teacher -> {
                    try {
                        return teacherMapper.toTeacherDto(teacher);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    return null;
                }).collect(Collectors.toList());
                try {
                    return Optional.ofNullable(objectMapper.writeValueAsString(teachers));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        }
            return Optional.empty();
    }

        public void handlePostRequest(String bodyParams) throws SQLException {
            try {
                TeacherDto teacherDto = objectMapper.readValue(bodyParams, TeacherDto.class);
                teacherDao.save(teacherMapper.toTeacher(teacherDto));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        public void handlePutRequest(String requestPath, String bodyParams) throws SQLException {
            String[] pathArray = requestPath.split("/");
            Long id = Long.parseLong(pathArray[1]);
            try {
                TeacherDto teacherDto = objectMapper.readValue(bodyParams, TeacherDto.class);
                teacherDto.setId(id);
                teacherDao.save(teacherMapper.toTeacher(teacherDto));
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

        public void handleDeleteRequest(String requestPath) throws SQLException {
            String[] pathArray = requestPath.split("/");
            Long id = Long.parseLong(pathArray[1]);
            teacherDao.deleteById(id);
        }

}
