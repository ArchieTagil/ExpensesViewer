package ru.viewer.expensesviewer.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.viewer.expensesviewer.controller.IncomeController;
import ru.viewer.expensesviewer.controller.MainController;
import ru.viewer.expensesviewer.model.objects.IncomeEntity;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class IncomeModel {
    private final Connection connection = DbConnection.getInstance().getConnection();
    private static final Logger LOGGER = LogManager.getLogger(IncomeController.class);

    public List<IncomeEntity> getIncomeList() throws SQLException {
        Statement statement = connection.createStatement();
        String queryGetAllIncome = "SELECT income_id, income.date, wallets_list.wallet_name, income_category_name, income.amount, income.comment FROM income\n" +
                "        LEFT JOIN wallets_list on wallets_list.wallet_id = income.wallet_id\n" +
                "        LEFT JOIN income_category on income.income_category_id = income_category.income_category_id ORDER BY `date` DESC, `income_id` DESC;";

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

    public void doEditIncomeCategoryField(int id, int newIncomeCategoryId) throws SQLException {
        Statement statement = connection.createStatement();
        String queryUpdate = "UPDATE `income` set `income_category_id` = " + newIncomeCategoryId + " WHERE `income_id` = " + id;
        statement.executeUpdate(queryUpdate);
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

    @SuppressWarnings("Duplicates")
    public boolean addNewIncomeRow(LocalDate date, int walletId, int categoryId, double amount, String comment) throws SQLException {
        double currentWalletBalance = MainController.getWalletBalanceById(walletId);
        int balanceWasUpdated = MainController.updateWalletBalanceById(walletId, currentWalletBalance + amount);

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

    @SuppressWarnings("Duplicates")
    public boolean deleteIncome(int incomeId, int walletId, double amount) throws SQLException {
        double currentWalletBalance = MainController.getWalletBalanceById(walletId);
        int balanceWasUpdated = MainController.updateWalletBalanceById(walletId, currentWalletBalance - amount);

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

    public void justDeleteIncome(int id) {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("DELETE FROM `income` WHERE `income_id` = " + id + ";");
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
    }
}
