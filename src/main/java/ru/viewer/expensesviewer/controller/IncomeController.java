package ru.viewer.expensesviewer.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.LocalDateTimeStringConverter;
import ru.viewer.expensesviewer.HelloApplication;
import ru.viewer.expensesviewer.model.DbConnection;
import ru.viewer.expensesviewer.model.IncomeModel;
import ru.viewer.expensesviewer.model.objects.IncomeEntity;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class IncomeController {
    Connection connection;
    HelloApplication Application;

    IncomeModel incomeModel = new IncomeModel();

    List<IncomeEntity> incomeEntityList;
    Map<Integer, String> walletList;

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

    public IncomeController() throws SQLException, ClassNotFoundException {
    }


    public void setApplication(HelloApplication helloApplication) throws SQLException, ClassNotFoundException {
        this.Application = helloApplication;
        connection = DbConnection.getInstance().getConnection();
        System.out.println("setApplication");
    }

    @FXML
    public void initialize() throws SQLException {
        System.out.println("Init");
        incomeEntityList = incomeModel.getIncomeList();
        walletList = incomeModel.getWalletList();

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

        ObservableList<String> walletObservableList = FXCollections.observableArrayList(FXCollections.observableArrayList(walletList.values()));
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

    @FXML
    public void addNewCar(ActionEvent actionEvent) {
    }

    @FXML
    public void exit(ActionEvent actionEvent) {
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
}
