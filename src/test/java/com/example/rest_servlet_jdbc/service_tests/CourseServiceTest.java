package com.example.rest_servlet_jdbc.service_tests;

import com.example.rest_servlet_jdbc.dao.*;
import com.example.rest_servlet_jdbc.dto.CourseDto;
import com.example.rest_servlet_jdbc.mapper.CourseMapper;
import com.example.rest_servlet_jdbc.service.CourseService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CourseServiceTest {

    private final DataSource dataSource = DBCPDataSource.getDataSource();

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final TeacherDao teacherDao;

    private final StudentDao studentDao;

    private final CourseDao courseDao;

    private final CourseMapper courseMapper;

    private final CourseService courseService;


    public CourseServiceTest() {
        teacherDao = new TeacherDaoJDBC(dataSource);
        studentDao = new StudentDaoJDBC(dataSource);
        courseDao = new CourseDaoJDBC(dataSource);
        courseMapper = new CourseMapper(teacherDao, studentDao);
        courseService = new CourseService(courseDao, courseMapper);
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
        List<CourseDto> courses = objectMapper.readValue(courseService.handleGetRequest("/").get(), List.class);

        CourseDto courseDto = objectMapper.readValue(courseService.handleGetRequest("/2").get(), CourseDto.class);

        Assertions.assertEquals(3, courses.size());
        Assertions.assertEquals(2L, courseDto.getId());
    }

    @Test
    void handleDeleteRequest() throws SQLException, JsonProcessingException {
        courseService.handleDeleteRequest("/1");

        List<CourseDto> courses = objectMapper.readValue(courseService.handleGetRequest("/").get(), List.class);

        Assertions.assertEquals(2, courses.size());
    }

}
