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
import ru.viewer.expensesviewer.model.IncomeModel;
import ru.viewer.expensesviewer.model.objects.IncomeEntity;
import ru.viewer.expensesviewer.model.objects.Popup;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class IncomeController {

    private final IncomeModel incomeModel = new IncomeModel();
    private static final Logger LOGGER = LogManager.getLogger(IncomeController.class);
    private MainController mainController;
    private Map<Integer, String> incomeCategoryList;
    private ObservableList<String> incomeCategoryObservableList;

    @FXML
    private TableView<IncomeEntity> incomeTable;
    @FXML
    private TableColumn<IncomeEntity, Integer> incomeId;
    @FXML
    private TableColumn<IncomeEntity, LocalDate> incomeDate;
    @FXML
    private TableColumn<IncomeEntity, String> incomeWallet;
    @FXML
    private TableColumn<IncomeEntity, String> incomeCategory;
    @FXML
    private TableColumn<IncomeEntity, Double> incomeSum;
    @FXML
    private TableColumn<IncomeEntity, String> incomeComment;
    @FXML
    private Button incomeAddButton;
    @FXML
    private DatePicker newIncomeDate;
    @FXML
    private ChoiceBox<String> selectNewIncomeWallet;
    @FXML
    private ChoiceBox<String> selectNewIncomeCategory;
    @FXML
    private TextField newIncomeAmount;
    @FXML
    private TextField newIncomeComment;
    @FXML
    private AnchorPane incomeAnchorPane;

    @SuppressWarnings("Duplicates")
    @FXML
    private void initialize() throws SQLException {
        initInsertFieldsSettings();
        initHotKeys();
        drawIncomeList();

        incomeTable.setEditable(true);
        incomeTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        incomeId.setCellValueFactory(new PropertyValueFactory<>("id"));

        incomeDate.setCellFactory(MainController.dateCallbackForIncome);
        incomeDate.setCellFactory(TextFieldTableCell.forTableColumn(new LocalDateStringConverter()));
        incomeDate.setCellValueFactory(new PropertyValueFactory<>("date"));

        incomeWallet.setCellFactory(ChoiceBoxTableCell.forTableColumn(MainController.getWalletObservableList()));
        incomeWallet.setCellValueFactory(new PropertyValueFactory<>("wallet_name"));

        incomeCategory.setCellFactory(ChoiceBoxTableCell.forTableColumn(incomeCategoryObservableList));
        incomeCategory.setCellValueFactory(new PropertyValueFactory<>("income_category"));

        incomeSum.setCellFactory(MainController.amountCallbackForIncome);
        incomeSum.setCellValueFactory(new PropertyValueFactory<>("amount"));

        incomeComment.setCellFactory(TextFieldTableCell.forTableColumn());
        incomeComment.setCellValueFactory(new PropertyValueFactory<>("comment"));
    }
    private void drawIncomeList() throws SQLException {
        List<IncomeEntity> incomeEntityList = incomeModel.getIncomeList();
        ObservableList<IncomeEntity> observableList = FXCollections.observableArrayList(incomeEntityList);
        incomeTable.setItems(observableList);
    }
    @SuppressWarnings("Duplicates")
    private void initHotKeys() {
        KeyCodeCombination alt1 = new KeyCodeCombination(KeyCode.DIGIT1, KeyCombination.ALT_DOWN);
        KeyCodeCombination ctrlEnter = new KeyCodeCombination(KeyCode.ENTER, KeyCombination.SHORTCUT_DOWN);

        EventHandler<KeyEvent> filter = event -> {
            if (ctrlEnter.match(event)) {
                incomeAddButton.fire();
            } else if (alt1.match(event)) {
                newIncomeDate.requestFocus();
            }
        };
        incomeAnchorPane.addEventFilter(KeyEvent.KEY_PRESSED, filter);
    }

    @SuppressWarnings("Duplicates")
    public void initInsertFieldsSettings() {
        newIncomeDate.setValue(LocalDate.now());
        incomeCategoryList = MainController.getIncomeCategoryList();

        incomeCategoryObservableList = FXCollections.observableArrayList(incomeCategoryList.values());

        selectNewIncomeWallet.setItems(MainController.getWalletObservableList());
        selectNewIncomeCategory.setItems(incomeCategoryObservableList);

        selectNewIncomeWallet.setValue(MainController.getDefaultWalletName());
        selectNewIncomeCategory.setValue(MainController.getDefaultIncomeCategory());

        newIncomeAmount.setTextFormatter(MainController.getOnlyDigitsTextFormatter());
    }

    public void dateEditCommit(TableColumn.CellEditEvent<IncomeEntity, LocalDate> incomeEntityLocalDateCellEditEvent) throws SQLException {
        LocalDate newDate = incomeEntityLocalDateCellEditEvent.getNewValue();
        int id = incomeEntityLocalDateCellEditEvent.getRowValue().getId();
        incomeModel.updateIncomeDate(id, newDate);
        drawIncomeList();
    }

    @SuppressWarnings("Duplicates")
    public void walletEditCommit(TableColumn.CellEditEvent<IncomeEntity, String> incomeEntityStringCellEditEvent) throws SQLException {
        int currentIncomeRowId = incomeEntityStringCellEditEvent.getRowValue().getId();
        double amountInCurrentRow = incomeEntityStringCellEditEvent.getRowValue().getAmount();

        LOGGER.debug("Old wallet name: " + incomeEntityStringCellEditEvent.getOldValue());
        LOGGER.debug("New wallet name: " + incomeEntityStringCellEditEvent.getNewValue());

        if (incomeEntityStringCellEditEvent.getOldValue() != null) {
            int oldWalletId = MainController.getWalletIdByName(incomeEntityStringCellEditEvent.getOldValue());
            double oldWalletBalance = MainController.getWalletBalanceById(oldWalletId);
            MainController.updateWalletBalanceById(oldWalletId, oldWalletBalance - amountInCurrentRow);
        }

        int newWalletId = MainController.getWalletIdByName(incomeEntityStringCellEditEvent.getNewValue());
        double newWalletBalance = MainController.getWalletBalanceById(newWalletId);


        MainController.updateWalletBalanceById(newWalletId, newWalletBalance + amountInCurrentRow);
        incomeModel.doEditWalletField(currentIncomeRowId, newWalletId);

        mainController.updateScreenInfo();
    }

    @SuppressWarnings("Duplicates")
    public void categoryEditCommit(TableColumn.CellEditEvent<IncomeEntity, String> incomeEntityStringCellEditEvent) throws SQLException {
        int currentIncomeRowId = incomeEntityStringCellEditEvent.getRowValue().getId();
        int newIncomeCategoryId = incomeCategoryList.entrySet().stream().
                filter(e -> e.getValue().equals(incomeEntityStringCellEditEvent.getNewValue())).
                findFirst().orElseThrow(() -> {
                    LOGGER.fatal("categoryEditCommit gets null");
                    return new NullPointerException("categoryEditCommit get null");
                }).getKey();
        incomeModel.doEditIncomeCategoryField(currentIncomeRowId, newIncomeCategoryId);
        drawIncomeList();
    }

    @SuppressWarnings("Duplicates")
    public void sumEditCommit(TableColumn.CellEditEvent<IncomeEntity, Double> incomeEntityDoubleCellEditEvent) throws SQLException {
        int walletId = MainController.getWalletIdByName(incomeEntityDoubleCellEditEvent.getRowValue().getWallet_name());
        double walletBalance = MainController.getWalletBalanceById(walletId);
        int currentIncomeRowId = incomeEntityDoubleCellEditEvent.getRowValue().getId();
        double oldAmount = incomeEntityDoubleCellEditEvent.getOldValue();
        double newAmount = incomeEntityDoubleCellEditEvent.getNewValue();
        MainController.updateWalletBalanceById(walletId, walletBalance + (newAmount - oldAmount));
        boolean incomeWasChanged = incomeModel.doEditIncomeAmountField(currentIncomeRowId, newAmount);
        if (!incomeWasChanged) {
            LOGGER.error("Income amount edit error during a change in database.");
            Popup.display("Income amount edit error", "Упс, что то пошло не так, не удалось изменить данные в БД");
        } else {
            drawIncomeList();
            mainController.initBalance();
        }
    }

    public void commentEditCommit(TableColumn.CellEditEvent<IncomeEntity, String> incomeEntityStringCellEditEvent) throws SQLException {
        int currentIncomeRowId = incomeEntityStringCellEditEvent.getRowValue().getId();
        String newText = incomeEntityStringCellEditEvent.getNewValue();
        boolean commentWasChanged = incomeModel.doEditIncomeCommentField(currentIncomeRowId, newText);
        if (!commentWasChanged) {
            LOGGER.error("Income comment edit error during a change in database.");
            Popup.display("Income comment edit error", "Упс, что то пошло не так, не удалось изменить данные в БД");
        }
    }
    @SuppressWarnings("Duplicates")
    public void addNewIncome() throws SQLException {
        incomeCategoryList = MainController.getIncomeCategoryList();
        LocalDate date = newIncomeDate.getValue();
        int walletId = MainController.getWalletList().entrySet().stream().filter(s -> s.getValue().equals(selectNewIncomeWallet.getValue())).
                findFirst().orElseThrow(() -> {
                    LOGGER.fatal("Wallet field gets null");
                    return new NullPointerException("Wallet field gets null");
                }).getKey();
        int categoryId = incomeCategoryList.entrySet().stream().filter(s -> s.getValue().equals(selectNewIncomeCategory.getValue())).
                findFirst().orElseThrow(() -> {
                    LOGGER.fatal("Category field gets null");
                    return new NullPointerException("Category field get null");
                }).getKey();
        double amount = 0;
        try {
            amount = Double.parseDouble(newIncomeAmount.getText());
        } catch (RuntimeException NumberFormatException) {
            Popup.display("Wrong amount", "Вы ввели некорретное число.");
        }
        String comment = newIncomeComment.getText();
        boolean incomeRowWasAdded = incomeModel.addNewIncomeRow(date, walletId, categoryId, amount, comment);
        if (incomeRowWasAdded) {
            drawIncomeList();
            mainController.updateScreenInfo();
        } else {
            Popup.display("Income wasn't added", "Упс, что то пошло не так, запис не была добавлена в БД");
            LOGGER.error("income wasn't added");
        }
    }
    public void updateVisualInformation() {
        incomeWallet.setCellFactory(ChoiceBoxTableCell.forTableColumn(MainController.getWalletObservableList()));
        selectNewIncomeWallet.setItems(MainController.getWalletObservableList());
        selectNewIncomeWallet.setValue(MainController.getDefaultWalletName());
        try {
            selectNewIncomeCategory.setItems(FXCollections.observableArrayList(MainController.getIncomeCategoryList().values()));
            selectNewIncomeCategory.setValue(MainController.getDefaultIncomeCategory());
            incomeCategory.setCellFactory(ChoiceBoxTableCell.forTableColumn(FXCollections.observableArrayList(MainController.getIncomeCategoryList().values())));
            drawIncomeList();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @SuppressWarnings("Duplicates")
    public void deleteRows(KeyEvent keyEvent) throws SQLException {
        if (keyEvent.getCode() == KeyCode.DELETE) {
            ObservableList<IncomeEntity> list = incomeTable.getSelectionModel().getSelectedItems();
            for (IncomeEntity entity : list) {
                if (entity.getWallet_name() != null) {
                    boolean isDeleted = incomeModel.deleteIncome(entity.getId(), MainController.getWalletIdByName(entity.getWallet_name()), entity.getAmount());
                    if (!isDeleted) LOGGER.debug("id: " + entity.getId() + " was failed to delete.");
                } else {
                    incomeModel.justDeleteIncome(entity.getId());
                }

            }
            mainController.initBalance();
            drawIncomeList();
        }
    }
}
