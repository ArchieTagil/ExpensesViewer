package ru.viewer.expensesviewer.controller.reports;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class TablesController implements Initializable {
    @FXML
    private RadioButton radioIncome;
    @FXML
    private RadioButton radioExpenses;
    @FXML
    private DatePicker dateFrom;
    @FXML
    private DatePicker dateTo;
    @FXML
    private ChoiceBox groupBy;
    @FXML
    private ChoiceBox filterWallet;
    @FXML
    private ChoiceBox filterCategory;
    @FXML
    private Button commit;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<String> groupByList = FXCollections.observableArrayList();
        groupByList.add("Без группировки");
        groupByList.add("Дате");
        groupByList.add("Кошельку");
        groupByList.add("Категории");
        groupBy.setItems(groupByList);
        groupBy.setValue(groupByList.get(0));
    }

    public void showReport(ActionEvent actionEvent) {
    }
}
