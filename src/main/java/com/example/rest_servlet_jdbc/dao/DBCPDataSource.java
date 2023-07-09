package com.example.rest_servlet_jdbc.dao;

import org.apache.commons.dbcp.BasicDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DBCPDataSource {

    private static BasicDataSource dataSource = new BasicDataSource();

    static {
        Properties properties = getProperties();
        dataSource.setDriverClassName(properties.getProperty("driverClassName"));
        dataSource.setUrl(properties.getProperty("url"));
        dataSource.setUsername(properties.getProperty("user"));
        dataSource.setPassword(properties.getProperty("password"));
        dataSource.setMinIdle(5);
        dataSource.setMaxIdle(10);
        dataSource.setMaxOpenPreparedStatements(100);
    }

    private DBCPDataSource(){

    }


    private static Properties getProperties() {
        Properties properties = new Properties();
        try (InputStream fileInputStream = DBCPDataSource.class.getClassLoader().getResourceAsStream("application.properties")) {
            properties.load(fileInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }


    public static DataSource getDataSource() {
        return dataSource;
    }


    public static void createTestTables(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE student (id BIGSERIAL PRIMARY KEY, firstname VARCHAR(255) NOT NULL, lastname VARCHAR(255) NOT NULL,age INTEGER);");
            statement.execute("CREATE TABLE teacher (id BIGSERIAL PRIMARY KEY, firstname VARCHAR(255) NOT NULL, lastname VARCHAR(255) NOT NULL, " +
                    "faculty VARCHAR(255) NOT NULL);");
            statement.execute("CREATE TABLE course (id BIGSERIAL PRIMARY KEY, title VARCHAR(255) NOT NULL, teacher_id BIGINT, " +
                    "FOREIGN KEY(teacher_id) REFERENCES teacher(id));");
            statement.execute("CREATE TABLE student_course (student_id BIGINT NOT NULL, course_id BIGINT NOT NULL, PRIMARY KEY (student_id, course_id), " +
                    "FOREIGN KEY(student_id) REFERENCES student(id), FOREIGN KEY(course_id) REFERENCES course(id));");
            statement.execute("INSERT INTO student (firstname, lastname, age) VALUES ('Ivan', 'Ivanov', 20), ('Fedor', 'Petrov', 19), ('Alexey', 'Popov', 25);");
            statement.execute("INSERT INTO teacher (firstname, lastname, faculty) VALUES ('Pavel', 'Pavlov', 'Technical'), ('Dmitriy', 'Sergeev', 'Business');");
            statement.execute("INSERT INTO course (title, teacher_id) VALUES ('Programming', 1), ('Mathematics', 1), ('Marketing', 2);");
            statement.execute("INSERT INTO student_course (student_id, course_id) VALUES (1, 1), (1, 2), (2, 3), (3, 1), (3, 2), (3, 3);");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteTestTables(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement()) {
            statement.execute("DROP TABLE student_course;");
            statement.execute("DROP TABLE course;");
            statement.execute("DROP TABLE teacher;");
            statement.execute("DROP TABLE student;");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}

