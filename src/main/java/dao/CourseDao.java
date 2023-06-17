package dao;

import entity.Course;
import java.util.List;
import java.util.Optional;

public interface CourseDao {

    Course save(Course course);

    Optional<Course> findById(Long id);

    List<Course> findAll();

    void deleteById(Long id);

}
