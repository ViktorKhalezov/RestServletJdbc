package com.example.rest_servlet_jdbc.servlet;


import com.example.rest_servlet_jdbc.service.StudentService;
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

@WebServlet(urlPatterns = "/student/*")
public class StudentServlet extends HttpServlet {

    private StudentService studentService;

    @Override
    public void init() throws ServletException {
        final Object studentService = getServletContext().getAttribute("studentService");
        this.studentService = (StudentService) studentService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String requestPath = req.getPathInfo();
            String getResponse = studentService.handleGetRequest(requestPath).get();
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
            studentService.handlePostRequest(bodyParams);
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
            studentService.handlePutRequest(requestPath, bodyParams);
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
            studentService.handleDeleteRequest(requestPath);
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
