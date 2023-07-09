package com.example.rest_servlet_jdbc.dao_tests;


import com.example.rest_servlet_jdbc.dao.CourseDao;
import com.example.rest_servlet_jdbc.dao.CourseDaoJDBC;
import com.example.rest_servlet_jdbc.dao.DBCPDataSource;
import com.example.rest_servlet_jdbc.entity.Course;
import com.example.rest_servlet_jdbc.entity.Teacher;
import org.junit.jupiter.api.*;
import javax.sql.DataSource;
import java.sql.SQLException;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CourseDaoTest {

    private final DataSource dataSource = DBCPDataSource.getDataSource();
    private final CourseDao courseDao;

    public CourseDaoTest() {
        this.courseDao = new CourseDaoJDBC(dataSource);
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
    void findByTitleTest() throws SQLException {
        String title = "Programming";
        Course course = courseDao.findByTitle(title).get();
        Assertions.assertEquals(title, course.getTitle());
    }

    @Test
    void saveTest() throws SQLException {
        Course course = new Course();
        course.setTitle("DevOps");
        Teacher teacher = new Teacher();
        teacher.setId(1L);
        course.setTeacher(teacher);
        courseDao.save(course);
        Assertions.assertEquals(course.getTitle(), courseDao.findByTitle("DevOps").get().getTitle());
    }

    @Test
    void findByIdTest() throws SQLException {
        Course course = courseDao.findById(1L).get();
        Assertions.assertEquals(1L, course.getId());
        Assertions.assertEquals("Programming", course.getTitle());
    }

    @Test
    void findAllTest() throws SQLException {
        Assertions.assertEquals(3, courseDao.findAll().size());
    }

    @Test
    void deleteTest() throws SQLException {
        courseDao.deleteById(3L);
        Assertions.assertEquals(2, courseDao.findAll().size());
    }

}