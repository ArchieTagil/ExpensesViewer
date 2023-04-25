package ru.viewer.expensesviewer.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.LocalDateTimeStringConverter;
import ru.viewer.expensesviewer.model.IncomeModel;
import ru.viewer.expensesviewer.model.objects.IncomeEntity;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class IncomeController {

    private IncomeModel incomeModel = new IncomeModel();

    private List<IncomeEntity> incomeEntityList;
    private Map<Integer, String> walletList;
    private ObservableList<String> walletObservableList;


    @FXML
    private TableView<IncomeEntity> incomeTable;
    @FXML
    private TableColumn<IncomeEntity, Integer> incomeId;
    @FXML
    private TableColumn<IncomeEntity, LocalDateTime> incomeDate;
    @FXML
    public TableColumn<IncomeEntity, String> incomeWallet;
    @FXML
    public TableColumn<IncomeEntity, String> incomeCategory;
    @FXML
    public TableColumn<IncomeEntity, Double> incomeSum;
    @FXML
    public TableColumn<IncomeEntity, String> incomeComment;
    @FXML
    public Button incomeAddButton;
    @FXML
    DatePicker newIncomeDate;
    @FXML
    ChoiceBox selectNewIncomeWallet;
    @FXML
    ChoiceBox selectNewIncomeCategory;
    @FXML
    TextField newIncomeAmount;
    @FXML
    TextField newIncomeComment;
    @FXML
    AnchorPane incomeAnchorPane;

    public IncomeController() throws SQLException, ClassNotFoundException {
    }

    @FXML
    public void initialize() throws SQLException {
        System.out.println("Init");
        initHotKeys();
        incomeEntityList = incomeModel.getIncomeList();
        walletList = incomeModel.getWalletList();
        newIncomeDate.setValue(LocalDate.now());
        walletObservableList = FXCollections.observableArrayList(FXCollections.observableArrayList(walletList.values()));
        selectNewIncomeWallet.setItems(walletObservableList);
        selectNewIncomeWallet.setValue("Sberbank");


        incomeTable.setEditable(true);
        incomeId.setCellValueFactory(new PropertyValueFactory<IncomeEntity, Integer>("id"));
        incomeDate.setCellFactory(column -> {
            TableCell<IncomeEntity, LocalDateTime> cell = new TableCell<>() {
                private DateTimeFormatter format = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

                @Override
                protected void updateItem(LocalDateTime localDateTime, boolean b) {
                    super.updateItem(localDateTime, b);
                    if (localDateTime == null) {
                        setText(null);
                    } else {
                        this.setText(localDateTime.format(format));
                    }

                }
            };

            return cell;
        });
        incomeDate.setCellFactory(TextFieldTableCell.forTableColumn(new LocalDateTimeStringConverter()));
        incomeDate.setCellValueFactory(new PropertyValueFactory<>("date"));

        incomeWallet.setCellFactory(ChoiceBoxTableCell.forTableColumn(walletObservableList));
        incomeWallet.setCellValueFactory(new PropertyValueFactory<>("wallet_name"));

        incomeCategory.setCellFactory(TextFieldTableCell.forTableColumn());
        incomeCategory.setCellValueFactory(new PropertyValueFactory<>("income_category"));

        incomeSum.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        incomeSum.setCellValueFactory(new PropertyValueFactory<>("amount"));

        incomeComment.setCellFactory(TextFieldTableCell.forTableColumn());
        incomeComment.setCellValueFactory(new PropertyValueFactory<>("comment"));

        ObservableList observableList = FXCollections.observableArrayList(incomeEntityList);
        System.out.println(observableList);
        incomeTable.setItems(observableList);
    }

    public void initHotKeys() {
        KeyCodeCombination alt1 = new KeyCodeCombination(KeyCode.DIGIT1, KeyCombination.ALT_DOWN);
        KeyCodeCombination ctrlEnter = new KeyCodeCombination(KeyCode.ENTER, KeyCombination.SHORTCUT_DOWN);

        EventHandler filter = new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (ctrlEnter.match(event)) {
                    incomeAddButton.fire();
                } else if (alt1.match(event)) {
                    newIncomeDate.requestFocus();
                }
            }
        };
        incomeAnchorPane.addEventFilter(KeyEvent.KEY_PRESSED, filter);
    }

    public void dateEditCommit(TableColumn.CellEditEvent<IncomeEntity, LocalDateTime> incomeEntityLocalDateTimeCellEditEvent) {
    }

    public void walletEditCommit(TableColumn.CellEditEvent<IncomeEntity, String> incomeEntityStringCellEditEvent) throws SQLException {
        int currentIncomeRowId = incomeEntityStringCellEditEvent.getRowValue().getId();
        int newWalletId = walletList.entrySet().stream().filter(e -> e.getValue().equals(incomeEntityStringCellEditEvent.getNewValue())).findFirst().get().getKey();
        incomeModel.doEditWalletField(currentIncomeRowId, newWalletId);
        initialize();
    }

    public void categoryEditCommit(TableColumn.CellEditEvent<IncomeEntity, String> incomeEntityStringCellEditEvent) {
    }

    public void sumEditCommit(TableColumn.CellEditEvent<IncomeEntity, Double> incomeEntityDoubleCellEditEvent) {
    }

    public void commentEditCommit(TableColumn.CellEditEvent<IncomeEntity, String> incomeEntityStringCellEditEvent) {
    }

    public void addNewIncome(ActionEvent actionEvent) {
        System.out.println("new element was added");
    }
}
