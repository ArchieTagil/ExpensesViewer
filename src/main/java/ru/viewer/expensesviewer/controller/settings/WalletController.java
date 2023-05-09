package ru.viewer.expensesviewer.controller.settings;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyEvent;
import javafx.util.converter.BooleanStringConverter;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.viewer.expensesviewer.controller.IncomeController;
import ru.viewer.expensesviewer.controller.MainController;
import ru.viewer.expensesviewer.model.DbConnection;
import ru.viewer.expensesviewer.model.objects.settings.WalletEntity;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class WalletController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(IncomeController.class);
    private final Connection connection = DbConnection.getInstance().getConnection();
    @FXML
    private TableView<WalletEntity> walletListTable;
    @FXML
    private TableColumn<WalletEntity, Integer> walletId;
    @FXML
    private TableColumn<WalletEntity, String> walletName;
    @FXML
    private TableColumn<WalletEntity, Double> walletBalance;
    @FXML
    private TableColumn<WalletEntity, Boolean> walletDefault;
    @FXML
    private TextField newWalletName;

    public void addNewWallet(ActionEvent actionEvent) {
    }

    public void walletEditCommit(TableColumn.CellEditEvent cellEditEvent) {
    }

    public void deleteRows(KeyEvent keyEvent) {
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        walletId.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        walletId.setCellValueFactory(new PropertyValueFactory<>("walletId"));

        walletName.setCellFactory(TextFieldTableCell.forTableColumn());
        walletName.setCellValueFactory(new PropertyValueFactory<>("walletName"));

        walletBalance.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        walletBalance.setCellValueFactory(new PropertyValueFactory<>("walletBalance"));

        walletDefault.setCellFactory(TextFieldTableCell.forTableColumn(new BooleanStringConverter()));
        walletDefault.setCellValueFactory(new PropertyValueFactory<>("walletDefault"));

        walletListTable.setItems(FXCollections.observableArrayList(getWalletEntityList()));
    }

    private List<WalletEntity> getWalletEntityList() {
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM wallets_list");
            List<WalletEntity> walletEntityList = new ArrayList<>();
            while (resultSet.next()) {
                walletEntityList.add(new WalletEntity(
                        resultSet.getInt("wallet_id"),
                        resultSet.getString("wallet_name"),
                        resultSet.getDouble("wallet_balance"),
                        resultSet.getBoolean("wallet_default")
                ));
            }
            return walletEntityList;
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
