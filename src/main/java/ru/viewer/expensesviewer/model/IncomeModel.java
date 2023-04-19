package ru.viewer.expensesviewer.model;

import ru.viewer.expensesviewer.model.objects.IncomeEntity;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class IncomeModel {
    private Connection connection = DbConnection.getInstance().getConnection();

    public IncomeModel() throws SQLException, ClassNotFoundException {
    }

    public void execute() throws SQLException {
        System.out.println(getIncomeList());
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
                    //LocalDateTime.ofInstant(resultSet.getDate("income.date").toInstant(), ZoneId.systemDefault()),
                    resultSet.getInt("income_id"),
                    LocalDateTime.parse(resultSet.getString("income.date"), dtf),
                    resultSet.getString("wallets_list.wallet_name"),
                    resultSet.getString("income_category_name"),
                    resultSet.getDouble("income.amount"),
                    resultSet.getString("income.comment")
            ));
        }
        return incomeEntityList;
    }
}
