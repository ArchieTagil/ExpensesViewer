package ru.viewer.expensesviewer.controller.reports;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.AnchorPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.viewer.expensesviewer.model.DbConnection;
import ru.viewer.expensesviewer.model.reports.TableModel;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class GraphicsTab implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(TableModel.class);
    private final Connection connection = DbConnection.getInstance().getConnection();
    @FXML
    private ChoiceBox<String> graphType;
    @FXML
    private DatePicker dateFrom;
    @FXML
    private DatePicker dateTo;
    @FXML
    private AnchorPane anchorPane;
    public void showReport() {
        anchorPane.getChildren().clear();
        LocalDate from = dateFrom.getValue();
        LocalDate to = dateTo.getValue();
        if (graphType.getValue().equals("Доходы по месяцам")) incomePerMonth(from, to);
        else if (graphType.getValue().equals("Расходы за период по категориям")) expensesPieChart(from, to);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList list = FXCollections.observableArrayList();
        list.add("Доходы по месяцам");
        list.add("Расходы за период по категориям");
        graphType.setItems(list);
        graphType.setValue("Расходы за период по категориям");

        dateFrom.setValue(LocalDate.now());
        dateTo.setValue(LocalDate.now());
    }

    public void expensesPieChart(LocalDate from, LocalDate to) {
        try (Statement statement = connection.createStatement()) {
            String query = "SELECT ";
            ResultSet resultSet = statement.executeQuery(query);

            ObservableList<PieChart.Data> observableList = FXCollections.observableArrayList(
                    new PieChart.Data("Iphone 5S", 13),
                    new PieChart.Data("Samsung Grand", 25),
                    new PieChart.Data("MOTO G", 10),
                    new PieChart.Data("Nokia Lumia", 22)
            );

            PieChart pieChart = new PieChart(observableList);
            pieChart.setPrefHeight(350);
            pieChart.setPrefWidth(700);
            anchorPane.getChildren().add(pieChart);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void incomePerMonth(LocalDate from, LocalDate to) {
        String query = "SELECT DATE_FORMAT(income.date, '%Y-%b') AS date, SUM(income.amount) AS amount FROM income WHERE date BETWEEN '" + from + "' AND '" + to + "' GROUP BY DATE_FORMAT(income.date, '%Y-%m') ORDER BY  income.date;";
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(query);

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Доходы по месяцам");

            CategoryAxis xAxis = new CategoryAxis();
            NumberAxis yAxis = new NumberAxis();
            LineChart<String, Number> lineChart = new LineChart(xAxis, yAxis);
            lineChart.setPrefHeight(350);
            lineChart.setPrefWidth(900);

            while (resultSet.next()) {
                series.getData().add(new XYChart.Data<>(resultSet.getString("date"), resultSet.getDouble("amount")));
            }

            lineChart.getData().add(series);
            anchorPane.getChildren().add(lineChart);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
