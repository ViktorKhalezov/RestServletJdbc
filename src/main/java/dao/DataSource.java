package dao;

import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DataSource {

    private final static String URL = "jdbc:postgresql://localhost:5432/rest_servlet_jdbc";

    private final static String USER = "geek";

    private final static String PASSWORD = "geek";

    private static BasicDataSource dataSource = new BasicDataSource();

    static {
        dataSource.setUrl(URL);
        dataSource.setUsername(USER);
        dataSource.setPassword(PASSWORD);
        dataSource.setMinIdle(5);
        dataSource.setMaxIdle(10);
        dataSource.setMaxOpenPreparedStatements(100);
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    private DataSource() {

    }

}
