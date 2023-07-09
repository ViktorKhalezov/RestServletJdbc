package com.example.rest_servlet_jdbc.dao_tests;

import com.example.rest_servlet_jdbc.dao.*;
import com.example.rest_servlet_jdbc.entity.Teacher;
import org.junit.jupiter.api.*;

import javax.sql.DataSource;
import java.sql.SQLException;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TeacherDaoTest {

    private final DataSource dataSource = DBCPDataSource.getDataSource();
    private final TeacherDao teacherDao;

    public TeacherDaoTest() {
        this.teacherDao = new TeacherDaoJDBC(dataSource);
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
    void findByFullNameTest() throws SQLException {
        String firstname = "Dmitriy";
        String lastname = "Sergeev";
        Teacher teacher = teacherDao.findByFullName(firstname, lastname).get();
        Assertions.assertEquals(firstname, teacher.getFirstname());
        Assertions.assertEquals(lastname, teacher.getLastname());
    }


    @Test
    void saveTest() throws SQLException {
        Teacher teacher = new Teacher();
        teacher.setFirstname("Vladimir");
        teacher.setLastname("Kozlov");
        teacher.setFaculty("Business");
        teacherDao.save(teacher);
        Assertions.assertEquals(teacher.getFirstname(), teacherDao.findByFullName("Vladimir", "Kozlov").get().getFirstname());
        Assertions.assertEquals(teacher.getLastname(), teacherDao.findByFullName("Vladimir", "Kozlov").get().getLastname());
        Assertions.assertEquals(teacher.getFaculty(), teacherDao.findByFullName("Vladimir", "Kozlov").get().getFaculty());
    }

    @Test
    void findByIdTest() throws SQLException {
        Teacher teacher = teacherDao.findById(2L).get();
        Assertions.assertEquals(2L, teacher.getId());
        Assertions.assertEquals("Dmitriy", teacher.getFirstname());
        Assertions.assertEquals("Sergeev", teacher.getLastname());
    }

    @Test
    void findAllTest() throws SQLException {
        Assertions.assertEquals(2, teacherDao.findAll().size());
    }

    @Test
    void deleteByIdTest() throws SQLException {
        teacherDao.deleteById(1L);
        Assertions.assertEquals(1, teacherDao.findAll().size());
    }

}
