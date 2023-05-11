package ru.viewer.expensesviewer.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.viewer.expensesviewer.HelloApplication;
import ru.viewer.expensesviewer.controller.settings.WalletController;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SettingsController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(IncomeController.class);
    private MainController mainController;
    private WalletController walletController;
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
            walletController = walletLoader.getController();
        } catch (IOException e) {
            LOGGER.fatal("WalletTab.fxml wasn't loaded");
            LOGGER.info(e.getMessage());
            throw new RuntimeException("WalletTab.fxml wasn't loaded");
        }
    }

    public void setMainControllerInit() {
        walletController.setMainController(mainController);
    }
}
