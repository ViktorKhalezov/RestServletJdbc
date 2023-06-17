package dao;

import entity.Student;
import java.util.List;
import java.util.Optional;

public interface StudentDao {

    Student save(Student student);

    Optional<Student> findById(Long id);

    List<Student> findAll();

    void deleteById(Long id);

}
