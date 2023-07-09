package com.example.rest_servlet_jdbc.mapper_tests;

import com.example.rest_servlet_jdbc.dao.CourseDao;
import com.example.rest_servlet_jdbc.dao.CourseDaoJDBC;
import com.example.rest_servlet_jdbc.dao.DBCPDataSource;
import com.example.rest_servlet_jdbc.dto.TeacherDto;
import com.example.rest_servlet_jdbc.entity.Course;
import com.example.rest_servlet_jdbc.entity.Teacher;
import com.example.rest_servlet_jdbc.mapper.TeacherMapper;
import org.junit.jupiter.api.*;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TeacherMapperTest {

    private final static DataSource dataSource = DBCPDataSource.getDataSource();

    private final CourseDao courseDao;

    private final TeacherMapper teacherMapper;

    public TeacherMapperTest() {
        courseDao = new CourseDaoJDBC(dataSource);
        teacherMapper = new TeacherMapper(courseDao);
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
    void toTeacherDtoTest() throws SQLException {
        Teacher teacher = new Teacher();
        teacher.setId(1L);
        teacher.setFirstname("Dmitriy");
        teacher.setLastname("Sergeev");
        teacher.setFaculty("Business");

        Set<Course> courses = new HashSet<>();
        Course management = new Course();
        management.setId(1L);
        management.setTitle("Management");
        courses.add(management);
        Course marketing = new Course();
        marketing.setId(2L);
        marketing.setTitle("Marketing");
        courses.add(marketing);
        teacher.setCourses(courses);

        TeacherDto teacherDto = teacherMapper.toTeacherDto(teacher);

        Assertions.assertEquals(teacher.getId(), teacherDto.getId());
        Assertions.assertEquals(teacher.getFirstname(), teacherDto.getFirstname());
        Assertions.assertEquals(teacher.getLastname(), teacherDto.getLastname());

        Assertions.assertTrue(teacherDto.getCourses().contains("Management"));
        Assertions.assertTrue(teacherDto.getCourses().contains("Marketing"));
    }

    @Test
    void toTeacherTest() throws SQLException {
        TeacherDto teacherDto = new TeacherDto();
        teacherDto.setId(3L);
        teacherDto.setFirstname("Vitaly");
        teacherDto.setLastname("Kolosov");
        teacherDto.setFaculty("Technical");
        Set<String> courses = new HashSet<>();
        courses.add("Programming");
        courses.add("Mathematics");
        teacherDto.setCourses(courses);

        Teacher teacher = teacherMapper.toTeacher(teacherDto);

        Assertions.assertEquals(teacherDto.getId(), teacher.getId());
        Assertions.assertEquals(teacherDto.getFirstname(), teacher.getFirstname());
        Assertions.assertEquals(teacherDto.getLastname(), teacher.getLastname());

        Assertions.assertEquals(teacherDto.getCourses().size(), teacher.getCourses().size());

    }

}