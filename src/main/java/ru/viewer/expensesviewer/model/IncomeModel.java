package ru.viewer.expensesviewer.model;

import ru.viewer.expensesviewer.model.objects.IncomeTable;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class IncomeModel {
    private Connection connection = DbConnection.getInstance().getConnection();

    public IncomeModel() throws SQLException, ClassNotFoundException {
    }

    public void execute() throws SQLException {
        Statement statement = connection.createStatement();
        String queryGetAllIncome = "SELECT income.date, wallets_list.wallet_name, income_category_name, income.amount, income.comment FROM income\n" +
                "        LEFT JOIN wallets_list on wallets_list.wallet_id = income.wallet_id\n" +
                "        LEFT JOIN income_category on income.income_category_id = income_category.income_category_id;";

        //ResultSet resultSet = statement.executeQuery("SELECT * FROM wallets_list;");
        ResultSet resultSet = statement.executeQuery(queryGetAllIncome);
        List<IncomeTable> incomeTableList = new ArrayList<>();

        while (resultSet.next()) {
            incomeTableList.add(new IncomeTable(
                    //LocalDateTime.ofInstant(resultSet.getDate("income.date").toInstant(), ZoneId.systemDefault()),
                    LocalDateTime.now(),
                    resultSet.getString("wallets_list.wallet_name"),
                    resultSet.getString("income_category_name"),
                    resultSet.getInt("income.amount"),
                    resultSet.getString("income.comment")
            ));
            System.out.println(LocalDateTime.now());
            System.out.println(resultSet.getString("income.date"));
            System.out.println(resultSet.getString("income_category_name"));
            System.out.println(incomeTableList);
        }
    }
}
