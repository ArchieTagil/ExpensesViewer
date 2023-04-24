package ru.viewer.expensesviewer.model;

import java.sql.*;

public class DbConnection {
    private static DbConnection instance;
    private final Connection connection;

    private static final String URL = "jdbc:mysql://localhost:3306/simpleexpensesmanager";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "12345678";

    private DbConnection() throws ClassNotFoundException, SQLException {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    public static DbConnection getInstance() throws SQLException, ClassNotFoundException {
        if (instance == null) instance = new DbConnection();
        return instance;
    }

    public Connection getConnection() {
        return instance.connection;
    }
}
