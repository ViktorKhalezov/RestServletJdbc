package com.example.rest_servlet_jdbc.dao_tests;

import com.example.rest_servlet_jdbc.dao.*;
import com.example.rest_servlet_jdbc.entity.Teacher;
import org.apache.commons.dbcp.BasicDataSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TeacherDaoTest {

    private final TeacherDao teacherDao;
    private final ConnectionPool connectionPool;
    private final static BasicDataSource dataSource = new BasicDataSource();

    public TeacherDaoTest() {
        this.connectionPool = new ConnectionPoolImpl();
        this.teacherDao = new TeacherDaoJDBC(connectionPool);
    }

    private class ConnectionPoolImpl implements ConnectionPool {

        @Override
        public Connection getConnection() {
            Connection connection = null;
            try {
                connection = dataSource.getConnection();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return connection;
        }

        @Override
        public boolean releaseConnection(Connection connection) {
            return false;
        }
    }

    @BeforeAll
    static void createDb () {
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:mem:test");
        dataSource.setUsername("user");
        dataSource.setPassword("password");
        dataSource.setMinIdle(5);
        dataSource.setMaxIdle(10);
        dataSource.setMaxOpenPreparedStatements(100);

        try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE student (id BIGSERIAL PRIMARY KEY, firstname VARCHAR(255) NOT NULL, lastname VARCHAR(255) NOT NULL,age INTEGER);");
            statement.execute("CREATE TABLE teacher (id BIGSERIAL PRIMARY KEY, firstname VARCHAR(255) NOT NULL, lastname VARCHAR(255) NOT NULL, " +
                    "faculty VARCHAR(255) NOT NULL);");
            statement.execute("CREATE TABLE course (id BIGSERIAL PRIMARY KEY, title VARCHAR(255) NOT NULL, teacher_id BIGINT, " +
                    "FOREIGN KEY(teacher_id) REFERENCES teacher(id));");
            statement.execute("CREATE TABLE student_course (student_id BIGINT NOT NULL, course_id BIGINT NOT NULL, PRIMARY KEY (student_id, course_id), " +
                    "FOREIGN KEY(student_id) REFERENCES student(id), FOREIGN KEY(course_id) REFERENCES course(id));");
            statement.execute("insert into student (firstname, lastname, age) values ('Ivan', 'Ivanov', 20), ('Fedor', 'Petrov', 19), ('Alexey', 'Popov', 25);");
            statement.execute("insert into teacher (firstname, lastname, faculty) values ('Pavel', 'Pavlov', 'Technical'), ('Dmitriy', 'Sergeev', 'Business');");
            statement.execute("insert into course (title, teacher_id) values ('Programming', 1), ('Mathematics', 1), ('Marketing', 2);");
            statement.execute("insert into student_course (student_id, course_id) values (1, 1), (1, 2), (2, 3), (3, 1), (3, 2), (3, 3);");
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
        Assertions.assertEquals(2, teacherDao.findAll().size());
    }

}
