package com.example.rest_servlet_jdbc.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BasicConnectionPool implements ConnectionPool {

    private final static String URL = "jdbc:postgresql://localhost:5432/rest_servlet_jdbc";

    private final static String USER = "geek";

    private final static String PASSWORD = "geek";

    private static final int INITIAL_POOL_SIZE = 10;
    private static final List<Connection> connectionPool = new ArrayList<>(INITIAL_POOL_SIZE);
    private static final List<Connection> usedConnections = new ArrayList<>();
    private final static ConnectionPool basicConnectionPool = new BasicConnectionPool();


    static {
        for (int i = 0; i < INITIAL_POOL_SIZE; i++) {
            try {
                Class.forName("org.postgresql.Driver");
                connectionPool.add(DriverManager.getConnection(URL, USER, PASSWORD));
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private BasicConnectionPool() {

    }

    public static ConnectionPool getConnectionPool() {
        return basicConnectionPool;
    }


    @Override
    public Connection getConnection() {
        Connection connection = connectionPool.remove(connectionPool.size() - 1);
        usedConnections.add(connection);
        return connection;
    }

    @Override
    public boolean releaseConnection(Connection connection) {
        connectionPool.add(connection);
        return usedConnections.remove(connection);
    }

}
