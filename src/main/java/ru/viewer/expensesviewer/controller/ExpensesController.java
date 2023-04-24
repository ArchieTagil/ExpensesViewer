package ru.viewer.expensesviewer.controller;

import ru.viewer.expensesviewer.HelloApplication;

import java.sql.Connection;

public class ExpensesController {
    Connection connection;

    private HelloApplication application;

//    @FXML
//    public void exit() {
//        System.exit(0);
//    }
//
//    public void setApplication(HelloApplication application) {
//        this.application = application;
//        myTable.setItems(application.getList());
//    }
//
//    @FXML
//    public void initialize() throws SQLException, ClassNotFoundException {
//        IncomeModel incomeModel = new IncomeModel();
//        incomeModel.execute();
//
//        myTable.setEditable(true);
//        myColumn1.setCellFactory(TextFieldTableCell.forTableColumn());
//        myColumn2.setCellFactory(TextFieldTableCell.forTableColumn());
//        myColumn3.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
//        myColumn4.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
//
//        KeyCodeCombination altT = new KeyCodeCombination(KeyCode.T, KeyCombination.ALT_DOWN);
//        KeyCodeCombination ctrlEnter = new KeyCodeCombination(KeyCode.ENTER, KeyCombination.SHORTCUT_DOWN);
//        KeyCodeCombination altEscape = new KeyCodeCombination(KeyCode.ESCAPE, KeyCombination.ALT_DOWN);
//        EventHandler filter = new EventHandler<KeyEvent>() {
//            @Override
//            public void handle(KeyEvent event) {
//                if (altEscape.match(event)) {
//                    exit.fire();
//                } else if (ctrlEnter.match(event)) {
//                    addButton.fire();
//                } else if (altT.match(event)) {
//                    newMake.requestFocus();
//                }
//            }
//        };
//        mainStackPane.addEventFilter(KeyEvent.KEY_PRESSED, filter);
//        myColumn1.setCellValueFactory(new PropertyValueFactory<Car, String>("make"));
//        myColumn2.setCellValueFactory(new PropertyValueFactory<Car, String>("model"));
//        myColumn3.setCellValueFactory(new PropertyValueFactory<Car, Integer>("distance"));
//        myColumn4.setCellValueFactory(new PropertyValueFactory<Car, Double>("price"));
//    }
//    @FXML
//    public void addNewCar() {
//        application.getList().add(
//                new Car(
//                        newMake.getText(),
//                        newModel.getText(),
//                        Integer.parseInt(newDistance.getText()),
//                        Double.parseDouble(newPrice.getText())
//                ));
//    }
//
//    public void setConnection(Connection connection) {
//        this.connection = connection;
//    }
}
