package ru.viewer.expensesviewer.controller.reports;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.viewer.expensesviewer.controller.MainController;
import ru.viewer.expensesviewer.model.objects.IncomeEntity;
import ru.viewer.expensesviewer.model.reports.TableModel;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class TablesController implements Initializable {
    @SuppressWarnings("unused")
    private static final Logger LOGGER = LogManager.getLogger(TablesController.class);
    private final TableModel tableModel = new TableModel();
    @FXML
    @SuppressWarnings("unused")
    private ToggleGroup ReportType;
    @FXML
    private Boolean isReportTypeExpenses = false;
    @FXML
    private DatePicker dateFrom;
    @FXML
    private DatePicker dateTo;
    @FXML
    private ChoiceBox<String> groupBy;
    @FXML
    private ChoiceBox<String> filterWallet;
    @FXML
    private ChoiceBox<String> filterCategory;
    @FXML
    private TableView<IncomeEntity> reportTableIncome;
    @FXML
    private TableColumn<IncomeEntity, Integer> reportTableIncomeId;
    @FXML
    private TableColumn<IncomeEntity, LocalDate> reportTableIncomeDate;
    @FXML
    private TableColumn<IncomeEntity, String> reportTableIncomeWallet;
    @FXML
    private TableColumn<IncomeEntity, String> reportTableIncomeCategory;
    @FXML
    private TableColumn<IncomeEntity, Double> reportTableIncomeAmount;
    @FXML
    private TableColumn<IncomeEntity, String> reportTableIncomeComment;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        reportTableIncomeId.setCellValueFactory(new PropertyValueFactory<>("id"));
        reportTableIncomeDate.setCellFactory(MainController.dateCallbackForIncome);
        reportTableIncomeDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        reportTableIncomeWallet.setCellValueFactory(new PropertyValueFactory<>("wallet_name"));
        reportTableIncomeCategory.setCellValueFactory(new PropertyValueFactory<>("income_category"));
        reportTableIncomeAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        reportTableIncomeComment.setCellValueFactory(new PropertyValueFactory<>("comment"));

        dateFrom.setValue(LocalDate.now());
        dateTo.setValue(LocalDate.now());

        ObservableList<String> groupByList = FXCollections.observableArrayList();
        groupByList.add("Без группировки");
        groupByList.add("Дате");
        groupByList.add("Кошельку");
        groupByList.add("Категории");
        groupBy.setItems(groupByList);
        groupBy.setValue(groupByList.get(0));

        selectIncome();
        initDynamicSelectFields();
    }

    public void showReport() {

        reportTableIncome.setItems(FXCollections.observableArrayList(tableModel.getReportList(
                dateFrom.getValue(),
                dateTo.getValue(),
                filterWallet.getValue(),
                filterCategory.getValue(),
                groupBy.getValue(),
                isReportTypeExpenses
                )));
    }

    public void selectIncome() {
        ObservableList<String> listIncomeCategory = FXCollections.observableArrayList(MainController.getIncomeCategoryList().values());
        listIncomeCategory.add(0, "Без фильтра");
        filterCategory.setItems(listIncomeCategory);
        filterCategory.setValue(listIncomeCategory.get(0));
        isReportTypeExpenses = false;
    }

    public void selectExpenses() {
        ObservableList<String> listExpensesCategory = FXCollections.observableArrayList(MainController.getExpensesCategoryList().values());
        listExpensesCategory.add(0, "Без фильтра");
        filterCategory.setItems(listExpensesCategory);
        filterCategory.setValue(listExpensesCategory.get(0));
        isReportTypeExpenses = true;
    }

    public void initDynamicSelectFields() {
        ObservableList<String> listWallet = FXCollections.observableArrayList(MainController.getWalletList().values());
        listWallet.add(0, "Без фильтра");
        filterWallet.setItems(listWallet);
        filterWallet.setValue(listWallet.get(0));

        if (isReportTypeExpenses) {
            selectExpenses();
        } else {
            selectIncome();
        }
    }
}
