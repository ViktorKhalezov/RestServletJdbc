package com.example.rest_servlet_jdbc.dao_tests;


import com.example.rest_servlet_jdbc.dao.ConnectionPool;
import com.example.rest_servlet_jdbc.dao.CourseDao;
import com.example.rest_servlet_jdbc.dao.CourseDaoJDBC;
import com.example.rest_servlet_jdbc.entity.Course;
import com.example.rest_servlet_jdbc.entity.Teacher;
import org.apache.commons.dbcp.BasicDataSource;
import org.junit.jupiter.api.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CourseDaoTest {

    private final CourseDao courseDao;
    private final ConnectionPool connectionPool;
    private final static BasicDataSource dataSource = new BasicDataSource();

    public CourseDaoTest() {
        this.connectionPool = new ConnectionPoolImpl();
        this.courseDao = new CourseDaoJDBC(connectionPool);
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

//    @Test
//    void updateTest() throws SQLException {
//        Course course = courseDao.findById(2L).get();
//        System.out.println(course.getId());
//        course.setTitle("Management");
//        courseDao.save(course);
//        Assertions.assertEquals(course.getTitle(), courseDao.findById(2L).get().getTitle());
//    }

    @Test
    void findByIdTest() throws SQLException {
        Course course = courseDao.findById(1L).get();
        Assertions.assertEquals(1L, course.getId());
        Assertions.assertEquals("Programming", course.getTitle());
    }

    @Test
    void findAllTest() throws SQLException {
        Assertions.assertEquals(4, courseDao.findAll().size());
    }

    @Test
    void deleteTest() throws SQLException {
        courseDao.deleteById(3L);
        Assertions.assertEquals(3, courseDao.findAll().size());
    }

}



 //   CREATE TABLE student (id BIGSERIAL PRIMARY KEY, firstname VARCHAR(255) NOT NULL, lastname VARCHAR(255) NOT NULL,age INTEGER);

//        CREATE TABLE teacher (id BIGSERIAL PRIMARY KEY, firstname VARCHAR(255) NOT NULL, lastname VARCHAR(255) NOT NULL, faculty VARCHAR(255) NOT NULL);

 //       CREATE TABLE course (id BIGSERIAL PRIMARY KEY, title VARCHAR(255) NOT NULL, teacher_id BIGINT, FOREIGN KEY(teacher_id) REFERENCES teacher(id));

//        CREATE TABLE student_course (student_id BIGINT NOT NULL, course_id BIGINT NOT NULL, PRIMARY KEY (student_id, course_id), FOREIGN KEY(student_id) REFERENCES student(id), FOREIGN KEY(course_id) REFERENCES course(id));


//    insert into student (firstname, lastname, age) values ('Ivan', 'Ivanov', 20), ('Fedor', 'Petrov', 19), ('Alexey', 'Popov', 25);

 //              insert into teacher (firstname, lastname, faculty) values ('Pavel', 'Pavlov', 'Technical'), ('Dmitriy', 'Sergeev', 'Business');

  //             insert into course (title, teacher_id) values ('Programming', 1), ('Mathmatics', 1), ('Marketing', 2);

 //              insert into student_course (student_id, course_id) values (1, 1), (1, 2), (2, 3), (3, 1), (3, 2), (3, 3);


//--delete from student_course;
//        --delete from course;
//        --delete from teacher;
//        --delete from student;