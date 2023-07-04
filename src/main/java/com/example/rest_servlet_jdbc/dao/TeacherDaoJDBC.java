package com.example.rest_servlet_jdbc.dao;

import com.example.rest_servlet_jdbc.entity.Course;
import com.example.rest_servlet_jdbc.entity.Teacher;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class TeacherDaoJDBC implements TeacherDao {

    private final String GET = "SELECT t.id, firstname, lastname, faculty, c.id as course_id, c.title as course_title FROM teacher t " +
            "LEFT JOIN course c ON t.id = c.teacher_id WHERE t.id = ?";
    private final String GET_ALL = "SELECT t.id, firstname, lastname, faculty, c.id as course_id, c.title as course_title FROM teacher t " +
            "LEFT JOIN course c ON t.id = c.teacher_id";
    private final String INSERT = "INSERT INTO teacher (firstname, lastname, faculty) VALUES(?, ?, ?)";
    private final String UPDATE = "UPDATE teacher SET firstname = ?, lastname = ?, faculty = ? WHERE id = ?";
    private final String DELETE = "DELETE FROM teacher WHERE id = ?";
    private final String DELETE_COURSES = "UPDATE course SET teacher_id = NULL WHERE teacher_id = ?";
    private final String CLEAR_COURSE = "UPDATE course SET teacher_id = NULL WHERE title = ?";

    private final String GET_BY_FULLNAME = "SELECT t.id, firstname, lastname, faculty, c.id as course_id, c.title as course_title FROM teacher t " +
            "LEFT JOIN course c ON t.id = c.teacher_id WHERE firstname = ? AND lastname = ?";

    private final String UPDATE_COURSES = "UPDATE course SET teacher_id = ? WHERE title = ?";

    private final ConnectionPool connectionPool;


    public TeacherDaoJDBC(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }


    @Override
    public Optional<Teacher> findByFullName(String firstname, String lastname) throws SQLException {
        Teacher teacher = null;
        Connection connection = null;
        try {
            connection = connectionPool.getConnection();

        try (PreparedStatement preparedStatement = connection.prepareStatement(GET_BY_FULLNAME)) {
            preparedStatement.setString(1, firstname);
            preparedStatement.setString(2, lastname);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {

                teacher = new Teacher();
                Set<Course> courses = new HashSet<>();

                while (resultSet.next()) {
                    teacher.setCourses(courses);

                    if (teacher.getId() == null) {
                        teacher.setId(resultSet.getLong("id"));
                    }

                    if (teacher.getFirstname() == null) {
                        teacher.setFirstname(resultSet.getString("firstname"));
                    }

                    if (teacher.getLastname() == null) {
                        teacher.setLastname(resultSet.getString("lastname"));
                    }

                    if (teacher.getFaculty() == null) {
                        teacher.setFaculty(resultSet.getString("faculty"));
                    }

                    Long courseId = resultSet.getLong("course_id");
                    String courseTitle = resultSet.getString("course_title");
                        if(courseId != null && courseTitle != null) {
                            Course course = new Course();
                            course.setId(courseId);
                            course.setTitle(courseTitle);
                            courses.add(course);
                        }
                }

            }
        }
        } finally {
            connectionPool.releaseConnection(connection);
        }
        return Optional.ofNullable(teacher);
    }

    @Override
    public Teacher save(Teacher teacher) throws SQLException {
        Connection connection = null;
        try {
            connection = connectionPool.getConnection();

            if (teacher.getId() == null) {
                try (PreparedStatement insertStatement = connection.prepareStatement(INSERT)) {
                    insertStatement.setString(1, teacher.getFirstname());
                    insertStatement.setString(2, teacher.getLastname());
                    insertStatement.setString(3, teacher.getFaculty());
                    insertStatement.execute();
                }
                    Set<Course> courses = teacher.getCourses();
                    if(courses != null && courses.size() > 0) {
                        Teacher teacherFromDb = findByFullName(teacher.getFirstname(), teacher.getLastname()).get();

                        for (Course course : courses) {
                            try (PreparedStatement updateCourseStatement = connection.prepareStatement(UPDATE_COURSES)) {
                                updateCourseStatement.setLong(1, teacherFromDb.getId());
                                updateCourseStatement.setString(2, course.getTitle());
                                updateCourseStatement.execute();
                            }
                        }
                    }

            } else {
                try (PreparedStatement updateStatement = connection.prepareStatement(UPDATE)) {
                    updateStatement.setString(1, teacher.getFirstname());
                    updateStatement.setString(2, teacher.getLastname());
                    updateStatement.setString(3, teacher.getFaculty());
                    updateStatement.setLong(4, teacher.getId());
                    updateStatement.execute();
                }
                Set<Course> courses = teacher.getCourses();

                if (courses != null && courses.size() > 0) {
                    Teacher teacherFromDb = findById(teacher.getId()).get();
                    Set<Course> coursesFromDb = teacherFromDb.getCourses();

                    for (Course course : courses) {
                        if (!coursesFromDb.contains(course)) {
                            try (PreparedStatement updateCourseStatement = connection.prepareStatement(UPDATE_COURSES)) {
                                updateCourseStatement.setLong(1, teacher.getId());
                                updateCourseStatement.setString(2, course.getTitle());
                                updateCourseStatement.execute();
                            }
                        }
                    }

                    if(coursesFromDb.size() > 0) {
                        for (Course courseFromDb : coursesFromDb) {
                            if (courseFromDb != null && !courses.contains(courseFromDb)) {
                                try (PreparedStatement clearCourseStatement = connection.prepareStatement(CLEAR_COURSE)) {
                                    clearCourseStatement.setString(1, courseFromDb.getTitle());
                                    clearCourseStatement.execute();
                                }
                            }
                        }
                    }

                } else {
                    try (PreparedStatement deleteCourseStatement = connection.prepareStatement(DELETE_COURSES)) {
                        deleteCourseStatement.setLong(1, teacher.getId());
                        deleteCourseStatement.execute();
                    }
                }
            }
        } finally {
            connectionPool.releaseConnection(connection);
        }
        return teacher;
    }

    @Override
    public Optional<Teacher> findById(Long id) throws SQLException {
        Teacher teacher = null;

        Connection connection = null;
        try {
            connection = connectionPool.getConnection();

        try (PreparedStatement preparedStatement = connection.prepareStatement(GET)) {
            preparedStatement.setLong(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {

                teacher = new Teacher();
                Set<Course> courses = new HashSet<>();

                while (resultSet.next()) {
                    teacher.setCourses(courses);

                    if (teacher.getId() == null) {
                        teacher.setId(resultSet.getLong("id"));
                    }

                    if (teacher.getFirstname() == null) {
                        teacher.setFirstname(resultSet.getString("firstname"));
                    }

                    if (teacher.getLastname() == null) {
                        teacher.setLastname(resultSet.getString("lastname"));
                    }

                    if (teacher.getFaculty() == null) {
                        teacher.setFaculty(resultSet.getString("faculty"));
                    }

                    Course course = new Course();
                    course.setId(resultSet.getLong("course_id"));
                    course.setTitle(resultSet.getString("course_title"));
                    courses.add(course);
                }

            }
        }
        } finally {
            connectionPool.releaseConnection(connection);
        }
        return Optional.ofNullable(teacher);
    }

    @Override
    public List<Teacher> findAll() throws SQLException {
        List<Teacher> teachers = null;

        Connection connection = null;
        try {
            connection = connectionPool.getConnection();

        try (PreparedStatement preparedStatement = connection.prepareStatement(GET_ALL)) {

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                Map<Long, Teacher> teacherMap = new HashMap<>();
                while (resultSet.next()) {
                    Long id = resultSet.getLong("id");
                    if(teacherMap.containsKey(id)) {
                        Teacher teacher = teacherMap.get(id);
                        Course course = new Course();
                        course.setId(resultSet.getLong("course_id"));
                        course.setTitle(resultSet.getString("course_title"));
                        teacher.getCourses().add(course);
                    } else {
                        Teacher teacher = new Teacher();
                        teacher.setId(resultSet.getLong("id"));
                        teacher.setFirstname(resultSet.getString("firstname"));
                        teacher.setLastname(resultSet.getString("lastname"));
                        teacher.setFaculty(resultSet.getString("faculty"));
                        Set<Course> courses = new HashSet<>();
                        Course course = new Course();
                        course.setId(resultSet.getLong("course_id"));
                        course.setTitle(resultSet.getString("course_title"));
                        courses.add(course);
                        teacher.setCourses(courses);
                        teacherMap.put(id, teacher);
                    }
                }
               teachers = teacherMap.values().stream().collect(Collectors.toList());
            }
        }
        } finally {
            connectionPool.releaseConnection(connection);
        }
        return teachers;
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



