package com.example.rest_servlet_jdbc.dao;

import com.example.rest_servlet_jdbc.entity.Course;
import com.example.rest_servlet_jdbc.entity.Student;
import com.example.rest_servlet_jdbc.entity.Teacher;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class CourseDaoJDBC implements CourseDao {


    private final String GET = "SELECT c.id, title, teacher_id, t.firstname as teacher_firstname, t.lastname as teacher_lastname, t.faculty, " +
            "sc.student_id, s.firstname as student_firstname, s.lastname as student_lastname, s.age FROM course c " +
            "LEFT JOIN teacher t ON c.teacher_id = t.id " +
            "LEFT JOIN student_course sc ON c.id = sc.course_id LEFT JOIN student s ON sc.student_id = s.id WHERE c.id = ?";

    private final String GET_ALL = "SELECT c.id, title, teacher_id, t.firstname as teacher_firstname, t.lastname as teacher_lastname, t.faculty, " +
            "sc.student_id, s.firstname as student_firstname, s.lastname as student_lastname, s.age FROM course c " +
            "LEFT JOIN teacher t ON c.teacher_id = t.id " +
            "LEFT JOIN student_course sc ON c.id = sc.course_id LEFT JOIN student s ON sc.student_id = s.id";

    private final String INSERT = "INSERT INTO course (title, teacher_id) VALUES (?, ?)";
    private final String INSERT_STUDENT = "INSERT INTO student_course (student_id, course_id) VALUES (?, ?)";
    private final String UPDATE = "UPDATE course SET title = ?, teacher_id = ? WHERE id = ?";
    private final String DELETE = "DELETE FROM course WHERE id = ?";
    private final String DELETE_STUDENTS = "DELETE FROM student_course WHERE course_id = ?";

    private final String GET_BY_TITLE = "SELECT c.id, title, teacher_id, t.firstname as teacher_firstname, t.lastname as teacher_lastname, t.faculty, " +
            "sc.student_id, s.firstname as student_firstname, s.lastname as student_lastname, s.age FROM course c " +
            "LEFT JOIN teacher t ON c.teacher_id = t.id " +
            "LEFT JOIN student_course sc ON c.id = sc.course_id LEFT JOIN student s ON sc.student_id = s.id WHERE title = ?";
    private final String CLEAR_STUDENT = "DELETE FROM student_course WHERE student_id = ? AND course_id = ?";

    private final DataSource dataSource;


    public CourseDaoJDBC(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    @Override
    public Optional<Course> findByTitle(String title) throws SQLException {
        Course course = null;

        try (Connection connection = dataSource.getConnection()) {


        try(PreparedStatement preparedStatement = connection.prepareStatement(GET_BY_TITLE)) {
            preparedStatement.setString(1, title);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                course = new Course();
                Teacher teacher = new Teacher();
                Set<Student> students = new HashSet<>();

                while (resultSet.next()) {
                    course.setTeacher(teacher);
                    course.setStudents(students);

                    if (course.getId() == null) {
                        course.setId(resultSet.getLong("id"));
                    }

                    if (course.getTitle() == null) {
                        course.setTitle(resultSet.getString("title"));
                    }

                    if (course.getTeacher().getId() == null) {
                        teacher.setId(resultSet.getLong("teacher_id"));
                        teacher.setFirstname(resultSet.getString("teacher_firstname"));
                        teacher.setLastname(resultSet.getString("teacher_lastname"));
                        teacher.setFaculty(resultSet.getString("faculty"));
                    }
                    Student student = new Student();
                    student.setId(resultSet.getLong("student_id"));
                    student.setFirstname(resultSet.getString("student_firstname"));
                    student.setLastname(resultSet.getString("student_lastname"));
                    student.setAge(resultSet.getInt("age"));
                    students.add(student);
                }
            }
        }
        }

        return Optional.ofNullable(course);
    }

    @Override
    public Course save(Course course) throws SQLException {

        try (Connection connection = dataSource.getConnection()) {

            if(course.getId() == null) {
                try(PreparedStatement insertStatement = connection.prepareStatement(INSERT)) {
                    insertStatement.setString(1, course.getTitle());
                    insertStatement.setLong(2, course.getTeacher().getId());
                    insertStatement.execute();
                }

                Set<Student> students = course.getStudents();

                    if(students != null && students.size() > 0) {
                        Course courseFromDb = findByTitle(course.getTitle()).get();

                        for(Student student : students) {
                            try(PreparedStatement insertStudentStatement = connection.prepareStatement(INSERT_STUDENT)) {
                                insertStudentStatement.setLong(1, student.getId());
                                insertStudentStatement.setLong(2, courseFromDb.getId());
                                insertStudentStatement.execute();
                            }
                        }
                    }

            } else {
                try (PreparedStatement updateStatement = connection.prepareStatement(UPDATE)) {
                    updateStatement.setString(1, course.getTitle());
                    updateStatement.setLong(2, course.getTeacher().getId());
                    updateStatement.setLong(3, course.getId());
                    updateStatement.execute();
                }
                Set<Student> students = course.getStudents();

                if (students != null && students.size() > 0) {
                    Course courseFromDb = findById(course.getId()).get();
                    Set<Student> studentsFromDb = courseFromDb.getStudents();

                    for(Student student : students) {
                        if(!studentsFromDb.contains(student)) {
                            try(PreparedStatement insertStudentStatement = connection.prepareStatement(INSERT_STUDENT)) {
                                insertStudentStatement.setLong(1, student.getId());
                                insertStudentStatement.setLong(2, course.getId());
                                insertStudentStatement.execute();
                            }
                        }
                    }

                    if(studentsFromDb.size() > 0) {
                        for(Student studentFromDb : studentsFromDb) {
                            if(studentFromDb != null && !students.contains(studentFromDb)) {
                                try(PreparedStatement deleteStudentStatement = connection.prepareStatement(CLEAR_STUDENT)) {
                                    deleteStudentStatement.setLong(1, studentFromDb.getId());
                                    deleteStudentStatement.setLong(2, course.getId());
                                    deleteStudentStatement.execute();
                                }
                            }

                        }
                    }

                } else {
                    try (PreparedStatement deleteStudentStatement = connection.prepareStatement(DELETE_STUDENTS)) {
                        deleteStudentStatement.setLong(1, course.getId());
                        deleteStudentStatement.execute();
                    }
                }
            }
        }
        return course;
    }

    @Override
    public Optional<Course> findById(Long id) throws SQLException {
        Course course = null;

        try (Connection connection = dataSource.getConnection()) {

        try(PreparedStatement preparedStatement = connection.prepareStatement(GET)) {
            preparedStatement.setLong(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {

                course = new Course();
                Teacher teacher = new Teacher();
                Set<Student> students = new HashSet<>();

                while (resultSet.next()) {
                    course.setTeacher(teacher);
                    course.setStudents(students);

                    if (course.getId() == null) {
                        course.setId(resultSet.getLong("id"));
                    }

                    if (course.getTitle() == null) {
                        course.setTitle(resultSet.getString("title"));
                    }

                    if (course.getTeacher().getId() == null) {
                        teacher.setId(resultSet.getLong("teacher_id"));
                        teacher.setFirstname(resultSet.getString("teacher_firstname"));
                        teacher.setLastname(resultSet.getString("teacher_lastname"));
                        teacher.setFaculty(resultSet.getString("faculty"));
                    }
                    Student student = new Student();
                    student.setId(resultSet.getLong("student_id"));
                    student.setFirstname(resultSet.getString("student_firstname"));
                    student.setLastname(resultSet.getString("student_lastname"));
                    student.setAge(resultSet.getInt("age"));
                    students.add(student);
                }
            }
        }
        }
        return Optional.ofNullable(course);
    }

    @Override
    public List<Course> findAll() throws SQLException {
       List<Course> courses = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {

            try(PreparedStatement preparedStatement = connection.prepareStatement(GET_ALL)) {

                try (ResultSet resultSet = preparedStatement.executeQuery()) {

                    Map<Long, Course> courseMap = new HashMap<>();

                    while (resultSet.next()) {
                        Long id = resultSet.getLong("id");
                        if (courseMap.containsKey(id)) {
                            Course course = courseMap.get(id);
                            Student student = new Student();
                            student.setId(resultSet.getLong("student_id"));
                            student.setFirstname(resultSet.getString("student_firstname"));
                            student.setLastname(resultSet.getString("student_lastname"));
                            student.setAge(resultSet.getInt("age"));
                            course.getStudents().add(student);
                        } else {
                            Course course = new Course();
                            course.setId(id);
                            course.setTitle(resultSet.getString("title"));
                            Teacher teacher = new Teacher();
                            teacher.setId(resultSet.getLong("teacher_id"));
                            teacher.setFirstname(resultSet.getString("teacher_firstname"));
                            teacher.setLastname(resultSet.getString("teacher_lastname"));
                            teacher.setFaculty(resultSet.getString("faculty"));
                            course.setTeacher(teacher);
                            Set<Student> students = new HashSet<>();
                            Student student = new Student();
                            student.setId(resultSet.getLong("student_id"));
                            student.setFirstname(resultSet.getString("student_firstname"));
                            student.setLastname(resultSet.getString("student_lastname"));
                            student.setAge(resultSet.getInt("age"));
                            students.add(student);
                            course.setStudents(students);
                            courseMap.put(id, course);
                        }
                    }
                    courses = courseMap.values().stream().collect(Collectors.toList());
                }
            }
        }
        return courses;
    }

    @Override
    public void deleteById(Long id) throws SQLException {

        try (Connection connection = dataSource.getConnection()) {

            try (PreparedStatement deleteStudentStatement = connection.prepareStatement(DELETE_STUDENTS)) {
                deleteStudentStatement.setLong(1, id);
                deleteStudentStatement.execute();
            }
            try(PreparedStatement deleteStatement = connection.prepareStatement(DELETE)) {
                deleteStatement.setLong(1, id);
                deleteStatement.execute();
        }
        }
    }

}


