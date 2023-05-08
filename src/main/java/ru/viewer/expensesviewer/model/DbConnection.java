package ru.viewer.expensesviewer.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;

public class DbConnection {
    @SuppressWarnings("FieldCanBeLocal")
    private final Logger LOGGER = LogManager.getLogger(MainModel.class);
    private static DbConnection instance;
    private final Connection connection;

    private static final String URL = "jdbc:mysql://localhost:3306/simpleexpensesmanager";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "12345678";

    private DbConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            LOGGER.fatal("MainModel connection to DB was failed!");
            throw new RuntimeException(e);
        }
    }

    public static DbConnection getInstance() {
        if (instance == null) instance = new DbConnection();
        return instance;
    }

    public Connection getConnection() {
        return instance.connection;
    }
}
