package com.example.rest_servlet_jdbc.dao;

import com.example.rest_servlet_jdbc.entity.Course;
import com.example.rest_servlet_jdbc.entity.Student;
import com.example.rest_servlet_jdbc.entity.Teacher;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class StudentDaoJDBC implements StudentDao {


    private final String INSERT = "INSERT INTO student (firstname, lastname, age) VALUES(?, ?, ?)";
    private final String INSERT_COURSES = "INSERT INTO student_course (student_id, course_id) VALUES(?, ?)";
    private final String UPDATE = "UPDATE student SET firstname = ?, lastname = ?, age = ? WHERE id = ?";
    private final String DELETE = "DELETE FROM student WHERE id = ?";
    private final String DELETE_COURSES = "DELETE FROM student_course WHERE student_id = ?";

    private final String GET = "SELECT id, firstname, lastname, age FROM student WHERE id = ?";

    private final String GET_ALL = "SELECT id, firstname, lastname, age FROM student";

    private final String GET_BY_FULLNAME = "SELECT id, firstname, lastname, age FROM student WHERE firstname = ? AND lastname = ?";

    private final String GET_COURSES = "SELECT c.id, c.title, c.teacher_id, sc.student_id FROM course c LEFT JOIN student_course sc ON c.id = sc.course_id WHERE sc.student_id = ?";

    private final String CLEAR_COURSE = "DELETE FROM student_course WHERE student_id = ? AND course_id =?";

    private final ConnectionPool connectionPool;


    public StudentDaoJDBC(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public Optional<Student> findByFullName(String firstname, String lastname) throws SQLException {
        Student student = null;
        Connection connection = null;
        try {
            connection = connectionPool.getConnection();

            try(PreparedStatement preparedStatement = connection.prepareStatement(GET_BY_FULLNAME)) {
                preparedStatement.setString(1, firstname);
                preparedStatement.setString(2, lastname);

                try(ResultSet resultSet = preparedStatement.executeQuery()) {
                    student = new Student();

                    while (resultSet.next()) {
                        student.setId(resultSet.getLong("id"));
                        student.setFirstname(resultSet.getString("firstname"));
                        student.setLastname(resultSet.getString("lastname"));
                        student.setAge(resultSet.getInt("age"));
                    }


                }

            }

            if(student != null) {
                Set<Course> courses = new HashSet<>();
                student.setCourses(courses);

                try (PreparedStatement getCourseStatement = connection.prepareStatement(GET_COURSES)) {
                    getCourseStatement.setLong(1, student.getId());

                    try(ResultSet resultSet = getCourseStatement.executeQuery()) {

                        while (resultSet.next()) {
                            Course course = new Course();
                            course.setId(resultSet.getLong("id"));
                            course.setTitle(resultSet.getString("title"));
                            Teacher teacher = new Teacher();
                            teacher.setId(resultSet.getLong("teacher_id"));
                            course.setTeacher(teacher);
                            courses.add(course);
                        }
                    }
                }
            }

        } finally {
            connectionPool.releaseConnection(connection);
        }

        return Optional.ofNullable(student);
    }


    @Override
    public Student save(Student student) throws SQLException {
        Connection connection = null;
        try {
            connection = connectionPool.getConnection();

            if(student.getId() == null) {
                try(PreparedStatement insertStatement = connection.prepareStatement(INSERT)) {
                    insertStatement.setString(1, student.getFirstname());
                    insertStatement.setString(2, student.getLastname());
                    insertStatement.setInt(3, student.getAge());
                    insertStatement.execute();
                }
                Set<Course> courses = student.getCourses();
                if(courses != null && courses.size() > 0) {
                    Student studentFromDb = findByFullName(student.getFirstname(), student.getLastname()).get();

                    for(Course course : courses) {
                        try(PreparedStatement insertCourseStatement = connection.prepareStatement(INSERT_COURSES)) {
                            insertCourseStatement.setLong(1, studentFromDb.getId());
                            insertCourseStatement.setLong(2, course.getId());
                            insertCourseStatement.execute();
                        }
                    }
                }

            } else {
                try (PreparedStatement updateStatement = connection.prepareStatement(UPDATE)) {
                    updateStatement.setString(1, student.getFirstname());
                    updateStatement.setString(2, student.getLastname());
                    updateStatement.setInt(3, student.getAge());
                    updateStatement.setLong(4, student.getId());
                    updateStatement.execute();
                }
                Set<Course> courses = student.getCourses();
                if (courses != null && courses.size() > 0) {
                    Student studentFromDb = findById(student.getId()).get();
                    Set<Course> coursesFromDb = studentFromDb.getCourses();

                    for(Course course : courses) {
                        if(!coursesFromDb.contains(course)) {
                            try(PreparedStatement updateCourseStatement = connection.prepareStatement(INSERT_COURSES)) {
                                updateCourseStatement.setLong(1, student.getId());
                                updateCourseStatement.setLong(2, course.getId());
                                updateCourseStatement.execute();
                            }
                        }
                    }

                    if(coursesFromDb.size() > 0) {
                        for(Course courseFromDb : coursesFromDb) {
                            if(courseFromDb != null && !courses.contains(courseFromDb)) {
                                try(PreparedStatement clearCourseStatement = connection.prepareStatement(CLEAR_COURSE)) {
                                    clearCourseStatement.setLong(1, student.getId());
                                    clearCourseStatement.setLong(2, courseFromDb.getId());
                                    clearCourseStatement.execute();
                                }
                            }
                        }
                    }

                } else {
                    try(PreparedStatement deleteCourseStatement = connection.prepareStatement(DELETE_COURSES)) {
                        deleteCourseStatement.setLong(1, student.getId());
                        deleteCourseStatement.execute();
                    }
                }
            }
        } finally {
            connectionPool.releaseConnection(connection);
        }
        return student;
    }

    @Override
    public Optional<Student> findById(Long id) throws SQLException {
        Student student = null;
        Connection connection = null;

        try {
            connection = connectionPool.getConnection();

            try(PreparedStatement preparedStatement = connection.prepareStatement(GET)) {
                preparedStatement.setLong(1, id);

                try(ResultSet resultSet = preparedStatement.executeQuery()) {
                    student = new Student();
                    while (resultSet.next()) {
                        student.setId(resultSet.getLong("id"));
                        student.setFirstname(resultSet.getString("firstname"));
                        student.setLastname(resultSet.getString("lastname"));
                        student.setAge(resultSet.getInt("age"));
                    }
                }
            }

            if(student != null) {
                Set<Course> courses = new HashSet<>();
                student.setCourses(courses);

                try (PreparedStatement getCourseStatement = connection.prepareStatement(GET_COURSES)) {
                    getCourseStatement.setLong(1, student.getId());

                    try(ResultSet resultSet = getCourseStatement.executeQuery()) {

                        while (resultSet.next()) {
                            Course course = new Course();
                            course.setId(resultSet.getLong("id"));
                            course.setTitle(resultSet.getString("title"));
                            Teacher teacher = new Teacher();
                            teacher.setId(resultSet.getLong("teacher_id"));
                            course.setTeacher(teacher);
                            courses.add(course);
                        }
                    }
                }
            }

        } finally {
            connectionPool.releaseConnection(connection);
        }

        return Optional.ofNullable(student);
    }

    @Override
    public List<Student> findAll() throws SQLException {
        List<Student> students = new ArrayList<>();
        Connection connection = null;

        try {
            connection = connectionPool.getConnection();

            try (PreparedStatement preparedStatement = connection.prepareStatement(GET_ALL)) {

                try(ResultSet resultSet = preparedStatement.executeQuery()) {

                    while (resultSet.next()) {
                        Student student = new Student();
                        student.setId(resultSet.getLong("id"));
                        student.setFirstname(resultSet.getString("firstname"));
                        student.setLastname(resultSet.getString("lastname"));
                        student.setAge(resultSet.getInt("age"));
                        students.add(student);
                    }
                }
            }

            if(students.size() > 0) {
                for(Student student : students) {
                    Set<Course> courses = new HashSet<>();
                    student.setCourses(courses);

                    try(PreparedStatement preparedStatement = connection.prepareStatement(GET_COURSES)) {
                        preparedStatement.setLong(1, student.getId());

                        try (ResultSet resultSet = preparedStatement.executeQuery()) {

                            while (resultSet.next()) {
                                Course course = new Course();
                                course.setId(resultSet.getLong("id"));
                                course.setTitle(resultSet.getString("title"));
                                Teacher teacher = new Teacher();
                                teacher.setId(resultSet.getLong("teacher_id"));
                                course.setTeacher(teacher);
                                courses.add(course);
                            }

                        }
                    }
                }
            }

        } finally {
            connectionPool.releaseConnection(connection);
        }

        return students;
    }


    @Override
    public void deleteById(Long id) throws SQLException {
        Connection connection = null;
        try {
            connection = connectionPool.getConnection();
            try (PreparedStatement deleteCourseStatement = connection.prepareStatement(DELETE_COURSES)) {
                deleteCourseStatement.setLong(1, id);
                deleteCourseStatement.execute();
            }
            try (PreparedStatement deleteStatement = connection.prepareStatement(DELETE)) {
                deleteStatement.setLong(1, id);
                deleteStatement.execute();
            }
        } finally {
            connectionPool.releaseConnection(connection);
        }
    }

}


