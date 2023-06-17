package dao;

import entity.Course;
import entity.Teacher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class TeacherDaoJDBC implements TeacherDao {

    private final String GET = "SELECT t.id, firstname, lastname, faculty, c.id as course_id, c.title as course_title FROM teacher t " +
            "LEFT JOIN course c ON t.id = c.teacher_id WHERE t.id = ?";
    private final String GET_ALL = "SELECT t.id, firstname, lastname, faculty, c.id as course_id, c.title as course_title FROM teacher t " +
            "LEFT JOIN course c ON t.id = c.teacher_id";
    private final String INSERT = "INSERT INTO teacher (firstname, lastname, faculty) VALUES(?, ?, ?)";
    private final String INSERT_COURSES = "INSERT INTO course (title, teacher_id) VALUES (?, ?)";
    private final String UPDATE = "UPDATE teacher firstname = ?, lastname = ?, faculty = ? WHERE id = ?";
    private final String DELETE = "DELETE FROM teacher WHERE id = ?";
    private final String DELETE_COURSES = "DELETE FROM course WHERE teacher_id = ?";


    @Override
    public Teacher save(Teacher teacher) {
        try (Connection connection = DataSource.getConnection()) {
            if (teacher.getId() == null) {
                try (PreparedStatement insertStatement = connection.prepareStatement(INSERT)) {
                    insertStatement.setString(1, teacher.getFirstname());
                    insertStatement.setString(2, teacher.getLastname());
                    insertStatement.setString(3, teacher.getFaculty());
                    insertStatement.execute();
                    Set<Course> courses = teacher.getCourses();
                    if(courses != null || courses.size() > 0) {
                        for (Course course : courses) {
                            try (PreparedStatement insertCourseStatement = connection.prepareStatement(INSERT_COURSES)) {
                                insertCourseStatement.setString(1, course.getTitle());
                                insertCourseStatement.setLong(2, teacher.getId());
                                insertCourseStatement.execute();
                            }
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
                    Set<Course> courses = teacher.getCourses();

                    try (PreparedStatement deleteCourseStatement = connection.prepareStatement(DELETE_COURSES)) {
                        deleteCourseStatement.setLong(1, teacher.getId());
                        deleteCourseStatement.execute();
                    }

                    if (courses != null && courses.size() > 0) {
                        for (Course course : courses) {
                            try(PreparedStatement updateCourseStatement = connection.prepareStatement(INSERT_COURSES)) {
                                updateCourseStatement.setString(1, course.getTitle());
                                updateCourseStatement.setLong(2, teacher.getId());
                                updateCourseStatement.execute();
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return teacher;
    }

    @Override
    public Optional<Teacher> findById(Long id) {
        Teacher teacher = null;
        try (Connection connection = DataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(GET)) {
            preparedStatement.setLong(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {

                teacher = new Teacher();
                Set<Course> courses = new HashSet<>();

                while (resultSet.next()) {
                    teacher.setCourses(courses);

                    if(teacher.getId() == null) {
                        teacher.setId(resultSet.getLong("t.id"));
                    }

                    if(teacher.getFirstname() == null) {
                        teacher.setFirstname(resultSet.getString("firstname"));
                    }

                    if(teacher.getLastname() == null) {
                        teacher.setLastname(resultSet.getString("lastname"));
                    }

                    if(teacher.getFaculty() == null) {
                        teacher.setFaculty(resultSet.getString("faculty"));
                    }

                    Course course = new Course();
                    course.setId(resultSet.getLong("course_id"));
                    course.setTitle(resultSet.getString("course_title"));
                    courses.add(course);
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(teacher);
    }

    @Override
    public List<Teacher> findAll() {
        List<Teacher> teachers = new ArrayList<>();

        try (Connection connection = DataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(GET_ALL)) {

            try (ResultSet resultSet = preparedStatement.executeQuery()) {

                Teacher teacher = null;
                Set<Course> courses = null;

                while (resultSet.next()) {

                    if(teacher == null || teacher.getId() == null) {
                        teacher = new Teacher();
                        courses = new HashSet<>();
                        teacher.setId(resultSet.getLong("t.id"));
                        teacher.setCourses(courses);
                        teachers.add(teacher);
                    } else {
                        courses = teacher.getCourses();
                    }

                    if(teacher.getFirstname() == null) {
                        teacher.setFirstname(resultSet.getString("firstname"));
                    }

                    if(teacher.getLastname() == null) {
                        teacher.setLastname(resultSet.getString("lastname"));
                    }

                    if(teacher.getFaculty() == null) {
                        teacher.setFaculty(resultSet.getString("faculty"));
                    }
                    Course course = new Course();
                    course.setId(resultSet.getLong("course_id"));
                    course.setTitle(resultSet.getString("course_title"));
                    courses.add(course);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return teachers;
    }

    @Override
    public void deleteById(Long id) {
        try (Connection connection = DataSource.getConnection();
            PreparedStatement deleteStatement = connection.prepareStatement(DELETE)) {
            deleteStatement.setLong(1, id);
            deleteStatement.execute();
            try(PreparedStatement deleteCourseStatement = connection.prepareStatement(DELETE_COURSES)) {
                deleteCourseStatement.setLong(1, id);
                deleteCourseStatement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}

//    @Override
//    public void deleteById(Long id) {
//        try(Connection connection = DataSource.getConnection();
//            PreparedStatement deleteStatement = connection.prepareStatement(DELETE)) {
//            deleteStatement.setLong(1, id);
//            deleteStatement.execute();
//            try(PreparedStatement deleteStudentStatement = connection.prepareStatement(DELETE_STUDENTS)) {
//                deleteStudentStatement.setLong(1, id);
//                deleteStudentStatement.execute();
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }


