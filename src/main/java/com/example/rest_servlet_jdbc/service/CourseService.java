package com.example.rest_servlet_jdbc.service;

import com.example.rest_servlet_jdbc.dto.CourseDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.rest_servlet_jdbc.dao.CourseDao;
import com.example.rest_servlet_jdbc.mapper.CourseMapper;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class CourseService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private CourseDao courseDao;
    private CourseMapper courseMapper;

    public CourseService(CourseDao courseDao, CourseMapper courseMapper) {
        this.courseDao = courseDao;
        this.courseMapper = courseMapper;
    }

    public Optional<String> handleGetRequest(String requestPath) throws SQLException {
        if(requestPath == null) {
            List<CourseDto> courses = courseDao.findAll().stream().map(course -> {
                try {
                    return courseMapper.toCourseDto(course);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return null;
            }).collect(Collectors.toList());
            try {
                return Optional.ofNullable(objectMapper.writeValueAsString(courses));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        } else {
            String[] pathArray = requestPath.split("/");
            if(pathArray.length > 0) {
                long id = Long.parseLong(pathArray[1]);
                CourseDto courseDto = courseMapper.toCourseDto(courseDao.findById(id).get());
                try {
                    return Optional.ofNullable(objectMapper.writeValueAsString(courseDto));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            } else {
                List<CourseDto> courses = courseDao.findAll().stream().map(course -> {
                    try {
                        return courseMapper.toCourseDto(course);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    return null;
                }).collect(Collectors.toList());
                try {
                    return Optional.ofNullable(objectMapper.writeValueAsString(courses));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        }
        return Optional.empty();
    }

    public void handlePostRequest(String bodyParams) throws SQLException {
        try {
            CourseDto courseDto = objectMapper.readValue(bodyParams, CourseDto.class);
            courseDao.save(courseMapper.toCourse(courseDto));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public void handlePutRequest(String requestPath, String bodyParams) throws SQLException {
        String[] pathArray = requestPath.split("/");
        Long id = Long.parseLong(pathArray[1]);
        try {
            CourseDto courseDto = objectMapper.readValue(bodyParams, CourseDto.class);
            courseDto.setId(id);
            courseDao.save(courseMapper.toCourse(courseDto));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public void handleDeleteRequest(String requestPath) throws SQLException {
        String[] pathArray = requestPath.split("/");
        Long id = Long.parseLong(pathArray[1]);
        courseDao.deleteById(id);
    }

}
