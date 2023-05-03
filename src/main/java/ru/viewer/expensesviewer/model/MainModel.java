package ru.viewer.expensesviewer.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MainModel {
    private final Logger LOGGER = LogManager.getLogger(MainModel.class);
    private final Connection connection;
    {
        try {
            connection = DbConnection.getInstance().getConnection();
        } catch (SQLException | ClassNotFoundException e) {
            LOGGER.fatal("MainModel connection to DB was failed!");
            throw new RuntimeException(e);
        }
    }
    public String getDefaultWalletName() {
        try (Statement statement = connection.createStatement()) {
            String query = "SELECT `wallet_name` FROM wallets_list WHERE wallet_default = 1;";
            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) return resultSet.getString("wallet_name");
            else return null;
        } catch (SQLException e) {
            LOGGER.error("Create statement failed during get default wallet name");
            throw new RuntimeException(e);
        }
    }

    public double defaultWalletBalance() {
        try (Statement statement = connection.createStatement()) {
            String query = "SELECT `wallet_balance` FROM wallets_list WHERE wallet_default = 1;";
            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) return resultSet.getDouble("wallet_balance");
            return 0;
        } catch (SQLException e) {
            LOGGER.error("Create statement failed during get default wallet balance");
            throw new RuntimeException(e);
        }
    }
}
