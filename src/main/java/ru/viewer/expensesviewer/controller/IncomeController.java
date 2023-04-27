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
import javafx.util.converter.LocalDateStringConverter;
import ru.viewer.expensesviewer.model.IncomeModel;
import ru.viewer.expensesviewer.model.objects.IncomeEntity;
import ru.viewer.expensesviewer.model.objects.Popup;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

public class IncomeController {

    private final IncomeModel incomeModel = new IncomeModel();

    private List<IncomeEntity> incomeEntityList;
    private Map<Integer, String> walletList;
    private ObservableList<String> walletObservableList;
    private Map<Integer, String> incomeCategoryList;
    private ObservableList<String> incomeCategoryObservableList;
    private TextFormatter<String> textFormatter;


    @FXML
    private TableView<IncomeEntity> incomeTable;
    @FXML
    private TableColumn<IncomeEntity, Integer> incomeId;
    @FXML
    private TableColumn<IncomeEntity, LocalDate> incomeDate;
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
    ChoiceBox<String> selectNewIncomeWallet;
    @FXML
    ChoiceBox<String> selectNewIncomeCategory;
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
        initInsertFieldsSettings();

        incomeTable.setEditable(true);
        incomeId.setCellValueFactory(new PropertyValueFactory<>("id"));
        incomeDate.setCellFactory(column -> {
            TableCell<IncomeEntity, LocalDate> cell = new TableCell<>() {
                private final DateTimeFormatter format = DateTimeFormatter.ofPattern("dd.MM.yyyy");

                @Override
                protected void updateItem(LocalDate localDate, boolean b) {
                    super.updateItem(localDate, b);
                    if (localDate == null) {
                        setText(null);
                    } else {
                        this.setText(localDate.format(format));
                    }

                }
            };

            return cell;
        });
        incomeDate.setCellFactory(TextFieldTableCell.forTableColumn(new LocalDateStringConverter()));
        incomeDate.setCellValueFactory(new PropertyValueFactory<>("date"));

        incomeWallet.setCellFactory(ChoiceBoxTableCell.forTableColumn(walletObservableList));
        incomeWallet.setCellValueFactory(new PropertyValueFactory<>("wallet_name"));

        incomeCategory.setCellFactory(ChoiceBoxTableCell.forTableColumn(incomeCategoryObservableList));
        incomeCategory.setCellValueFactory(new PropertyValueFactory<>("income_category"));

        incomeSum.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        incomeSum.setCellValueFactory(new PropertyValueFactory<>("amount"));

        incomeComment.setCellFactory(TextFieldTableCell.forTableColumn());
        incomeComment.setCellValueFactory(new PropertyValueFactory<>("comment"));

        ObservableList<IncomeEntity> observableList = FXCollections.observableArrayList(incomeEntityList);
        System.out.println(observableList);
        incomeTable.setItems(observableList);
    }

    public void initHotKeys() {
        KeyCodeCombination alt1 = new KeyCodeCombination(KeyCode.DIGIT1, KeyCombination.ALT_DOWN);
        KeyCodeCombination ctrlEnter = new KeyCodeCombination(KeyCode.ENTER, KeyCombination.SHORTCUT_DOWN);

        EventHandler<KeyEvent> filter = new EventHandler<KeyEvent>() {
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

    public void initInsertFieldsSettings() throws SQLException {
        newIncomeDate.setValue(LocalDate.now());
        walletList = incomeModel.getWalletList();
        incomeCategoryList = incomeModel.getIncomeCategoryList();

        walletObservableList = FXCollections.observableArrayList(walletList.values());
        incomeCategoryObservableList = FXCollections.observableArrayList(incomeCategoryList.values());

        selectNewIncomeWallet.setItems(walletObservableList);
        selectNewIncomeCategory.setItems(incomeCategoryObservableList);

        selectNewIncomeWallet.setValue(incomeModel.getDefaultWallet());
        selectNewIncomeCategory.setValue(incomeModel.getDefaultIncomeCategory());

        UnaryOperator<TextFormatter.Change> filter = change -> {
            String text = change.getText();

            if (text.matches("[0-9,/.]*")) {
                return change;
            }
            return null;
        };
        textFormatter = new TextFormatter<String>(filter);
        newIncomeAmount.setTextFormatter(textFormatter);
    }

    public void dateEditCommit(TableColumn.CellEditEvent<IncomeEntity, LocalDate> incomeEntityLocalDateCellEditEvent) throws SQLException {
        LocalDate newDate = incomeEntityLocalDateCellEditEvent.getNewValue();
        int id = incomeEntityLocalDateCellEditEvent.getRowValue().getId();
        incomeModel.updateIncomeDate(id, newDate);
        initialize();
    }

    public void walletEditCommit(TableColumn.CellEditEvent<IncomeEntity, String> incomeEntityStringCellEditEvent) throws SQLException {
        int currentIncomeRowId = incomeEntityStringCellEditEvent.getRowValue().getId();
        int newWalletId = walletList.entrySet().stream().filter(e -> e.getValue().equals(incomeEntityStringCellEditEvent.getNewValue())).findFirst().get().getKey();
        incomeModel.doEditWalletField(currentIncomeRowId, newWalletId);
        initialize();
    }

    public void categoryEditCommit(TableColumn.CellEditEvent<IncomeEntity, String> incomeEntityStringCellEditEvent) throws SQLException {
        int currentIncomeRowId = incomeEntityStringCellEditEvent.getRowValue().getId();
        int newIncomeCategoryId = incomeCategoryList.entrySet().stream().filter(e -> e.getValue().equals(incomeEntityStringCellEditEvent.getNewValue())).findFirst().get().getKey();
        incomeModel.doEditIncomeCategoryField(currentIncomeRowId, newIncomeCategoryId);
        initialize();
    }

    public void sumEditCommit(TableColumn.CellEditEvent<IncomeEntity, Double> incomeEntityDoubleCellEditEvent) {
    }

    public void commentEditCommit(TableColumn.CellEditEvent<IncomeEntity, String> incomeEntityStringCellEditEvent) {
    }

    public void addNewIncome(ActionEvent actionEvent) throws SQLException {
        LocalDate date = newIncomeDate.getValue();
        int walletId = walletList.entrySet().stream().filter(s -> s.getValue() == selectNewIncomeWallet.getValue()).findFirst().get().getKey();
        int categoryId = incomeCategoryList.entrySet().stream().filter(s -> s.getValue() == selectNewIncomeCategory.getValue()).findFirst().get().getKey();
        double amount = 0;
        try {
            amount = Double.parseDouble(newIncomeAmount.getText());
        } catch (RuntimeException NumberFormatException) {
            Popup.display("Wrong amount", "Вы ввели некорретное число.");
        }
        String comment = newIncomeComment.getText();
        boolean incomeRowWasAdded = incomeModel.addNewIncomeRow(date, walletId, categoryId, amount, comment);
        if (incomeRowWasAdded == true) {
            initialize();
            System.out.println("income was successfully added");
        } else {
            System.out.println("income wasn't added");
        }
    }
}
