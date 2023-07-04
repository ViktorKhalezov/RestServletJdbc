package com.example.rest_servlet_jdbc.dao;

import java.sql.Connection;

public interface ConnectionPool  {

    Connection getConnection();
    boolean releaseConnection(Connection connection);

}
