package ru.viewer.expensesviewer.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

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
            String query = "SELECT TRUNCATE(wallet_balance,2) AS wallet_balance FROM wallets_list WHERE wallet_default = 1;";
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

            String qGetWalletList = "SELECT `wallet_id`, `wallet_name` FROM `wallets_list` ORDER BY `wallet_name` ASC;";
            ResultSet resultSet = statement.executeQuery(qGetWalletList);
            Map<Integer, String> walletList = new LinkedHashMap<>();

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
            String query = "SELECT TRUNCATE(`wallet_balance`, 2) AS wallet_balance FROM `wallets_list` WHERE wallet_id = " + id + ";";
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
            LOGGER.error(e.getMessage());
            throw new RuntimeException(e);
        }

    }

    public Map<Integer, String> getIncomeCategoryList() {
        try (Statement statement = connection.createStatement()) {
            String qGetIncomeList = "SELECT `income_category_id`, `income_category_name` FROM `income_category` ORDER BY `income_category_name` ASC;";
            ResultSet resultSet = statement.executeQuery(qGetIncomeList);
            Map<Integer, String> incomeCategoryList = new LinkedHashMap<>();

            while (resultSet.next()) {
                incomeCategoryList.put(resultSet.getInt("income_category_id"), resultSet.getString("income_category_name"));
            }
            return incomeCategoryList;
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public String getDefaultIncomeCategory() {
        try (Statement statement = connection.createStatement()) {
            String getValue = "SELECT `income_category_name` FROM `income_category` WHERE `income_default` = true;";
            ResultSet resultSet = statement.executeQuery(getValue);
            if (resultSet.next()) {
                return resultSet.getString("income_category_name");
            } else {
                return "";
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public Map<Integer, String> getExpensesCategoryList() {
        try (Statement statement = connection.createStatement()) {
            String qGetExpensesList = "SELECT `expenses_category_id`, `expenses_category_name` FROM `expenses_category` ORDER BY `expenses_category_name` ASC;";
            ResultSet resultSet = statement.executeQuery(qGetExpensesList);
            Map<Integer, String> expensesCategoryList = new LinkedHashMap<>();

            while (resultSet.next()) {
                expensesCategoryList.put(resultSet.getInt("expenses_category_id"), resultSet.getString("expenses_category_name"));
            }
            return expensesCategoryList;
        } catch (SQLException e) {
            LOGGER.debug("Can't get data from DB about Expenses Category list.");
            throw new RuntimeException(e);
        }
    }

    public String getDefaultExpensesCategory() {
        try (Statement statement = connection.createStatement()) {
            String getValue = "SELECT `expenses_category_name` FROM `expenses_category` WHERE `expenses_default` = true;";
            ResultSet resultSet = statement.executeQuery(getValue);
            if (resultSet.next()) {
                return resultSet.getString("expenses_category_name");
            } else {
                return "";
            }
        } catch (SQLException e) {
            LOGGER.debug("Can't get data from DB about Expenses category default value.");
            throw new RuntimeException(e);
        }
    }
}
