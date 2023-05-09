package ru.viewer.expensesviewer.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.viewer.expensesviewer.HelloApplication;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SettingsController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(IncomeController.class);
    MainController mainController;
    @FXML
    private Tab wallet;
    @FXML
    private Tab income_category;
    @FXML
    private Tab expenses_category;
    @FXML
    private Tab import_export;
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            FXMLLoader walletLoader = new FXMLLoader();
            walletLoader.setLocation(HelloApplication.class.getResource("settings/WalletTab.fxml"));
            wallet.setContent(walletLoader.load());
        } catch (IOException e) {
            LOGGER.fatal("WalletTab.fxml wasn't loaded");
            LOGGER.info(e.getMessage());
            throw new RuntimeException("WalletTab.fxml wasn't loaded");
        }
    }
}
