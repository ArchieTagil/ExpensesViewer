package ru.viewer.expensesviewer.controller;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.viewer.expensesviewer.HelloApplication;
import ru.viewer.expensesviewer.model.MainModel;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(IncomeController.class);
    private static final MainModel mainModel = new MainModel();
    @FXML
    private Tab incomeTab;
    @FXML
    private Button exit;
    @FXML
    private StackPane mainStackPane;
    @FXML
    public Label displayWalletName;
    @FXML
    public Label displayWalletBalance;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initBalance();
        KeyCodeCombination altQ = new KeyCodeCombination(KeyCode.Q, KeyCombination.ALT_DOWN);
        EventHandler<KeyEvent> filter = event -> {
            if (altQ.match(event)) {
                exit.fire();
            }
        };
        mainStackPane.addEventFilter(KeyEvent.KEY_PRESSED, filter);

        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(HelloApplication.class.getResource("IncomeTab.fxml"));
            AnchorPane anchorPaneIncomeTab = loader.load();
            incomeTab.setContent(anchorPaneIncomeTab);
            IncomeController incomeController = loader.getController();
            incomeController.setMainController(this);
        } catch (IOException e) {
            LOGGER.fatal("IncomeTab.fxml wasn't loaded");
            throw new RuntimeException("IncomeTab.fxml wasn't loaded");
        }
    }

    public void initBalance() {
        String defaultWalletName = mainModel.getDefaultWalletName();
        double defaultWalletBalance = mainModel.defaultWalletBalance();
        displayWalletName.setText(defaultWalletName);
        displayWalletBalance.setText(String.valueOf(defaultWalletBalance));

    }

    public void exit() {
        System.exit(0);
    }
}
