package ru.viewer.expensesviewer.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.converter.LocalDateStringConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.viewer.expensesviewer.model.ExpensesModel;
import ru.viewer.expensesviewer.model.objects.ExpenseEntity;
import ru.viewer.expensesviewer.model.objects.Popup;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class ExpensesController {
    private MainController mainController;
    private final ExpensesModel expensesModel = new ExpensesModel();
    private static final Logger LOGGER = LogManager.getLogger(ExpensesController.class);
    private Map<Integer, String> expensesCategoryList;
    private ObservableList<String> expensesCategoryObservableList;
    @FXML
    private AnchorPane expensesAnchorPane;
    @FXML
    private TableView<ExpenseEntity> expensesTable;
    @FXML
    private TableColumn<ExpenseEntity, Integer> expenseId;
    @FXML
    private TableColumn<ExpenseEntity, LocalDate> expenseDate;
    @FXML
    private TableColumn<ExpenseEntity, String> Wallet;
    @FXML
    private TableColumn<ExpenseEntity, String> expensesCategory;
    @FXML
    private TableColumn<ExpenseEntity, Double> expenseAmount;
    @FXML
    private TableColumn<ExpenseEntity, String> expenseComment;
    @FXML
    private DatePicker expenseDateNewRow;
    @FXML
    private ChoiceBox<String> selectExpenseWalletNewRow;
    @FXML
    private ChoiceBox<String> selectExpenseCategoryNewRow;
    @FXML
    private TextField expenseAmountNewRow;
    @FXML
    private TextField expenseCommentNewRow;
    @FXML
    private Button expenseAddButton;

    @FXML
    @SuppressWarnings("Duplicates")
    private void initialize() {
        initHotKeys();

        initInsertFieldsSettings();
        drawExpensesList();

        expensesTable.setEditable(true);
        expensesTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        expenseId.setCellValueFactory(new PropertyValueFactory<>("id"));

        expenseDate.setCellFactory(MainController.dateCallbackForExpenses);
        expenseDate.setCellFactory(TextFieldTableCell.forTableColumn(new LocalDateStringConverter()));
        expenseDate.setCellValueFactory(new PropertyValueFactory<>("date"));

        Wallet.setCellFactory(ChoiceBoxTableCell.forTableColumn(MainController.getWalletObservableList()));
        Wallet.setCellValueFactory(new PropertyValueFactory<>("wallet_name"));

        expensesCategory.setCellFactory(ChoiceBoxTableCell.forTableColumn(expensesCategoryObservableList));
        expensesCategory.setCellValueFactory(new PropertyValueFactory<>("expense_category"));

        expenseAmount.setCellFactory(MainController.amountCallbackForExpenses);
        expenseAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));

        expenseComment.setCellFactory(TextFieldTableCell.forTableColumn());
        expenseComment.setCellValueFactory(new PropertyValueFactory<>("comment"));
    }

    public void dateEditCommit(TableColumn.CellEditEvent<ExpenseEntity, LocalDate> cellEditEvent) {
        LocalDate newDate = cellEditEvent.getNewValue();
        int id = cellEditEvent.getRowValue().getId();
        expensesModel.updateExpenseRowDate(id, newDate);
        drawExpensesList();
    }

    @SuppressWarnings("Duplicates")
    public void walletEditCommit(TableColumn.CellEditEvent<ExpenseEntity, String> cellEditEvent) {
        int currentIncomeRowId = cellEditEvent.getRowValue().getId();
        double amountInCurrentRow = cellEditEvent.getRowValue().getAmount();

        if (!MainController.getWalletObservableList().isEmpty() && cellEditEvent.getNewValue() != null) {
            if (cellEditEvent.getOldValue() != null){
                int oldWalletId = MainController.getWalletIdByName(cellEditEvent.getOldValue());
                double oldWalletBalance = MainController.getWalletBalanceById(oldWalletId);
                MainController.updateWalletBalanceById(oldWalletId, oldWalletBalance + amountInCurrentRow);
            }
            int newWalletId = MainController.getWalletIdByName(cellEditEvent.getNewValue());
            double newWalletBalance = MainController.getWalletBalanceById(newWalletId);

            MainController.updateWalletBalanceById(newWalletId, newWalletBalance - amountInCurrentRow);
            expensesModel.doEditWalletField(currentIncomeRowId, newWalletId);
        }
        drawExpensesList();
        mainController.initBalance();
    }

    @SuppressWarnings("Duplicates")
    public void categoryEditCommit(TableColumn.CellEditEvent<ExpenseEntity, String> cellEditEvent) {
        int currentExpenseRowId = cellEditEvent.getRowValue().getId();
        expensesCategoryList = MainController.getExpensesCategoryList();
        if (!expensesCategoryList.isEmpty() && cellEditEvent.getNewValue() != null) {
            int newExpenseCategoryId = expensesCategoryList.entrySet().stream().
                    filter(e -> e.getValue().equals(cellEditEvent.getNewValue())).
                    findFirst().orElseThrow(() -> {
                        LOGGER.fatal("categoryEditCommit gets null");
                        return new NullPointerException("categoryEditCommit get null");
                    }).getKey();
            expensesModel.doEditExpensesCategoryField(currentExpenseRowId, newExpenseCategoryId);
        }
        drawExpensesList();
    }

    @SuppressWarnings("Duplicates")
    public void sumEditCommit(TableColumn.CellEditEvent<ExpenseEntity, Double> cellEditEvent) {
        int walletId = MainController.getWalletIdByName(cellEditEvent.getRowValue().getWallet_name());
        double walletBalance = MainController.getWalletBalanceById(walletId);
        int currentExpenseRowId = cellEditEvent.getRowValue().getId();
        double oldAmount = cellEditEvent.getOldValue();
        double newAmount = cellEditEvent.getNewValue();
        MainController.updateWalletBalanceById(walletId, walletBalance - (newAmount - oldAmount));
        boolean expenseWasChanged = expensesModel.doEditExpenseAmountField(currentExpenseRowId, newAmount);
        if (!expenseWasChanged) {
            LOGGER.error("Expense amount edit error during a change in database.");
            Popup.display("Expense amount edit error", "Упс, что то пошло не так, не удалось изменить данные в БД");
        } else {
            drawExpensesList();
            mainController.initBalance();
        }
    }

    public void commentEditCommit(TableColumn.CellEditEvent<ExpenseEntity, String> cellEditEvent) {
        int currentExpensesRowId = cellEditEvent.getRowValue().getId();
        String newText = cellEditEvent.getNewValue();
        boolean commentWasChanged = expensesModel.doEditExpensesCommentField(currentExpensesRowId, newText);
        if (!commentWasChanged) {
            LOGGER.error("Expenses comment edit error during a change in database.");
            Popup.display("Expenses comment edit error", "Упс, что то пошло не так, не удалось изменить данные в БД");
        }
    }

    public void addNewExpense() {
        expensesCategoryList = MainController.getExpensesCategoryList();
        LocalDate date = expenseDateNewRow.getValue();
        if (selectExpenseWalletNewRow.getValue() != null && selectExpenseCategoryNewRow.getValue() != null) {
            int walletId = MainController.getWalletIdByName(selectExpenseWalletNewRow.getValue());
            int categoryId = getCategoryIdByName(selectExpenseCategoryNewRow.getValue());

            double amount = 0;
            try {
                amount = Double.parseDouble(expenseAmountNewRow.getText());
            } catch (RuntimeException NumberFormatException) {
                Popup.display("Wrong amount", "Вы ввели некорретное число.");
            }
            String comment = expenseCommentNewRow.getText();
            boolean incomeRowWasAdded = expensesModel.addNewExpensesRow(date, walletId, categoryId, amount, comment);
            if (incomeRowWasAdded) {
                drawExpensesList();
                mainController.initBalance();
            } else {
                Popup.display("Income wasn't added", "Упс, что то пошло не так, запис не была добавлена в БД");
                LOGGER.error("income wasn't added");
            }
        }
    }

    @SuppressWarnings("Duplicates")
    public void deleteRows(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.DELETE) {
            ObservableList<ExpenseEntity> list = expensesTable.getSelectionModel().getSelectedItems();
            for (ExpenseEntity entity : list) {
                if (entity.getWallet_name() != null) {
                    boolean isDeleted = expensesModel.deleteExpense(entity.getId(), MainController.getWalletIdByName(entity.getWallet_name()), entity.getAmount());
                    if (!isDeleted) LOGGER.debug("id: " + entity.getId() + " was failed to delete.");
                } else {
                    expensesModel.justDeleteExpense(entity.getId());
                }
            }
            mainController.initBalance();
            drawExpensesList();
        }
    }

    private int getCategoryIdByName(String name) {
        return expensesCategoryList.entrySet().stream().
                filter(s -> s.getValue().equals(name)).
                findFirst().orElseThrow(() -> {
                    LOGGER.fatal("categoryEditCommit gets null");
                    return new NullPointerException("categoryEditCommit get null");
                }).getKey();
    }

    private void drawExpensesList() {
        List<ExpenseEntity> expenseEntityList = expensesModel.getExpensesList();
        ObservableList<ExpenseEntity> observableList = FXCollections.observableArrayList(expenseEntityList);
        expensesTable.setItems(observableList);
    }
    @SuppressWarnings("Duplicates")
    private void initInsertFieldsSettings() {
        expenseDateNewRow.setValue(LocalDate.now());
        expensesCategoryList = MainController.getExpensesCategoryList();
        expensesCategoryObservableList = FXCollections.observableArrayList(expensesCategoryList.values());

        selectExpenseWalletNewRow.setItems(MainController.getWalletObservableList());
        selectExpenseCategoryNewRow.setItems(expensesCategoryObservableList);

        selectExpenseWalletNewRow.setValue(MainController.getDefaultWalletName());
        selectExpenseCategoryNewRow.setValue(MainController.getDefaultExpensesCategory());

        expenseAmountNewRow.setTextFormatter(MainController.getOnlyDigitsTextFormatter());
    }
    public void updateVisualInformation() {
        Wallet.setCellFactory(ChoiceBoxTableCell.forTableColumn(MainController.getWalletObservableList()));
        selectExpenseWalletNewRow.setItems(MainController.getWalletObservableList());
        selectExpenseWalletNewRow.setValue(MainController.getDefaultWalletName());

        selectExpenseCategoryNewRow.setItems(FXCollections.observableArrayList(MainController.getExpensesCategoryList().values()));
        selectExpenseCategoryNewRow.setValue(MainController.getDefaultExpensesCategory());

        expensesCategory.setCellFactory(ChoiceBoxTableCell.forTableColumn(FXCollections.observableArrayList(MainController.getExpensesCategoryList().values())));
        drawExpensesList();
    }
    @SuppressWarnings("Duplicates")
    private void initHotKeys() {
        KeyCodeCombination alt1 = new KeyCodeCombination(KeyCode.DIGIT1, KeyCombination.ALT_DOWN);
        KeyCodeCombination ctrlEnter = new KeyCodeCombination(KeyCode.ENTER, KeyCombination.SHORTCUT_DOWN);

        EventHandler<KeyEvent> filter = event -> {
            if (ctrlEnter.match(event)) {
                expenseAddButton.fire();
            } else if (alt1.match(event)) {
                expenseDateNewRow.requestFocus();
            }
        };
        expensesAnchorPane.addEventFilter(KeyEvent.KEY_PRESSED, filter);
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}
