package dao;


import entity.Teacher;
import java.util.List;
import java.util.Optional;

public interface TeacherDao {

    Teacher save(Teacher teacher);

    Optional<Teacher> findById(Long id);

    List<Teacher> findAll();

    void deleteById(Long id);

}
