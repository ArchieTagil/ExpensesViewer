package ru.viewer.expensesviewer.model.reports;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.viewer.expensesviewer.model.DbConnection;
import ru.viewer.expensesviewer.model.objects.IncomeEntity;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TableModel {
    private static final Logger LOGGER = LogManager.getLogger(TableModel.class);
    private final Connection connection = DbConnection.getInstance().getConnection();


    public List<IncomeEntity> getReportList(LocalDate from, LocalDate to, String walletName, String categoryName, String groupByValue, boolean isReportTypeExpensive) {
        if (walletName.equals("Без фильтра")) walletName = "%";
        if (categoryName.equals("Без фильтра")) categoryName = "%";
        @SuppressWarnings("UnusedAssignment")
        String selectIncomeString = "";
        @SuppressWarnings("UnusedAssignment")
        String selectExpensesString = "";

        switch (groupByValue) {
            case "Дате": {
                groupByValue = "date";
                selectIncomeString = "" +
                        "ANY_VALUE(income_id) id, " +
                        "income.date AS date, " +
                        "ANY_VALUE(wallets_list.wallet_name) wallet, " +
                        "ANY_VALUE(income_category_name) category, " +
                        "SUM(amount) AS amount, " +
                        "ANY_VALUE(income.comment) comment ";
                selectExpensesString = "" +
                        "ANY_VALUE(expenses_id) id, " +
                        "expenses.date AS date, " +
                        "ANY_VALUE(wallets_list.wallet_name) wallet, " +
                        "ANY_VALUE(expenses_category_name) category, " +
                        "SUM(amount) AS amount, " +
                        "ANY_VALUE(expenses.comment) comment ";
                break;
            }
            case "Кошельку": {
                groupByValue = "wallet";
                selectIncomeString = "" +
                        "ANY_VALUE(income_id) id, " +
                        "ANY_VALUE(income.date) date, " +
                        "wallets_list.wallet_name AS wallet, " +
                        "ANY_VALUE(income_category_name) category, " +
                        "SUM(amount) AS amount, " +
                        "ANY_VALUE(income.comment) comment ";
                selectExpensesString = "" +
                        "ANY_VALUE(expenses_id) id, " +
                        "ANY_VALUE(expenses.date) date, " +
                        "wallets_list.wallet_name AS wallet, " +
                        "ANY_VALUE(expenses_category_name) category, " +
                        "SUM(amount) AS amount, " +
                        "ANY_VALUE(expenses.comment) comment ";
                break;
            }
            case "Категории": {
                groupByValue = "category";
                selectIncomeString = "" +
                        "ANY_VALUE(income_id) id, " +
                        "ANY_VALUE(income.date) date, " +
                        "ANY_VALUE(wallets_list.wallet_name) wallet, " +
                        "income_category_name AS category, " +
                        "SUM(amount) AS amount, " +
                        "ANY_VALUE(income.comment) comment ";
                selectExpensesString = "" +
                        "ANY_VALUE(expenses_id) id, " +
                        "ANY_VALUE(expenses.date) date, " +
                        "ANY_VALUE(wallets_list.wallet_name) wallet, " +
                        "expenses_category_name AS category, " +
                        "SUM(amount) AS amount, " +
                        "ANY_VALUE(expenses.comment) comment ";

                break;
            }
            default: {
                groupByValue = "";
                selectIncomeString = "" +
                        "income_id AS id, " +
                        "income.date AS date, " +
                        "wallets_list.wallet_name AS wallet, " +
                        "income_category_name AS category, " +
                        "amount AS amount, " +
                        "income.comment AS comment ";
                selectExpensesString = "" +
                        "expenses_id AS id, " +
                        "expenses.date AS date, " +
                        "wallets_list.wallet_name AS wallet, " +
                        "expenses_category_name AS category, " +
                        "amount AS amount, " +
                        "expenses.comment AS comment ";
            }
        }

        String qGroupBy = !groupByValue.equals("") ? " GROUP BY " + groupByValue : "";

        try (Statement reportStatement = connection.createStatement(); Statement totalStatement = connection.createStatement()) {
            String queryGetAllIncome;
            String queryTotal;
            if (!isReportTypeExpensive) {
                queryGetAllIncome = "SELECT " +
                        selectIncomeString +
                        "FROM income " +
                        "LEFT JOIN wallets_list on wallets_list.wallet_id = income.wallet_id " +
                        "LEFT JOIN income_category on income.income_category_id = income_category.income_category_id " +
                        "WHERE wallets_list.wallet_name LIKE '" + walletName + "' " +
                        "AND income_category_name LIKE '" + categoryName + "' " +
                        "AND (date BETWEEN '" + from + "' AND '" + to + "') " +
                        qGroupBy + " ORDER BY income.income_id;";
                queryTotal = "SELECT TRUNCATE(SUM(amount), 2) AS amount from income " +
                        "LEFT JOIN wallets_list on wallets_list.wallet_id = income.wallet_id " +
                        "LEFT JOIN income_category on income.income_category_id = income_category.income_category_id " +
                        "WHERE wallets_list.wallet_name LIKE '" + walletName + "' " +
                        "AND income_category_name LIKE '" + categoryName + "' " +
                        "AND (date BETWEEN '" + from + "' AND '" + to + "')";
            } else {
                queryGetAllIncome = "SELECT " +
                        selectExpensesString +
                        "FROM expenses " +
                        "LEFT JOIN wallets_list on wallets_list.wallet_id = expenses.debit_wallet_id " +
                        "LEFT JOIN expenses_category on expenses.expenses_category_id = expenses_category.expenses_category_id " +
                        "WHERE wallets_list.wallet_name LIKE '" + walletName + "' " +
                        "AND expenses_category_name LIKE '" + categoryName + "' " +
                        "AND (date BETWEEN '" + from + "' AND '" + to + "') " +
                        qGroupBy + " ORDER BY expenses.expenses_id;";
                queryTotal = "SELECT TRUNCATE(SUM(amount), 2) AS amount from expenses " +
                        "LEFT JOIN wallets_list on wallets_list.wallet_id = expenses.debit_wallet_id " +
                        "LEFT JOIN expenses_category on expenses.expenses_category_id = expenses_category.expenses_category_id " +
                        "WHERE wallets_list.wallet_name LIKE '" + walletName + "' " +
                        "AND expenses_category_name LIKE '" + categoryName + "' " +
                        "AND (date BETWEEN '" + from + "' AND '" + to + "');";

            }
            ResultSet resultSet = reportStatement.executeQuery(queryGetAllIncome);
            List<IncomeEntity> resultList = new ArrayList<>();

            ResultSet totalResultSet = totalStatement.executeQuery(queryTotal);
            totalResultSet.next();
            double total = totalResultSet.getDouble("amount");

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            while (resultSet.next()) {
                resultList.add(new IncomeEntity(
                        resultSet.getInt("id"),
                        LocalDate.parse(resultSet.getString("date"), dtf),
                        resultSet.getString("wallet"),
                        resultSet.getString("category"),
                        resultSet.getDouble("amount"),
                        resultSet.getString("comment")
                ));
            }
            resultList.add(new IncomeEntity(0, null, "", "Итого:", total, ""));
            return resultList;
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
