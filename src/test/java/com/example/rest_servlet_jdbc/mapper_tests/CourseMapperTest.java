package com.example.rest_servlet_jdbc.mapper_tests;

import com.example.rest_servlet_jdbc.dao.*;
import com.example.rest_servlet_jdbc.dto.CourseDto;
import com.example.rest_servlet_jdbc.entity.Course;
import com.example.rest_servlet_jdbc.entity.Student;
import com.example.rest_servlet_jdbc.entity.Teacher;
import com.example.rest_servlet_jdbc.mapper.CourseMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class CourseMapperTest {


    private final static DataSource dataSource = DBCPDataSource.getDataSource();

    private final TeacherDao teacherDao;

    private final StudentDao studentDao;

    private final CourseMapper courseMapper;


    public CourseMapperTest() {
        teacherDao = new TeacherDaoJDBC(dataSource);
        studentDao = new StudentDaoJDBC(dataSource);
        courseMapper = new CourseMapper(teacherDao, studentDao);
    }

    @BeforeAll
    static void initDb() {
        DBCPDataSource.createTestTables(dataSource);
    }

    @AfterAll
    static void destroyDb() {
        DBCPDataSource.deleteTestTables(dataSource);
    }

    @Test
    void toCourseDtoTest() throws SQLException {
        Course course = new Course();
        course.setId(1L);
        course.setTitle("Management");
        Teacher teacher = new Teacher();
        teacher.setId(1L);
        teacher.setFirstname("Lev");
        teacher.setLastname("Tolstoy");
        teacher.setFaculty("Literature");
        course.setTeacher(teacher);

        Set<Student> students = new HashSet<>();
        Student student1 = new Student();
        student1.setId(1L);
        student1.setFirstname("Ivan");
        student1.setLastname("Ivanov");
        student1.setAge(19);
        students.add(student1);
        Student student2 = new Student();
        student2.setId(2L);
        student2.setFirstname("Vladimir");
        student2.setLastname("Frolov");
        student2.setAge(21);
        students.add(student2);
        course.setStudents(students);

        CourseDto courseDto = courseMapper.toCourseDto(course);

        Assertions.assertEquals(course.getId(), courseDto.getId());
        Assertions.assertEquals(course.getTitle(), courseDto.getTitle());
        Assertions.assertEquals(course.getTeacher().getFirstname() + " " + course.getTeacher().getLastname(), courseDto.getTeacher());

        Assertions.assertTrue(courseDto.getStudents().contains("Ivan Ivanov"));
        Assertions.assertTrue(courseDto.getStudents().contains("Vladimir Frolov"));

    }

    @Test
    void toCourseTest() throws SQLException {
        CourseDto courseDto = new CourseDto();
        courseDto.setId(2L);
        courseDto.setTitle("DevOps");
        courseDto.setTeacher("Pavel Pavlov");
        Set<String> students = new HashSet<>();
        students.add("Ivan Ivanov");
        students.add("Fedor Petrov");
        courseDto.setStudents(students);

        Course course = courseMapper.toCourse(courseDto);

        Assertions.assertEquals(courseDto.getId(), course.getId());
        Assertions.assertEquals(courseDto.getTitle(), course.getTitle());
        Assertions.assertEquals(courseDto.getTeacher(), course.getTeacher().getFirstname() + " " + course.getTeacher().getLastname());

        Assertions.assertEquals(courseDto.getStudents().size(), course.getStudents().size());
    }

}