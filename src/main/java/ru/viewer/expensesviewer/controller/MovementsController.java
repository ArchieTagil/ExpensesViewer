package ru.viewer.expensesviewer.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
import ru.viewer.expensesviewer.model.MovementsModel;
import ru.viewer.expensesviewer.model.objects.MovementEntity;
import ru.viewer.expensesviewer.model.objects.Popup;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class MovementsController implements Initializable {
    private MainController mainController;
    private final MovementsModel movementsModel = new MovementsModel();
    private static final Logger LOGGER = LogManager.getLogger(MovementsController.class);
    private ObservableList<String> walletObservableList;

    @FXML
    private AnchorPane movementsAnchorPane;
    @FXML
    private TableView<MovementEntity> movementsTable;
    @FXML
    private TableColumn<MovementEntity, Integer> movementId;
    @FXML
    private TableColumn<MovementEntity, LocalDate> movementDate;
    @FXML
    private TableColumn<MovementEntity, String> sourceWallet;
    @FXML
    private TableColumn<MovementEntity, String> destinationWallet;
    @FXML
    private TableColumn<MovementEntity, Double> movementAmount;
    @FXML
    private TableColumn<MovementEntity, String> movementComment;
    @FXML
    private DatePicker movementDateNewRow;
    @FXML
    private ChoiceBox<String> sourceWalletNewRow;
    @FXML
    private ChoiceBox<String> destinationWalletNewRow;
    @FXML
    private TextField movementAmountNewRow;
    @FXML
    private TextField movementCommentNewRow;
    @FXML
    private Button movementAddNewButton;

    @SuppressWarnings("Duplicates")
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initHotKeys();

        initInsertFieldsSettings();
        drawMovementsList();

        movementsTable.setEditable(true);
        movementsTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        movementId.setCellValueFactory(new PropertyValueFactory<>("id"));

        movementDate.setCellFactory(MainController.dateCallbackForMovements);
        movementDate.setCellFactory(TextFieldTableCell.forTableColumn(new LocalDateStringConverter()));
        movementDate.setCellValueFactory(new PropertyValueFactory<>("date"));

        sourceWallet.setCellFactory(ChoiceBoxTableCell.forTableColumn(walletObservableList));
        sourceWallet.setCellValueFactory(new PropertyValueFactory<>("wallet_debit_name"));

        destinationWallet.setCellFactory(ChoiceBoxTableCell.forTableColumn(walletObservableList));
        destinationWallet.setCellValueFactory(new PropertyValueFactory<>("wallet_credit_name"));

        movementAmount.setCellFactory(MainController.amountCallbackForMovements);
        movementAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));

        movementComment.setCellFactory(TextFieldTableCell.forTableColumn());
        movementComment.setCellValueFactory(new PropertyValueFactory<>("comment"));
    }
    @SuppressWarnings("Duplicates")
    public void deleteRows(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.DELETE) {
            ObservableList<MovementEntity> list = movementsTable.getSelectionModel().getSelectedItems();
            for (MovementEntity entity : list) {
                boolean isDeleted = movementsModel.deleteMovement(
                        entity.getId(),
                        MainController.getWalletIdByName(entity.getWallet_debit_name()),
                        MainController.getWalletIdByName(entity.getWallet_credit_name()),
                        entity.getAmount());
                if (!isDeleted) LOGGER.debug("id: " + entity.getId() + " was failed to delete.");
            }
            mainController.initBalance();
            drawMovementsList();
        }
    }
    public void dateEditCommit(TableColumn.CellEditEvent<MovementEntity, LocalDate> cellEditEvent) {
        LocalDate newDate = cellEditEvent.getNewValue();
        int id = cellEditEvent.getRowValue().getId();
        movementsModel.updateExpenseRowDate(id, newDate);
        drawMovementsList();
    }
    @SuppressWarnings("Duplicates")
    public void walletSourceEditCommit(TableColumn.CellEditEvent<MovementEntity, String> cellEditEvent) {
        int currentMovementRowId = cellEditEvent.getRowValue().getId();
        double amountInCurrentRow = cellEditEvent.getRowValue().getAmount();

        int oldSourceWalletId = MainController.getWalletIdByName(cellEditEvent.getOldValue());
        int newSourceWalletId = MainController.getWalletIdByName(cellEditEvent.getNewValue());

        double oldWalletBalance = MainController.getWalletBalanceById(oldSourceWalletId);
        double newWalletBalance = MainController.getWalletBalanceById(newSourceWalletId);

        MainController.updateWalletBalanceById(oldSourceWalletId, oldWalletBalance + amountInCurrentRow); //в источник возвращаем сумму
        MainController.updateWalletBalanceById(newSourceWalletId, newWalletBalance - amountInCurrentRow); //из нового кошелька вычитаем сумму в пользу кошелька назначения

        movementsModel.doEditWalletField(currentMovementRowId, newSourceWalletId, "wallet_debit_id");
        drawMovementsList();
        mainController.initBalance();
    }
    @SuppressWarnings("Duplicates")
    public void walletDestinationEditCommit(TableColumn.CellEditEvent<MovementEntity, String> cellEditEvent) {
        int currentMovementRowId = cellEditEvent.getRowValue().getId();
        double amountInCurrentRow = cellEditEvent.getRowValue().getAmount();

        int oldDestinationWalletId = MainController.getWalletIdByName(cellEditEvent.getOldValue());
        int newDestinationWalletId = MainController.getWalletIdByName(cellEditEvent.getNewValue());

        double oldWalletBalance = MainController.getWalletBalanceById(oldDestinationWalletId);
        double newWalletBalance = MainController.getWalletBalanceById(newDestinationWalletId);

        MainController.updateWalletBalanceById(oldDestinationWalletId, oldWalletBalance - amountInCurrentRow);
        MainController.updateWalletBalanceById(newDestinationWalletId, newWalletBalance + amountInCurrentRow);

        movementsModel.doEditWalletField(currentMovementRowId, newDestinationWalletId, "wallet_credit_id");
        drawMovementsList();
        mainController.initBalance();
    }
    @SuppressWarnings("Duplicates")
    public void sumEditCommit(TableColumn.CellEditEvent<MovementEntity, Double> cellEditEvent) {
        int currentMovementRowId = cellEditEvent.getRowValue().getId();

        double oldAmountInCurrentRow = cellEditEvent.getOldValue();
        double newAmountInCurrentRow = cellEditEvent.getNewValue();
        double difference = newAmountInCurrentRow - oldAmountInCurrentRow;

        int sourceWalletId = MainController.getWalletIdByName(cellEditEvent.getRowValue().getWallet_debit_name());
        int destinationWalletId = MainController.getWalletIdByName(cellEditEvent.getRowValue().getWallet_credit_name());

        double sourceWalletBalance = MainController.getWalletBalanceById(sourceWalletId);
        double destinationWalletBalance = MainController.getWalletBalanceById(destinationWalletId);

        MainController.updateWalletBalanceById(sourceWalletId, sourceWalletBalance - difference);
        MainController.updateWalletBalanceById(destinationWalletId, destinationWalletBalance + difference);

        boolean amountWasChanged = movementsModel.doEditMovementAmountField(currentMovementRowId, newAmountInCurrentRow);
        if (!amountWasChanged) {
            LOGGER.error("Movement amount edit error during a change in database.");
            Popup.display("Movement amount edit error", "Упс, что то пошло не так, не удалось изменить данные в БД");
        } else {
            drawMovementsList();
            mainController.initBalance();
        }
    }
    public void commentEditCommit(TableColumn.CellEditEvent<MovementEntity, String> cellEditEvent) {
        int currentMovementRowId = cellEditEvent.getRowValue().getId();
        String newText = cellEditEvent.getNewValue();
        boolean commentWasChanged = movementsModel.doEditMovementCommentField(currentMovementRowId, newText);
        if (!commentWasChanged) {
            LOGGER.error("Movement comment edit error during a change in database.");
            Popup.display("Movement comment edit error", "Упс, что то пошло не так, не удалось изменить данные в БД");
        }
    }
    @SuppressWarnings("Duplicates")
    public void addNewMovement() {
        LocalDate date = movementDateNewRow.getValue();
        int sourceWalletId = MainController.getWalletIdByName(sourceWalletNewRow.getValue());
        int destinationWalletId = MainController.getWalletIdByName(destinationWalletNewRow.getValue());

        double amount = 0;
        try {
            amount = Double.parseDouble(movementAmountNewRow.getText());
        } catch (RuntimeException NumberFormatException) {
            Popup.display("Wrong amount", "Вы ввели некорретное число.");
        }
        String comment = movementCommentNewRow.getText();
        boolean incomeRowWasAdded = movementsModel.addNewMovement(date, sourceWalletId, destinationWalletId, amount, comment);
        if (incomeRowWasAdded) {
            drawMovementsList();
            mainController.initBalance();
        } else {
            Popup.display("Income wasn't added", "Упс, что то пошло не так, запис не была добавлена в БД");
            LOGGER.error("income wasn't added");
        }
    }
    @SuppressWarnings("Duplicates")
    private void initInsertFieldsSettings() {
        movementDateNewRow.setValue(LocalDate.now());

        walletObservableList = FXCollections.observableArrayList(MainController.getWalletList().values());

        sourceWalletNewRow.setItems(walletObservableList);
        destinationWalletNewRow.setItems(walletObservableList);

        sourceWalletNewRow.setValue(MainController.getDefaultWalletName());
        destinationWalletNewRow.setValue(MainController.getDefaultWalletName());

        movementAmountNewRow.setTextFormatter(MainController.getOnlyDigitsTextFormatter());
    }
    private void drawMovementsList() {
        List<MovementEntity> movementEntityList = movementsModel.getMovementsList();
        ObservableList<MovementEntity> observableList = FXCollections.observableArrayList(movementEntityList);
        movementsTable.setItems(observableList);
    }
    public void updateLists() {
        sourceWallet.setCellFactory(ChoiceBoxTableCell.forTableColumn(MainController.getWalletObservableList()));
        destinationWallet.setCellFactory(ChoiceBoxTableCell.forTableColumn(MainController.getWalletObservableList()));

        sourceWalletNewRow.setItems(MainController.getWalletObservableList());
        sourceWalletNewRow.setValue(MainController.getDefaultWalletName());

        destinationWalletNewRow.setItems(MainController.getWalletObservableList());
        destinationWalletNewRow.setValue(MainController.getDefaultWalletName());
    }
    @SuppressWarnings("Duplicates")
    private void initHotKeys() {
        KeyCodeCombination alt1 = new KeyCodeCombination(KeyCode.DIGIT1, KeyCombination.ALT_DOWN);
        KeyCodeCombination ctrlEnter = new KeyCodeCombination(KeyCode.ENTER, KeyCombination.SHORTCUT_DOWN);

        EventHandler<KeyEvent> filter = event -> {
            if (ctrlEnter.match(event)) {
                movementAddNewButton.fire();
            } else if (alt1.match(event)) {
                movementDateNewRow.requestFocus();
            }
        };
        movementsAnchorPane.addEventFilter(KeyEvent.KEY_PRESSED, filter);
    }
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}
