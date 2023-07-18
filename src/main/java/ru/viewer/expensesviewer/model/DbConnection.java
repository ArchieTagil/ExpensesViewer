package ru.viewer.expensesviewer.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DbConnection {
    private static final Properties config = new Properties();
    @SuppressWarnings("FieldCanBeLocal")
    private final Logger LOGGER = LogManager.getLogger(MainModel.class);
    private static DbConnection instance;
    private final Connection connection;

    private DbConnection() {
        try {
            //config.load(new FileInputStream("src/main/resources/ru/viewer/expensesviewer/config.properties"));
            config.load(new FileInputStream("./config.properties"));
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(config.getProperty("dbHost"));
        } catch (ClassNotFoundException | SQLException | IOException e) {
            LOGGER.fatal(e.getMessage());
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

    public static Properties getConfig() {
        return config;
    }
}
