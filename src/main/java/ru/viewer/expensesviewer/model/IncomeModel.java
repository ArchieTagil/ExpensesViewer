package ru.viewer.expensesviewer.model;

import ru.viewer.expensesviewer.controller.IncomeController;
import ru.viewer.expensesviewer.model.objects.IncomeEntity;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class IncomeModel {
    private final Connection connection = DbConnection.getInstance().getConnection();
    private static final Logger LOGGER = LogManager.getLogger(IncomeController.class);

    public IncomeModel() throws SQLException, ClassNotFoundException {
    }

    public List<IncomeEntity> getIncomeList() throws SQLException {
        Statement statement = connection.createStatement();
        String queryGetAllIncome = "SELECT income_id, income.date, wallets_list.wallet_name, income_category_name, income.amount, income.comment FROM income\n" +
                "        LEFT JOIN wallets_list on wallets_list.wallet_id = income.wallet_id\n" +
                "        LEFT JOIN income_category on income.income_category_id = income_category.income_category_id;";

        ResultSet resultSet = statement.executeQuery(queryGetAllIncome);
        List<IncomeEntity> incomeEntityList = new ArrayList<>();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        while (resultSet.next()) {
            incomeEntityList.add(new IncomeEntity(
                    resultSet.getInt("income_id"),
                    LocalDate.parse(resultSet.getString("income.date"), dtf),
                    resultSet.getString("wallets_list.wallet_name"),
                    resultSet.getString("income_category_name"),
                    resultSet.getDouble("income.amount"),
                    resultSet.getString("income.comment")
            ));
        }
        return incomeEntityList;
    }

    public Map<Integer, String> getWalletList() throws SQLException {
        Statement statement = connection.createStatement();
        String qGetWalletList = "SELECT `wallet_id`, `wallet_name` FROM `wallets_list`;";
        ResultSet resultSet = statement.executeQuery(qGetWalletList);
        Map<Integer, String> walletList = new HashMap<>();

        while (resultSet.next()) {
            walletList.put(resultSet.getInt("wallet_id"), resultSet.getString("wallet_name"));
        }
        return walletList;
    }

    public Map<Integer, String> getIncomeCategoryList() throws SQLException {
        Statement statement = connection.createStatement();
        String qGetIncomeList = "SELECT `income_category_id`, `income_category_name` FROM `income_category`;";
        ResultSet resultSet = statement.executeQuery(qGetIncomeList);
        Map<Integer, String> incomeCategoryList = new HashMap<>();

        while (resultSet.next()) {
            incomeCategoryList.put(resultSet.getInt("income_category_id"), resultSet.getString("income_category_name"));
        }
        return incomeCategoryList;
    }

    public void updateIncomeDate(int id, LocalDate newDate) throws SQLException {
        String sql = "UPDATE `income` SET `date` = ? WHERE `income_id` = ?;";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setDate(1, Date.valueOf(newDate));
        preparedStatement.setInt(2, id);
        preparedStatement.executeUpdate();
    }

    public void doEditWalletField(int id, int newWalletId) throws SQLException {
        Statement statement = connection.createStatement();
        String queryUpdate = "UPDATE `income` set `wallet_id` = " + newWalletId + " WHERE `income_id` = " + id;
        statement.executeUpdate(queryUpdate);
    }

    public String getDefaultWallet() throws SQLException {
        Statement statement = connection.createStatement();
        String getValue = "SELECT `wallet_name` FROM `wallets_list` WHERE `wallet_default` = true;";
        ResultSet resultSet = statement.executeQuery(getValue);
        resultSet.next();
        return resultSet.getString("wallet_name");
    }

    public void doEditIncomeCategoryField(int id, int newIncomeCategoryId) throws SQLException {
        Statement statement = connection.createStatement();
        String queryUpdate = "UPDATE `income` set `income_category_id` = " + newIncomeCategoryId + " WHERE `income_id` = " + id;
        statement.executeUpdate(queryUpdate);
    }

    public String getDefaultIncomeCategory() throws SQLException {
        Statement statement = connection.createStatement();
        String getValue = "SELECT `income_category_name` FROM `income_category` WHERE `income_default` = true;";
        ResultSet resultSet = statement.executeQuery(getValue);
        resultSet.next();
        return resultSet.getString("income_category_name");
    }

    public boolean doEditIncomeAmountField(int id, double amount) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("UPDATE `income` SET `amount` = ? WHERE `income_id` = ?;");
        preparedStatement.setDouble(1, amount);
        preparedStatement.setInt(2, id);
        int result = preparedStatement.executeUpdate();
        return result > 0;
    }

    public boolean doEditIncomeCommentField(int id, String newText) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("UPDATE `income` SET `comment` = ? WHERE `income_id` = ?;");
        preparedStatement.setString(1, newText);
        preparedStatement.setInt(2, id);
        int result = preparedStatement.executeUpdate();
        return result > 0;
    }

    public boolean addNewIncomeRow(LocalDate date, int walletId, int categoryId, double amount, String comment) throws SQLException {
        double currentWalletBalance = getWalletBalance(walletId);
        int balanceWasUpdated = updateWalletBalance(walletId, currentWalletBalance + amount);

        PreparedStatement preparedStatement =
            connection.prepareStatement("INSERT INTO `income` (date, wallet_id, income_category_id, amount, comment) VALUES (?, ?, ?, ?, ?);");
        preparedStatement.setDate(1, Date.valueOf(date));
        preparedStatement.setInt(2, walletId);
        preparedStatement.setInt(3, categoryId);
        preparedStatement.setDouble(4, amount);
        preparedStatement.setString(5, comment);
        int result = preparedStatement.executeUpdate();
        if (result > 0 && balanceWasUpdated > 0) {
            return true;
        } else {
            if (balanceWasUpdated <= 0) LOGGER.error("Метод добавления дохода выполнился с ошибкой, балланс не обновился");
            if (result <= 0) LOGGER.error("Метод добавления дохода выполнился с ошибкой, новый доход не был добавлен в таблицу Income");
            return false;
        }
    }

    public boolean deleteIncome(int incomeId, int walletId, double amount) throws SQLException {
        double currentWalletBalance = getWalletBalance(walletId);
        int balanceWasUpdated = updateWalletBalance(walletId, currentWalletBalance - amount);

        Statement statement = connection.createStatement();
        String query = "DELETE FROM `income` WHERE `income_id` = " + incomeId + ";";
        int result = statement.executeUpdate(query);
        if (balanceWasUpdated > 0 && result > 0) {
            return true;
        } else {
            if (balanceWasUpdated <=0) LOGGER.error("Метод удаления дохода выполнился с ошибкой, балланс не был обновлён после удаления. ID = " + walletId);
            if (result <= 0) LOGGER.error("Метод удаления дохода выполнился с ошибкой, строка не была удалена из БД. ID = " + incomeId);
            return false;
        }
    }

    public double getWalletBalance(int id) throws SQLException {
        Statement statement = connection.createStatement();
        String query = "SELECT `wallet_balance` FROM `wallets_list` WHERE wallet_id = " + id + ";";
        ResultSet resultSet = statement.executeQuery(query);
        double currentWalletBalance = 0;
        if (resultSet.next()) currentWalletBalance = resultSet.getDouble("wallet_balance");
        return currentWalletBalance;
    }

    public int updateWalletBalance(int id, double newValue) throws SQLException {
        PreparedStatement preparedStatementUpdateWalletBalance =
                connection.prepareStatement("UPDATE `wallets_list` SET `wallet_balance` = ? WHERE `wallet_id` = ?;");
        preparedStatementUpdateWalletBalance.setDouble(1, newValue);
        preparedStatementUpdateWalletBalance.setInt(2, id);
        return preparedStatementUpdateWalletBalance.executeUpdate();
    }
}
