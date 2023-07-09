package com.example.rest_servlet_jdbc.servlet;

import com.example.rest_servlet_jdbc.dao.*;
import com.example.rest_servlet_jdbc.mapper.CourseMapper;
import com.example.rest_servlet_jdbc.mapper.StudentMapper;
import com.example.rest_servlet_jdbc.mapper.TeacherMapper;
import com.example.rest_servlet_jdbc.service.CourseService;
import com.example.rest_servlet_jdbc.service.StudentService;
import com.example.rest_servlet_jdbc.service.TeacherService;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.sql.DataSource;


@WebListener
public class ContextListener implements ServletContextListener {


    private CourseDao courseDao;
    private StudentDao studentDao;
    private TeacherDao teacherDao;
    private CourseMapper courseMapper;
    private StudentMapper studentMapper;
    private TeacherMapper teacherMapper;
    private CourseService courseService;
    private StudentService studentService;
    private TeacherService teacherService;



    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        final ServletContext servletContext = servletContextEvent.getServletContext();

        DataSource dataSource = DBCPDataSource.getDataSource();
        courseDao = new CourseDaoJDBC(dataSource);
        studentDao = new StudentDaoJDBC(dataSource);
        teacherDao = new TeacherDaoJDBC(dataSource);
        courseMapper = new CourseMapper(teacherDao, studentDao);
        studentMapper = new StudentMapper(courseDao);
        teacherMapper = new TeacherMapper(courseDao);
        courseService = new CourseService(courseDao, courseMapper);
        studentService = new StudentService(studentDao, studentMapper);
        teacherService = new TeacherService(teacherDao, teacherMapper);


        servletContext.setAttribute("courseService", courseService);
        servletContext.setAttribute("studentService", studentService);
        servletContext.setAttribute("teacherService", teacherService);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }

}
