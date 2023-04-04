package ru.viewer.expensesviewer;

import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.*;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;
import ru.viewer.expensesviewer.model.Car;

import java.security.Key;
import java.sql.Connection;

public class ExpensesController {
    Connection connection;
    @FXML
    private TableView<Car> myTable;
    @FXML
    private TableColumn<Car, String> myColumn1;
    @FXML
    private TableColumn<Car, String> myColumn2;
    @FXML
    private TableColumn<Car, Integer> myColumn3;
    @FXML
    private TableColumn<Car, Double> myColumn4;

    @FXML
    private TextField newMake;
    @FXML
    private TextField newModel;
    @FXML
    private TextField newDistance;
    @FXML
    private TextField newPrice;
    @FXML
    private Button addButton;
    @FXML
    private StackPane mainStackPane;
    @FXML
    private Button exit;

    private HelloApplication application;

    @FXML
    public void exit() {
        System.exit(0);
    }

    public void setApplication(HelloApplication application) {
        this.application = application;
        myTable.setItems(application.getList());
    }

    @FXML
    public void initialize() {
        myTable.setEditable(true);
        myColumn1.setCellFactory(TextFieldTableCell.forTableColumn());
        myColumn2.setCellFactory(TextFieldTableCell.forTableColumn());
        myColumn3.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        myColumn4.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));



        KeyCodeCombination altT = new KeyCodeCombination(KeyCode.T, KeyCombination.ALT_DOWN);
        KeyCodeCombination ctrlEnter = new KeyCodeCombination(KeyCode.ENTER, KeyCombination.SHORTCUT_DOWN);
        KeyCodeCombination altEscape = new KeyCodeCombination(KeyCode.ESCAPE, KeyCombination.ALT_DOWN);
        EventHandler filter = new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (altEscape.match(event)) {
                    exit.fire();
                } else if (ctrlEnter.match(event)) {
                    addButton.fire();
                } else if (altT.match(event)) {
                    newMake.requestFocus();
                }
            }
        };
        mainStackPane.addEventFilter(KeyEvent.KEY_PRESSED, filter);
        myColumn1.setCellValueFactory(new PropertyValueFactory<Car, String>("make"));
        myColumn2.setCellValueFactory(new PropertyValueFactory<Car, String>("model"));
        myColumn3.setCellValueFactory(new PropertyValueFactory<Car, Integer>("distance"));
        myColumn4.setCellValueFactory(new PropertyValueFactory<Car, Double>("price"));
    }
    @FXML
    public void addNewCar() {
        application.getList().add(
                new Car(
                        newMake.getText(),
                        newModel.getText(),
                        Integer.parseInt(newDistance.getText()),
                        Double.parseDouble(newPrice.getText())
                ));
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }
}
