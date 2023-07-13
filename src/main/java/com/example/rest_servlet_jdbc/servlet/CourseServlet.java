package com.example.rest_servlet_jdbc.servlet;

import com.example.rest_servlet_jdbc.service.CourseService;
import com.example.rest_servlet_jdbc.util.ResponseMessage;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.stream.Collectors;


@WebServlet(urlPatterns = "/course/*")
public class CourseServlet extends HttpServlet {

    private CourseService courseService;

    @Override
    public void init() throws ServletException {
        final Object courseService = getServletContext().getAttribute("courseService");
        this.courseService = (CourseService) courseService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String requestPath = req.getPathInfo();
            String getResponse = courseService.handleGetRequest(requestPath).get();
            resp.setContentType("application/json; charset=UTF-8");
            resp.setStatus(200);
            PrintWriter printWriter = resp.getWriter();
            printWriter.write(getResponse);
        } catch (SQLException e) {
            e.printStackTrace();
            resp.setStatus(404);
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write(ResponseMessage.GET_ERROR.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String bodyParams = req.getReader().lines().collect(Collectors.joining());
            courseService.handlePostRequest(bodyParams);
            resp.setStatus(201);
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write(ResponseMessage.POST_SUCCESS.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
            resp.setStatus(400);
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write(ResponseMessage.POST_ERROR.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String requestPath = req.getPathInfo();
            String bodyParams = req.getReader().lines().collect(Collectors.joining());
            courseService.handlePutRequest(requestPath, bodyParams);
            resp.setStatus(200);
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write(ResponseMessage.PUT_SUCCESS.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
            resp.setStatus(400);
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write(ResponseMessage.PUT_ERROR.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String requestPath = req.getPathInfo();
            courseService.handleDeleteRequest(requestPath);
            resp.setStatus(200);
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write(ResponseMessage.DELETE_SUCCESS.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
            resp.setStatus(400);
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write(ResponseMessage.DELETE_ERROR.getMessage());
        }
    }

}
