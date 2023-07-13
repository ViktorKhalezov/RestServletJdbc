package com.example.rest_servlet_jdbc.service_tests;

import com.example.rest_servlet_jdbc.dao.*;
import com.example.rest_servlet_jdbc.dto.CourseDto;
import com.example.rest_servlet_jdbc.dto.StudentDto;
import com.example.rest_servlet_jdbc.mapper.StudentMapper;
import com.example.rest_servlet_jdbc.service.StudentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class StudentServiceTest {

    private final DataSource dataSource = DBCPDataSource.getDataSource();

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final CourseDao courseDao;

    private final StudentDao studentDao;

    private final StudentMapper studentMapper;

    private final StudentService studentService;


    public StudentServiceTest() {
        courseDao = new CourseDaoJDBC(dataSource);
        studentDao = new StudentDaoJDBC(dataSource);
        studentMapper = new StudentMapper(courseDao);
        studentService = new StudentService(studentDao, studentMapper);
    }

    @BeforeEach
    void createTestTables() {
        DBCPDataSource.createTestTables(dataSource);
    }

    @AfterEach
    void deleteTestTables() {
        DBCPDataSource.deleteTestTables(dataSource);
    }

    @Test
    void handleGetRequest() throws SQLException, JsonProcessingException {
        List<StudentDto> students = objectMapper.readValue(studentService.handleGetRequest("/").get(), List.class);

        StudentDto studentDto = objectMapper.readValue(studentService.handleGetRequest("/3").get(), StudentDto.class);

        Assertions.assertEquals(3, students.size());
        Assertions.assertEquals(3L, studentDto.getId());
    }

    @Test
    void handlePostRequest() throws SQLException, JsonProcessingException {
        String newStudent = "{        \"firstname\": \"Sergey\",        \"lastname\": \"Rollback\",        " +
                "\"age\": 75,        \"courses\": [            \"Programming\"        ]    }";

        studentService.handlePostRequest(newStudent);

        List<StudentDto> students = objectMapper.readValue(studentService.handleGetRequest("/").get(), List.class);
        StudentDto studentDto = objectMapper.readValue(studentService.handleGetRequest("/4").get(), StudentDto.class);

        Assertions.assertEquals(4, students.size());
        Assertions.assertEquals("Sergey", studentDto.getFirstname());
        Assertions.assertEquals("Rollback", studentDto.getLastname());

    }

    @Test
    void handlePutRequest() throws SQLException, JsonProcessingException {
        String updatedStudent = "{        \"id\": 2,        \"firstname\": \"Alesha\",        \"lastname\": \"Popovich\",        " +
                "\"age\": 25,        \"courses\": [            \"Mathematics\"        ]    }";

        studentService.handlePutRequest("/2", updatedStudent);

        StudentDto studentDto = objectMapper.readValue(studentService.handleGetRequest("/2").get(), StudentDto.class);

        Assertions.assertEquals("Alesha", studentDto.getFirstname());
        Assertions.assertEquals("Popovich", studentDto.getLastname());


    }

    @Test
    void handleDeleteRequest() throws SQLException, JsonProcessingException {
        studentService.handleDeleteRequest("/2");

        List<StudentDto> students = objectMapper.readValue(studentService.handleGetRequest("/").get(), List.class);

        Assertions.assertEquals(2, students.size());
    }

}
