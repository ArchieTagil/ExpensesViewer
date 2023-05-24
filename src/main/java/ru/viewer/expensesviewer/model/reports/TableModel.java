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

        switch (groupByValue) {
            case "Дате": {
                groupByValue = "date";
                break;
            }
            case "Кошельку": {
                groupByValue = "wallet";
                break;
            }
            case "Категории": {
                groupByValue = "category";
                break;
            }
            default: {
                groupByValue = "";
            }
        }

        String qGroupBy = !groupByValue.equals("") ? " GROUP BY " + groupByValue + ";" : ";";
        String qAmount = !groupByValue.equals("") ? "SUM(amount) AS amount, " : "amount AS amount, ";

        try (Statement reportStatement = connection.createStatement()) {
            String queryGetAllIncome;
            if (!isReportTypeExpensive) {
                queryGetAllIncome = "SELECT " +
                        "income_id AS id, " +
                        "income.date AS date, " +
                        "wallets_list.wallet_name AS wallet, " +
                        "income_category_name AS category, " +
                        //qAmount1 + " income.amount " + qAmount2 + " AS amount " +
                        qAmount +
                        " income.comment AS comment FROM income " +
                        "LEFT JOIN wallets_list on wallets_list.wallet_id = income.wallet_id " +
                        "LEFT JOIN income_category on income.income_category_id = income_category.income_category_id " +
                        "WHERE wallets_list.wallet_name LIKE '" + walletName + "' " +
                        "AND income_category_name LIKE '" + categoryName + "' " +
                        "AND (date BETWEEN '" + from + "' AND '" + to + "')" +
                        qGroupBy;
            } else {
                queryGetAllIncome = "SELECT " +
                        "expenses_id AS id, " +
                        "expenses.date AS date, " +
                        "wallets_list.wallet_name AS wallet, " +
                        "expenses_category_name AS category, " +
                        qAmount +
                        " expenses.comment AS comment FROM expenses " +
                        "LEFT JOIN wallets_list on wallets_list.wallet_id = expenses.debit_wallet_id " +
                        "LEFT JOIN expenses_category on expenses.expenses_category_id = expenses_category.expenses_category_id " +
                        "WHERE wallets_list.wallet_name LIKE '" + walletName + "' " +
                        "AND expenses_category_name LIKE '" + categoryName + "' " +
                        "AND (date BETWEEN '" + from + "' AND '" + to + "')" +
                        qGroupBy;
            }
            ResultSet resultSet = reportStatement.executeQuery(queryGetAllIncome);
            List<IncomeEntity> resultList = new ArrayList<>();
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
            return resultList;
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
