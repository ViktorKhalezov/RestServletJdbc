package com.example.rest_servlet_jdbc.service_tests;

import com.example.rest_servlet_jdbc.dao.*;
import com.example.rest_servlet_jdbc.dto.TeacherDto;
import com.example.rest_servlet_jdbc.mapper.TeacherMapper;
import com.example.rest_servlet_jdbc.service.TeacherService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TeacherServiceTest {

    private final DataSource dataSource = DBCPDataSource.getDataSource();

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final TeacherDao teacherDao;

    private final CourseDao courseDao;

    private final TeacherMapper teacherMapper;

    private final TeacherService teacherService;


    public TeacherServiceTest() {
        teacherDao = new TeacherDaoJDBC(dataSource);
        courseDao = new CourseDaoJDBC(dataSource);
        teacherMapper = new TeacherMapper(courseDao);
        teacherService = new TeacherService(teacherDao, teacherMapper);
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
        List<TeacherDto> teachers = objectMapper.readValue(teacherService.handleGetRequest("/").get(), List.class);

        TeacherDto teacherDto = objectMapper.readValue(teacherService.handleGetRequest("/1").get(), TeacherDto.class);

        Assertions.assertEquals(2, teachers.size());
        Assertions.assertEquals(1L, teacherDto.getId());
    }

    @Test
    void handlePostRequest() throws SQLException, JsonProcessingException {
        String newTeacher = "{    \"firstname\": \"Artem\",    \"lastname\": \"Nikitin\",    \"faculty\": \"Technical\",  " +
                "  \"courses\": [        \"Programming\",	\"Mathmatics\",	\"Marketing\"    ]}";

        teacherService.handlePostRequest(newTeacher);

        List<TeacherDto> teachers = objectMapper.readValue(teacherService.handleGetRequest("/").get(), List.class);
        TeacherDto teacherDto = objectMapper.readValue(teacherService.handleGetRequest("/3").get(), TeacherDto.class);

        Assertions.assertEquals(3, teachers.size());
        Assertions.assertEquals("Artem", teacherDto.getFirstname());
        Assertions.assertEquals("Nikitin", teacherDto.getLastname());
    }

    @Test
    void handlePutRequest() throws SQLException, JsonProcessingException {
        String updatedTeacher = "{    \"id\": 2,    \"firstname\": \"Karl\",    \"lastname\": \"Brown\",    " +
                "\"faculty\": \"Technical\",    \"courses\": [        \"Programming\",	\"Mathmatics\"    ]}";

        teacherService.handlePutRequest("/2", updatedTeacher);

        TeacherDto teacherDto = objectMapper.readValue(teacherService.handleGetRequest("/2").get(), TeacherDto.class);

        Assertions.assertEquals("Karl", teacherDto.getFirstname());
        Assertions.assertEquals("Brown", teacherDto.getLastname());
    }

    @Test
    void handleDeleteRequest() throws SQLException, JsonProcessingException {
        teacherService.handleDeleteRequest("/1");

        List<TeacherDto> teachers = objectMapper.readValue(teacherService.handleGetRequest("/").get(), List.class);

        Assertions.assertEquals(1, teachers.size());
    }

}
