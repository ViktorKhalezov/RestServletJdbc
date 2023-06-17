package dao;

import entity.Course;
import entity.Student;
import entity.Teacher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class CourseDaoJDBC implements CourseDao {

//    private final DataSource dataSource;
//    private Connection connection;

//    private final String GET = "SELECT c.id, title, teacher_id, t.firstname as teacher_firstname, t.lastname as teacher_lastname, t.faculty as teacher_faculty FROM course c LEFT JOIN teacher t ON c.teacher_id = t.id WHERE c.id = ?";
//    private final String GET_ALL = "SELECT c.id, title, teacher_id, t.firstname as teacher_firstname, t.lastname as teacher_lastname, t.faculty as teacher_faculty FROM course c LEFT JOIN teacher t ON c.teacher_id = t.id";

    private final String GET = "SELECT c.id, title, teacher_id, t.firstname as teacher_firstname, t.lastname as teacher_lastname, t.faculty, " +
            "sc.student_id, s.firstname as student_firstname, s.lastname as student_lastname, s.age FROM course c " +
            "LEFT JOIN teacher t ON c.teacher_id = t.id " +
            "INNER JOIN student_course sc ON c.id = sc.course_id LEFT JOIN student s ON sc.student_id = s.id WHERE c.id = ?";

        private final String GET_ALL = "SELECT c.id, title, teacher_id, t.firstname as teacher_firstname, t.lastname as teacher_lastname, t.faculty, " +
            "sc.student_id, s.firstname as student_firstname, s.lastname as student_lastname, s.age FROM course c " +
            "LEFT JOIN teacher t ON c.teacher_id = t.id " +
            "INNER JOIN student_course sc ON c.id = sc.course_id LEFT JOIN student s ON sc.student_id = s.id";

    private final String INSERT = "INSERT INTO course (title, teacher_id) VALUES (?, ?)";
    private final String INSERT_STUDENTS = "INSERT INTO student_course (student_id, course_id) VALUES (?, ?)";
    private final String UPDATE = "UPDATE course SET title = ?, teacher_id = ? WHERE id = ?";
//    private final String UPDATE_STUDENTS = "UPDATE student_course SET student_id = ? WHERE course_id = ?";
    private final String DELETE = "DELETE FROM course WHERE id = ?";
    private final String DELETE_STUDENTS = "DELETE FROM student_course WHERE course_id = ?";




    @Override
    public Course save(Course course) {
        try(Connection connection = DataSource.getConnection()) {
            if(course.getId() == null) {
                try(PreparedStatement insertStatement = connection.prepareStatement(INSERT)) {
                    insertStatement.setString(1, course.getTitle());
                    insertStatement.setLong(2, course.getTeacher().getId());
                    insertStatement.execute();
                    Set<Student> students = course.getStudents();
                    if(students != null && students.size() > 0) {
                        for(Student student : students) {
                            try(PreparedStatement insertStudentStatement = connection.prepareStatement(INSERT_STUDENTS)) {
                                insertStudentStatement.setLong(1, student.getId());
                                insertStudentStatement.setLong(2, course.getId());
                                insertStudentStatement.execute();
                            }
                        }
                    }
                }
            } else {
                try (PreparedStatement updateStatement = connection.prepareStatement(UPDATE)) {
                    updateStatement.setString(1, course.getTitle());
                    updateStatement.setLong(2, course.getTeacher().getId());
                    updateStatement.setLong(3, course.getId());
                    updateStatement.execute();
                    Set<Student> students = course.getStudents();

                    try(PreparedStatement deleteStudentStatement = connection.prepareStatement(DELETE_STUDENTS)) {
                        deleteStudentStatement.setLong(1, course.getId());
                        deleteStudentStatement.execute();

                        if (students != null && students.size() > 0) {
                            for (Student student : students) {
                                try (PreparedStatement updateStudentStatement = connection.prepareStatement(INSERT_STUDENTS)) {
                                    updateStudentStatement.setLong(1, student.getId());
                                    updateStudentStatement.setLong(2, course.getId());
                                    updateStudentStatement.execute();
                                }
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return course;
    }

    @Override
    public Optional<Course> findById(Long id) {
        Course course = null;
        try(Connection connection = DataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(GET)) {
            preparedStatement.setLong(1, id);

            try(ResultSet resultSet = preparedStatement.executeQuery()) {

                course = new Course();
                Teacher teacher = new Teacher();
                Set<Student> students = new HashSet<>();

                while (resultSet.next()) {
                    course.setTeacher(teacher);
                    course.setStudents(students);

                    if(course.getId() == null) {
                        course.setId(resultSet.getLong("c.id"));
                    }

                    if(course.getTitle() == null) {
                        course.setTitle(resultSet.getString("title"));
                    }

                    if(course.getTeacher().getId() == null) {
                        teacher.setId(resultSet.getLong("teacher_id"));
                        teacher.setFirstname(resultSet.getString("teacher_firstname"));
                        teacher.setLastname(resultSet.getString("teacher_lastname"));
                        teacher.setFaculty(resultSet.getString("t.faculty"));
                    }
                    Student student = new Student();
                    student.setId(resultSet.getLong("sc.student_id"));
                    student.setFirstname(resultSet.getString("student_firstname"));
                    student.setLastname(resultSet.getString("student_lastname"));
                    student.setAge(resultSet.getInt("s.age"));
                    students.add(student);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(course);
    }

    @Override
    public List<Course> findAll() {
       List<Course> courses = new ArrayList<>();

        try(Connection connection = DataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(GET_ALL)) {

            try(ResultSet resultSet = preparedStatement.executeQuery()) {

                Course course = null;
                Teacher teacher = null;
                Set<Student> students = null;

                while (resultSet.next()) {

                    if(course == null || course.getId() == null) {
                        course = new Course();
                        teacher = new Teacher();
                        students = new HashSet<>();
                        course.setId(resultSet.getLong("c.id"));
                        course.setTeacher(teacher);
                        course.setStudents(students);
                        courses.add(course);
                    } else {
                        teacher = course.getTeacher();
                        students = course.getStudents();
                    }

                    if(course.getTitle() == null) {
                        course.setTitle(resultSet.getString("title"));
                    }

                    if(course.getTeacher().getId() == null) {
                        teacher.setId(resultSet.getLong("teacher_id"));
                        teacher.setFirstname(resultSet.getString("teacher_firstname"));
                        teacher.setLastname(resultSet.getString("teacher_lastname"));
                        teacher.setFaculty(resultSet.getString("t.faculty"));
                    }
                    Student student = new Student();
                    student.setId(resultSet.getLong("sc.student_id"));
                    student.setFirstname(resultSet.getString("student_firstname"));
                    student.setLastname(resultSet.getString("student_lastname"));
                    student.setAge(resultSet.getInt("s.age"));
                    students.add(student);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }

    @Override
    public void deleteById(Long id) {
        try(Connection connection = DataSource.getConnection();
            PreparedStatement deleteStatement = connection.prepareStatement(DELETE)) {
            deleteStatement.setLong(1, id);
            deleteStatement.execute();
            try(PreparedStatement deleteStudentStatement = connection.prepareStatement(DELETE_STUDENTS)) {
                deleteStudentStatement.setLong(1, id);
                deleteStudentStatement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}


// "SELECT * FROM GOODS where id > ? and id < ?"
//        "INSERT INTO GOODS(name) values(?)"

//    INSERT_GOAL("INSERT INTO goals (name, description) VALUES ((?), (?))"),
//    GET_GOAL_BY_ID("SELECT  goals.id, goals.name , goals.description from goals where id = (?)"),
//    DELETE_GOAL_BY_ID("DELETE from goals where id=(?)"),
//    UPDATE_GOAL_BY_ID("UPDATE goals set name=(?), description=(?) where id=(?)"),
//    GET_ALL_GOALS("SELECT goals.id, goals.name , goals.description from goals");


//    Goal goal = null;
//        sessionManager.beginSession();
//                try (Connection connection = sessionManager.getCurrentSession();
//                PreparedStatement pst = connection.prepareStatement(SQLTask.GET_GOAL_BY_ID.QUERY)) {
//                pst.setLong(1, id);
//
//                try (ResultSet rs = pst.executeQuery()) {
//                if (rs.next()) {
//                goal = parseGoalFromResultSet(rs);
//                }
//                }
//                } catch (SQLException ex) {
//                log.error(ex.getMessage(), ex);
//                sessionManager.rollbackSession();
//                throw ex;
//                }
//
//                return Optional.ofNullable(goal);


//    private Goal parseGoalFromResultSet(ResultSet rs) throws SQLException {
//        Goal goal = new Goal();
//
//        goal.setId(rs.getInt("id"));
//        goal.setName(rs.getString("name"));
//        goal.setDescription(rs.getString("description"));
//
//        return goal;
//    }


//    @Override
//    public int deleteGoal(Long goal_id) throws SQLException {
//        int updated_rows;
//
//        sessionManager.beginSession();
//        try (Connection connection = sessionManager.getCurrentSession();
//             PreparedStatement pst = connection.prepareStatement(SQLTask.DELETE_GOAL_BY_ID.QUERY)) {
//            pst.setLong(1, goal_id);
//            updated_rows = pst.executeUpdate();
//            sessionManager.commitSession();
//
//        } catch (SQLException ex) {
//            log.error(ex.getMessage(), ex);
//            sessionManager.rollbackSession();
//            throw ex;
//        }
//        return updated_rows;


//    @Override
//    public long insertGoal(final Goal goal) throws SQLException {
//        sessionManager.beginSession();
//
//        try (Connection connection = sessionManager.getCurrentSession();
//             PreparedStatement pst = connection.prepareStatement(SQLTask.INSERT_GOAL.QUERY, Statement.RETURN_GENERATED_KEYS)) {
//            pst.setString(1, goal.getName());
//            pst.setString(2, goal.getDescription());
//
//            pst.executeUpdate();
//            try (ResultSet rs = pst.getGeneratedKeys()) {
//                rs.next();
//                long id = rs.getLong(1);
//                sessionManager.commitSession();
//
//                return id;
//            }
//        } catch (SQLException ex) {
//            log.error(ex.getMessage(), ex);
//            sessionManager.rollbackSession();
//            throw ex;
//        }

