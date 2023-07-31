package ru.viewer.expensesviewer.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.viewer.expensesviewer.controller.IncomeController;
import ru.viewer.expensesviewer.controller.MainController;
import ru.viewer.expensesviewer.model.objects.ExpenseEntity;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ExpensesModel {
    private final Connection connection = DbConnection.getInstance().getConnection();
    private static final Logger LOGGER = LogManager.getLogger(IncomeController.class);

    @SuppressWarnings("Duplicates")
    public List<ExpenseEntity> getExpensesList() {
        try (Statement statement = connection.createStatement()) {
            String queryGetAllExpenses = "SELECT expenses.expenses_id, expenses.date, wallets_list.wallet_name, expenses_category.expenses_category_name, expenses.amount, expenses.comment FROM expenses\n" +
                    "        LEFT JOIN wallets_list on wallets_list.wallet_id = expenses.debit_wallet_id\n" +
                    "        LEFT JOIN expenses_category on expenses.expenses_category_id = expenses_category.expenses_category_id ORDER BY `expenses_id` DESC;";

            ResultSet resultSet = statement.executeQuery(queryGetAllExpenses);
            List<ExpenseEntity> expensesEntityList = new ArrayList<>();
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            while (resultSet.next()) {
                expensesEntityList.add(new ExpenseEntity(
                        resultSet.getInt("expenses.expenses_id"),
                        LocalDate.parse(resultSet.getString("expenses.date"), dtf),
                        resultSet.getString("wallets_list.wallet_name"),
                        resultSet.getString("expenses_category.expenses_category_name"),
                        resultSet.getDouble("expenses.amount"),
                        resultSet.getString("expenses.comment")
                ));
            }
            return expensesEntityList;
        } catch (SQLException e) {
            LOGGER.fatal("Can't get expenses list from DB!");
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("Duplicates")
    public boolean addNewExpensesRow(LocalDate date, int walletId, int categoryId, double amount, String comment) {
        double currentWalletBalance = MainController.getWalletBalanceById(walletId);
        int balanceWasUpdated = MainController.updateWalletBalanceById(walletId, currentWalletBalance - amount);

        try (PreparedStatement preparedStatement =
                connection.prepareStatement("INSERT INTO `expenses` (date, debit_wallet_id, expenses_category_id, amount, comment) VALUES (?, ?, ?, ?, ?);")) {
            preparedStatement.setDate(1, Date.valueOf(date));
            preparedStatement.setInt(2, walletId);
            preparedStatement.setInt(3, categoryId);
            preparedStatement.setDouble(4, amount);
            preparedStatement.setString(5, comment);
            int result = preparedStatement.executeUpdate();
            if (result > 0 && balanceWasUpdated > 0) {
                return true;
            } else {
                if (balanceWasUpdated <= 0) LOGGER.error("Метод добавления расхода выполнился с ошибкой, балланс не обновился");
                if (result <= 0) LOGGER.error("Метод добавления расхода выполнился с ошибкой, новый доход не был добавлен в таблицу Expenses");
                return false;
            }
        } catch (SQLException e) {
            LOGGER.error("Expenses row wasn't added to database, something wrong with SQL");
            throw new RuntimeException(e);
        }
    }

    public void updateExpenseRowDate(int id, LocalDate newDate) {
        String sql = "UPDATE `expenses` SET `date` = ? WHERE `expenses_id` = ?;";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setDate(1, Date.valueOf(newDate));
            preparedStatement.setInt(2, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error("Can't update `expenses date` from observable list into DB.");
            throw new RuntimeException(e);
        }
    }

    public void doEditWalletField(int id, int newWalletId) {
        try (Statement statement = connection.createStatement()) {
            String queryUpdate = "UPDATE `expenses` set `debit_wallet_id` = " + newWalletId + " WHERE `expenses_id` = " + id;
            statement.executeUpdate(queryUpdate);
        } catch (SQLException e) {
            LOGGER.error("Can't update `expenses wallet` from observable list into DB.");
            throw new RuntimeException(e);
        }
    }

    public void doEditExpensesCategoryField(int currentRowId, int newExpenseCategoryId) {
        try (Statement statement = connection.createStatement()) {
            String queryUpdate = "UPDATE `expenses` set `expenses_category_id` = " + newExpenseCategoryId + " WHERE `expenses_id` = " + currentRowId;
            statement.executeUpdate(queryUpdate);
        } catch (SQLException e) {
            LOGGER.error("Can't update `expenses category` from observable list into DB.");
            throw new RuntimeException(e);
        }
    }

    public boolean doEditExpenseAmountField(int id, double amount) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE `expenses` SET `amount` = ? WHERE `expenses_id` = ?;")) {
            preparedStatement.setDouble(1, amount);
            preparedStatement.setInt(2, id);
            int result = preparedStatement.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            LOGGER.error("New amount value doesn't sent to database");
            throw new RuntimeException(e);
        }
    }
    public boolean doEditExpensesCommentField(int rowId, String newText) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE `expenses` SET `comment` = ? WHERE `expenses_id` = ?;")) {
            preparedStatement.setString(1, newText);
            preparedStatement.setInt(2, rowId);
            int result = preparedStatement.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            LOGGER.error("Can't update `expenses comment` from observable list into DB.");
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("Duplicates")
    public boolean deleteExpense(int id, int walletId, double amount) {
        double currentWalletBalance = MainController.getWalletBalanceById(walletId);
        int balanceWasUpdated = MainController.updateWalletBalanceById(walletId, currentWalletBalance + amount);

        try (Statement statement = connection.createStatement()) {
            String query = "DELETE FROM `expenses` WHERE `expenses_id` = " + id + ";";
            int result = statement.executeUpdate(query);
            if (balanceWasUpdated > 0 && result > 0) {
                return true;
            } else {
                if (balanceWasUpdated <=0) LOGGER.error("Метод удаления расхода выполнился с ошибкой, балланс не был обновлён после удаления. ID = " + walletId);
                if (result <= 0) LOGGER.error("Метод удаления расхода выполнился с ошибкой, строка не была удалена из БД. ID = " + id);
                return false;
            }
        } catch (SQLException e) {
            LOGGER.error("Row wasn't delete because SQL function in Expenses model wasn't execute properly.");
            throw new RuntimeException(e);
        }
    }

    public void justDeleteExpense(int id) {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("DELETE FROM `expenses` WHERE `expenses_id` = " + id + ";");
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}