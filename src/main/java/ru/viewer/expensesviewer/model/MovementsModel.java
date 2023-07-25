package ru.viewer.expensesviewer.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.viewer.expensesviewer.controller.IncomeController;
import ru.viewer.expensesviewer.controller.MainController;
import ru.viewer.expensesviewer.model.objects.MovementEntity;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MovementsModel {
    private final Connection connection = DbConnection.getInstance().getConnection();
    private static final Logger LOGGER = LogManager.getLogger(IncomeController.class);
    @SuppressWarnings("Duplicates")
    public List<MovementEntity> getMovementsList() {
        try (Statement statement = connection.createStatement()) {
            String queryGetAllMovements =
                    "SELECT\n" +
                    "    movements.movements_id, movements.date, wlc.wallet_name AS wallet_credit, wld.wallet_name AS wallet_debit, movements.amount, movements.comment\n" +
                    "FROM movements\n" +
                    "    LEFT JOIN wallets_list wlc on wlc.wallet_id = movements.wallet_credit_id\n" +
                    "    LEFT JOIN wallets_list wld on wld.wallet_id = movements.wallet_debit_id;";

            ResultSet resultSet = statement.executeQuery(queryGetAllMovements);
            List<MovementEntity> movementEntityList = new ArrayList<>();
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            while (resultSet.next()) {
                movementEntityList.add(new MovementEntity(
                        resultSet.getInt("movements.movements_id"),
                        LocalDate.parse(resultSet.getString("movements.date"), dtf),
                        resultSet.getString("wallet_debit"),
                        resultSet.getString("wallet_credit"),
                        resultSet.getDouble("movements.amount"),
                        resultSet.getString("movements.comment")
                ));
            }
            return movementEntityList;
        } catch (SQLException e) {
            LOGGER.fatal("Can't get movements list from DB!");
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("Duplicates")
    public boolean addNewMovement(LocalDate date, int sourceWalletId, int destinationWalletId, double amount, String comment) {
        double sourceWalletOldBalance = MainController.getWalletBalanceById(sourceWalletId);
        double destinationWalletOldBalance = MainController.getWalletBalanceById(destinationWalletId);

        int sourceWalletBalanceWasUpdated = MainController.updateWalletBalanceById(sourceWalletId, sourceWalletOldBalance - amount);
        int destinationWalletBalanceWasUpdated = MainController.updateWalletBalanceById(destinationWalletId, destinationWalletOldBalance + amount);

        try (PreparedStatement preparedStatement =
                     connection.prepareStatement(
                             "INSERT INTO `movements` (date, wallet_debit_id, wallet_credit_id, amount, comment) VALUES (?, ?, ?, ?, ?);")) {
            preparedStatement.setDate(1, Date.valueOf(date));
            preparedStatement.setInt(2, sourceWalletId);
            preparedStatement.setInt(3, destinationWalletId);
            preparedStatement.setDouble(4, amount);
            preparedStatement.setString(5, comment);
            int result = preparedStatement.executeUpdate();
            if (result > 0 && sourceWalletBalanceWasUpdated > 0 && destinationWalletBalanceWasUpdated > 0) {
                return true;
            } else {
                if (sourceWalletBalanceWasUpdated <= 0 || destinationWalletBalanceWasUpdated <=0) LOGGER.error("Метод добавления расхода выполнился с ошибкой, балланс не обновился или обновился неправльно, рекомендую проверить последние транзакции");
                if (result <= 0) LOGGER.error("Метод добавления расхода выполнился с ошибкой, новый доход не был добавлен в таблицу Expenses, но балланс мог измениться");
                return false;
            }
        } catch (SQLException e) {
            LOGGER.error("Expenses row wasn't added to database, something wrong with SQL");
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("Duplicates")
    public void deleteMovement(int id, int sourceWalletId, int destinationWalletId, double amount) {
        double sourceWalletOldBalance = MainController.getWalletBalanceById(sourceWalletId);
        double destinationWalletOldBalance = MainController.getWalletBalanceById(destinationWalletId);

        MainController.updateWalletBalanceById(sourceWalletId, sourceWalletOldBalance + amount);
        MainController.updateWalletBalanceById(destinationWalletId, destinationWalletOldBalance - amount);

        try (Statement statement = connection.createStatement()) {
            String query = "DELETE FROM `movements` WHERE `movements_id` = " + id + ";";
            statement.executeUpdate(query);
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
    }

    public void deleteMovement(int id) {
        try (Statement statement = connection.createStatement()) {
            String query = "DELETE FROM `movements` WHERE `movements_id` = " + id + ";";
            statement.executeUpdate(query);
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
    }

    @SuppressWarnings("Duplicates")
    public void updateExpenseRowDate(int id, LocalDate newDate) {
        String sql = "UPDATE `movements` SET `date` = ? WHERE `movements_id` = ?;";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setDate(1, Date.valueOf(newDate));
            preparedStatement.setInt(2, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error("Can't update `movements date` from observable list into DB.");
            throw new RuntimeException(e);
        }
    }
    @SuppressWarnings("Duplicates")
    public boolean doEditMovementCommentField(int rowId, String newText) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE `movements` SET `comment` = ? WHERE `movements_id` = ?;")) {
            preparedStatement.setString(1, newText);
            preparedStatement.setInt(2, rowId);
            int result = preparedStatement.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            LOGGER.error("Can't update `movements comment` from observable list into DB.");
            throw new RuntimeException(e);
        }
    }

    public void doEditWalletField(int currentMovementRowId, int newWalletId, String field) {
        try (Statement statement = connection.createStatement()) {
            String queryUpdate = "UPDATE `movements` SET " + field + " = " + newWalletId + " WHERE `movements_id` = " + currentMovementRowId;
            statement.executeUpdate(queryUpdate);
        } catch (SQLException e) {
            LOGGER.error("Can't update `movement wallet` from observable list into DB.");
            throw new RuntimeException(e);
        }
    }
    @SuppressWarnings("Duplicates")
    public boolean doEditMovementAmountField(int currentMovementRowId, double newAmountInCurrentRow) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE `movements` SET `amount` = ? WHERE `movements_id` = ?;")) {
            preparedStatement.setDouble(1, newAmountInCurrentRow);
            preparedStatement.setInt(2, currentMovementRowId);
            int result = preparedStatement.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            LOGGER.error("New amount value doesn't sent to database");
            throw new RuntimeException(e);
        }
    }
}
