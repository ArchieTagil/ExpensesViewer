package ru.viewer.expensesviewer.controller.settings;

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
import javafx.util.converter.DoubleStringConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.viewer.expensesviewer.controller.IncomeController;
import ru.viewer.expensesviewer.controller.MainController;
import ru.viewer.expensesviewer.model.DbConnection;
import ru.viewer.expensesviewer.model.objects.Popup;
import ru.viewer.expensesviewer.model.objects.settings.WalletEntity;

import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class WalletController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(IncomeController.class);
    private final Connection connection = DbConnection.getInstance().getConnection();
    private MainController mainController;
    @FXML
    private AnchorPane walletAnchorPane;
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
    @FXML
    private Button walletAdd;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initHotKeys();
        walletListTable.setEditable(true);
        walletListTable.setItems(getWalletEntityList());

        walletListTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        walletId.setCellValueFactory(new PropertyValueFactory<>("walletId"));

        walletName.setCellFactory(TextFieldTableCell.forTableColumn());
        walletName.setCellValueFactory(new PropertyValueFactory<>("walletName"));

        walletBalance.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        walletBalance.setCellValueFactory(new PropertyValueFactory<>("walletBalance"));

        List<Boolean> listTrueFalse = new ArrayList<>();
        listTrueFalse.add(Boolean.TRUE);
        listTrueFalse.add(Boolean.FALSE);
        walletDefault.setCellFactory(ChoiceBoxTableCell.forTableColumn(FXCollections.observableArrayList(listTrueFalse)));
        walletDefault.setCellValueFactory(new PropertyValueFactory<>("walletDefault"));
    }

    public void addNewWallet() {
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO `wallets_list` (wallet_name, wallet_balance, wallet_default) VALUES (?, 0, 0)")) {
            statement.setString(1, newWalletName.getText());
            statement.execute();
            walletListTable.setItems(getWalletEntityList());
            mainController.initSelectLists();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void walletNameEditCommit(TableColumn.CellEditEvent<WalletEntity, String> cellEditEvent) {
        int walletId = cellEditEvent.getRowValue().getWalletId();
        String newName = cellEditEvent.getNewValue();
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "UPDATE `wallets_list` SET `wallet_name` = ? WHERE wallet_id = ?")) {
            preparedStatement.setString(1, newName);
            preparedStatement.setInt(2, walletId);
            preparedStatement.executeUpdate();
            mainController.initSelectLists();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("Duplicates")
    public void walletBalanceEditCommit(TableColumn.CellEditEvent<WalletEntity, Double> cellEditEvent) {
        int walletId = cellEditEvent.getRowValue().getWalletId();
        Double newBalance = cellEditEvent.getNewValue();
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "UPDATE `wallets_list` SET `wallet_balance` = ? WHERE wallet_id = ?")) {
            preparedStatement.setDouble(1, newBalance);
            preparedStatement.setInt(2, walletId);
            preparedStatement.executeUpdate();
            mainController.initSelectLists();
            mainController.initBalance();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void walletDefaultEditCommit(TableColumn.CellEditEvent<WalletEntity, Boolean> cellEditEvent) {
        try (Statement disableCurrentDefaultWallet = connection.createStatement();
             Statement getCountOfTrue = connection.createStatement();
             Statement updateDefault = connection.createStatement()
        ) {
            int id = cellEditEvent.getRowValue().getWalletId();
            String sqlCountOfTrue = "SELECT COUNT(*), wallet_id FROM wallets_list WHERE wallet_default = TRUE";
            ResultSet rs = getCountOfTrue.executeQuery(sqlCountOfTrue);
            rs.next();

            int oldIdDefaultWallet = rs.getInt(2);
            int countOfTrue = rs.getInt(1);
            boolean oldValue = cellEditEvent.getOldValue();
            boolean newValue = cellEditEvent.getNewValue();

            String sqlSetFalse = "UPDATE `wallets_list` SET `wallet_default` = FALSE WHERE wallet_default = " + oldIdDefaultWallet +";";
            String sqlSetTrue = "UPDATE `wallets_list` SET `wallet_default` = " + newValue + " WHERE wallet_id = " + id + ";";

            if (newValue == Boolean.TRUE && oldValue == Boolean.FALSE && countOfTrue == 1) {
                disableCurrentDefaultWallet.executeUpdate(sqlSetFalse);
                updateDefault.executeUpdate(sqlSetTrue);
            } else if (countOfTrue == 0 && newValue == Boolean.FALSE) {
                Popup.display("Ошибка", "Должен быть хотя бы один кошелёк по умолчанию!");
            }
            mainController.initBalance();
            mainController.initSelectLists();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteRows(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.DELETE) {
            ObservableList<WalletEntity> list = walletListTable.getSelectionModel().getSelectedItems();
            for (WalletEntity entity : list) {
                deleteWallet(entity.getWalletId());
            }
            mainController.initBalance();
            walletListTable.setItems(getWalletEntityList());;
        }
    }


    @SuppressWarnings("Duplicates")
    private void initHotKeys() {
        KeyCodeCombination alt1 = new KeyCodeCombination(KeyCode.DIGIT1, KeyCombination.ALT_DOWN);
        KeyCodeCombination ctrlEnter = new KeyCodeCombination(KeyCode.ENTER, KeyCombination.SHORTCUT_DOWN);

        EventHandler<KeyEvent> filter = event -> {
            if (ctrlEnter.match(event)) {
                walletAdd.fire();
            } else if (alt1.match(event)) {
                newWalletName.requestFocus();
            }
        };
        walletAnchorPane.addEventFilter(KeyEvent.KEY_PRESSED, filter);
    }
    private ObservableList<WalletEntity> getWalletEntityList() {
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
            return FXCollections.observableArrayList(walletEntityList);
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
    private void deleteWallet(int id) {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("DELETE FROM `wallets_list` WHERE `wallet_id` = " + id + ";");
            mainController.initSelectLists();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}
