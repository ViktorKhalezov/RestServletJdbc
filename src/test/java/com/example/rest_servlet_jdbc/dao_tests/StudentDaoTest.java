package com.example.rest_servlet_jdbc.dao_tests;

import com.example.rest_servlet_jdbc.dao.DBCPDataSource;
import com.example.rest_servlet_jdbc.dao.StudentDao;
import com.example.rest_servlet_jdbc.dao.StudentDaoJDBC;
import com.example.rest_servlet_jdbc.entity.Student;
import org.junit.jupiter.api.*;

import javax.sql.DataSource;
import java.sql.SQLException;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class StudentDaoTest {

    private final DataSource dataSource = DBCPDataSource.getDataSource();
    private final StudentDao studentDao;

    public StudentDaoTest() {
        this.studentDao = new StudentDaoJDBC(dataSource);
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
        String firstname = "Alexey";
        String lastname = "Popov";
        Student student = studentDao.findByFullName(firstname, lastname).get();
        Assertions.assertEquals(firstname, student.getFirstname());
        Assertions.assertEquals(lastname, student.getLastname());
    }


    @Test
    void saveTest() throws SQLException {
        Student student = new Student();
        student.setFirstname("Valentin");
        student.setLastname("Frolov");
        student.setAge(40);
        studentDao.save(student);
        Assertions.assertEquals(student.getFirstname(), studentDao.findByFullName("Valentin", "Frolov").get().getFirstname());
        Assertions.assertEquals(student.getLastname(), studentDao.findByFullName("Valentin", "Frolov").get().getLastname());
    }

    @Test
    void findByIdTest() throws SQLException {
        Student student = studentDao.findById(3L).get();
        Assertions.assertEquals(3L, student.getId());
        Assertions.assertEquals("Alexey", student.getFirstname());
        Assertions.assertEquals("Popov", student.getLastname());
    }

    @Test
    void findAllTest() throws SQLException {
        Assertions.assertEquals(3, studentDao.findAll().size());
    }

    @Test
    void deleteTest() throws SQLException {
        studentDao.deleteById(2L);
        Assertions.assertEquals(2, studentDao.findAll().size());
    }

}
