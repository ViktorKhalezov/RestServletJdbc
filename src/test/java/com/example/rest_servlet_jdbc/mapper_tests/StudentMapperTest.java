package com.example.rest_servlet_jdbc.mapper_tests;

import com.example.rest_servlet_jdbc.dao.CourseDao;
import com.example.rest_servlet_jdbc.dao.CourseDaoJDBC;
import com.example.rest_servlet_jdbc.dao.DBCPDataSource;
import com.example.rest_servlet_jdbc.dto.StudentDto;
import com.example.rest_servlet_jdbc.entity.Course;
import com.example.rest_servlet_jdbc.entity.Student;
import com.example.rest_servlet_jdbc.mapper.StudentMapper;
import org.junit.jupiter.api.*;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class StudentMapperTest {

    private final static DataSource dataSource = DBCPDataSource.getDataSource();

    private final CourseDao courseDao;

    private final StudentMapper studentMapper;

    public StudentMapperTest() {
        courseDao = new CourseDaoJDBC(dataSource);
        studentMapper = new StudentMapper(courseDao);
    }

    @BeforeAll
    static void initDb () {
        DBCPDataSource.createTestTables(dataSource);
    }

    @AfterAll
    static void destroyDb() {
        DBCPDataSource.deleteTestTables(dataSource);
    }

    @Test
    void toStudentDtoTest() throws SQLException {
        Student student = new Student();
        student.setId(1L);
        student.setFirstname("Ivan");
        student.setLastname("Ivanov");
        student.setAge(19);

        Set<Course> courses = new HashSet<>();
        Course management = new Course();
        management.setId(1L);
        management.setTitle("Management");
        courses.add(management);
        Course marketing = new Course();
        marketing.setId(2L);
        marketing.setTitle("Marketing");
        courses.add(marketing);
        student.setCourses(courses);

        StudentDto studentDto = studentMapper.toStudentDto(student);

        Assertions.assertEquals(student.getId(), studentDto.getId());
        Assertions.assertEquals(student.getFirstname(), studentDto.getFirstname());
        Assertions.assertEquals(student.getLastname(), studentDto.getLastname());

        Assertions.assertTrue(studentDto.getCourses().contains("Management"));
        Assertions.assertTrue(studentDto.getCourses().contains("Marketing"));
    }

    @Test
    void toStudentTest() throws SQLException {
        StudentDto studentDto = new StudentDto();
        studentDto.setId(1L);
        studentDto.setFirstname("Viktor");
        studentDto.setLastname("Vinogradov");
        studentDto.setAge(36);
        Set<String> courses = new HashSet<>();
        courses.add("Programming");
        courses.add("Mathematics");
        studentDto.setCourses(courses);

        Student student = studentMapper.toStudent(studentDto);

        Assertions.assertEquals(studentDto.getId(), student.getId());
        Assertions.assertEquals(studentDto.getFirstname(), student.getFirstname());
        Assertions.assertEquals(studentDto.getLastname(), student.getLastname());

        Assertions.assertEquals(studentDto.getCourses().size(), student.getCourses().size());
    }

}