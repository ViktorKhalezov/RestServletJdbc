package entity;

import java.util.Objects;
import java.util.Set;

public class Teacher extends Person {

    private String faculty;

    private Set<Course> courses;

    public Teacher(Long id, String firstname, String lastname, String faculty, Set<Course> courses) {
        super(id, firstname, lastname);
        this.faculty = faculty;
        this.courses = courses;
    }

    public Teacher() {

    }

    public String getFaculty() {
        return faculty;
    }

    public Set<Course> getCourses() {
        return courses;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public void setCourses(Set<Course> courses) {
        this.courses = courses;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Teacher teacher = (Teacher) o;
        return faculty.equals(teacher.faculty) && courses.equals(teacher.courses);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), faculty, courses);
    }
}
