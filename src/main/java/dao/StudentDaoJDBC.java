package dao;

import entity.Course;
import entity.Student;
import entity.Teacher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class StudentDaoJDBC implements StudentDao {

    private final String GET = "SELECT s.id, s.firstname, s.lastname, age, sc.course_id, c.title as course_title, c.teacher_id as course_teacher_id, " +
            "t.firstname as course_teacher_firstname, t.lastname as course_teacher_lastname, t.faculty as course_teacher_faculty FROM student s " +
            "INNER JOIN student_course sc ON s.id = sc.student_id LEFT JOIN course c ON sc.course_id = c.id" +
            "LEFT JOIN teacher t ON c.teacher_id = t.id WHERE s.id = ?";

    private final String GET_ALL = "SELECT s.id, s.firstname, s.lastname, age, sc.course_id, c.title as course_title, c.teacher_id as course_teacher_id, " +
            "t.firstname as course_teacher_firstname, t.lastname as course_teacher_lastname, t.faculty as course_teacher_faculty FROM student s " +
            "INNER JOIN student_course sc ON s.id = sc.student_id LEFT JOIN course c ON sc.course_id = c.id LEFT JOIN teacher t ON c.teacher_id = t.id";

    private final String INSERT = "INSERT INTO student (firstname, lastname, age) VALUES(?, ?, ?)";
    private final String INSERT_COURSES = "INSERT INTO student_course (student_id, course_id) VALUES(?, ?)";
    private final String UPDATE = "UPDATE student SET firstname = ?, lastname = ?, age = ? WHERE id = ?";
//    private final String UPDATE_COURSES = "UPDATE student_course SET course_id = ? WHERE student_id = ?";
    private final String DELETE = "DELETE FROM student WHERE id = ?";
    private final String DELETE_COURSES = "DELETE FROM student_course WHERE student_id = ?";

    @Override
    public Student save(Student student) {
        try (Connection connection = DataSource.getConnection()) {
            if (student.getId() == null) {
                try (PreparedStatement insertStatement = connection.prepareStatement(INSERT)) {
                    insertStatement.setString(1, student.getFirstname());
                    insertStatement.setString(2, student.getLastname());
                    insertStatement.setInt(3, student.getAge());
                    insertStatement.execute();
                    Set<Course> courses = student.getCourses();
                    if (courses != null && courses.size() > 0) {
                        for (Course course : courses) {
                            try(PreparedStatement insertCourseStatement = connection.prepareStatement(INSERT_COURSES)) {
                                insertCourseStatement.setLong(1, student.getId());
                                insertCourseStatement.setLong(2, course.getId());
                                insertCourseStatement.execute();
                            }
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
                    Set<Course> courses = student.getCourses();

                    try (PreparedStatement deleteCourseStatement = connection.prepareStatement(DELETE_COURSES)) {
                            deleteCourseStatement.setLong(1, student.getId());
                            deleteCourseStatement.execute();
                    }

                    if(courses != null && courses.size() > 0) {
                        for (Course course : courses) {
                            try (PreparedStatement updateCourseStatement = connection.prepareStatement(INSERT_COURSES)) {
                                    updateCourseStatement.setLong(1, student.getId());
                                    updateCourseStatement.setLong(2, course.getId());
                                    updateCourseStatement.execute();
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return student;
    }

    @Override
    public Optional<Student> findById(Long id) {
        Student student = null;
        try(Connection connection = DataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(GET)) {
                preparedStatement.setLong(1, id);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    student = new Student();
                    Set<Course> courses = new HashSet<>();

                    while (resultSet.next()) {
                        student.setCourses(courses);

                        if(student.getId() == null) {
                            student.setId(resultSet.getLong("s.id"));
                        }
                        if(student.getFirstname() == null) {
                            student.setFirstname(resultSet.getString("s.firstname"));
                        }
                        if(student.getLastname() == null) {
                            student.setLastname(resultSet.getString("s.lastname"));
                        }
                        Course course = new Course();
                        course.setId(resultSet.getLong("sc.course_id"));
                        course.setTitle(resultSet.getString(resultSet.getString("course_title")));
                        Teacher teacher = new Teacher();
                        teacher.setId(resultSet.getLong("course_teacher_id"));
                        teacher.setFirstname(resultSet.getString("course_teacher_firstname"));
                        teacher.setLastname(resultSet.getString("course_teacher_lastname"));
                        teacher.setFirstname(resultSet.getString("course_teacher_faculty"));
                        course.setTeacher(teacher);
                        courses.add(course);
                    }
                }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(student);
    }

    @Override
    public List<Student> findAll() {
        List<Student> students = new ArrayList<>();

        try (Connection connection = DataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(GET_ALL)) {

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                Student student = null;
                Set<Course> courses = null;

                while (resultSet.next()) {
                    if(student == null || student.getId() == null) {
                        student = new Student();
                        courses = new HashSet<>();
                        student.setId(resultSet.getLong("s.id"));
                        student.setCourses(courses);
                        students.add(student);
                    } else {
                        courses = student.getCourses();
                    }

                    if (student.getFirstname() == null) {
                        student.setFirstname(resultSet.getString("s.firstname"));
                    }

                    if (student.getLastname() == null) {
                        student.setLastname(resultSet.getString("s.lastname"));
                    }

                    if (student.getAge() == null) {
                        student.setAge(resultSet.getInt("age"));
                    }

                    Course course = new Course();
                    course.setId(resultSet.getLong("sc.course_id"));
                    course.setTitle(resultSet.getString(resultSet.getString("course_title")));
                    Teacher teacher = new Teacher();
                    teacher.setId(resultSet.getLong("course_teacher_id"));
                    teacher.setFirstname(resultSet.getString("course_teacher_firstname"));
                    teacher.setLastname(resultSet.getString("course_teacher_lastname"));
                    teacher.setFirstname(resultSet.getString("course_teacher_faculty"));
                    course.setTeacher(teacher);
                    courses.add(course);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }

    @Override
    public void deleteById(Long id) {
        try (Connection connection = DataSource.getConnection();
            PreparedStatement deleteStatement = connection.prepareStatement(DELETE)) {
            deleteStatement.setLong(1, id);
            deleteStatement.execute();
            try (PreparedStatement deleteCourseStatement = connection.prepareStatement(DELETE_COURSES)) {
                deleteCourseStatement.setLong(1, id);
                deleteCourseStatement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}


