package ru.viewer.expensesviewer.model;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MainModel {
    private final Logger LOGGER = LogManager.getLogger(MainModel.class);
    private final Connection connection = DbConnection.getInstance().getConnection();

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

    public Map<Integer, String> getWalletList() {
        try (Statement statement = connection.createStatement()) {

            String qGetWalletList = "SELECT `wallet_id`, `wallet_name` FROM `wallets_list`;";
            ResultSet resultSet = statement.executeQuery(qGetWalletList);
            Map<Integer, String> walletList = new HashMap<>();

            while (resultSet.next()) {
                walletList.put(resultSet.getInt("wallet_id"), resultSet.getString("wallet_name"));
            }
            return walletList;
        } catch (SQLException e) {
            LOGGER.error("Create statement failed during get wallet list");
            throw new RuntimeException(e);
        }
    }
    public double getWalletBalanceById(int id) {
        try (Statement statement = connection.createStatement()){
            String query = "SELECT `wallet_balance` FROM `wallets_list` WHERE wallet_id = " + id + ";";
            ResultSet resultSet = statement.executeQuery(query);
            double currentWalletBalance = 0;
            if (resultSet.next()) currentWalletBalance = resultSet.getDouble("wallet_balance");
            return currentWalletBalance;
        } catch (SQLException e) {
            LOGGER.error("Create statement failed during get wallet wallet balance by ID");
            throw new RuntimeException(e);
        }
    }

    public int updateWalletBalanceById(int id, double newValue) {
        try (PreparedStatement preparedStatementUpdateWalletBalance =
                     connection.prepareStatement("UPDATE `wallets_list` SET `wallet_balance` = ? WHERE `wallet_id` = ?;")) {
            preparedStatementUpdateWalletBalance.setDouble(1, newValue);
            preparedStatementUpdateWalletBalance.setInt(2, id);
            return preparedStatementUpdateWalletBalance.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
