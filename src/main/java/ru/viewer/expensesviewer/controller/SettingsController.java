package ru.viewer.expensesviewer.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.viewer.expensesviewer.HelloApplication;
import ru.viewer.expensesviewer.controller.settings.ExpensesCategoryController;
import ru.viewer.expensesviewer.controller.settings.IncomeCategoryController;
import ru.viewer.expensesviewer.controller.settings.WalletController;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SettingsController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(IncomeController.class);
    private MainController mainController;
    private WalletController walletController;
    private IncomeCategoryController incomeCategoryController;
    private ExpensesCategoryController expensesCategoryController;
    @FXML
    private Tab wallet;
    @FXML
    private Tab income_category;
    @FXML
    private Tab expenses_category;
    @FXML
    @SuppressWarnings("unused")
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

        try {
            FXMLLoader IncomeCategoryLoader = new FXMLLoader();
            IncomeCategoryLoader.setLocation(HelloApplication.class.getResource("settings/IncomeCategoryTab.fxml"));
            income_category.setContent(IncomeCategoryLoader.load());
            incomeCategoryController = IncomeCategoryLoader.getController();
        } catch (IOException e) {
            LOGGER.fatal("IncomeCategoryTab.fxml wasn't loaded");
            LOGGER.info(e.getMessage());
            throw new RuntimeException("IncomeCategoryTab.fxml wasn't loaded");
        }

        try {
            FXMLLoader ExpensesCategoryLoader = new FXMLLoader();
            ExpensesCategoryLoader.setLocation(HelloApplication.class.getResource("settings/ExpensesCategoryTab.fxml"));
            expenses_category.setContent(ExpensesCategoryLoader.load());
            expensesCategoryController = ExpensesCategoryLoader.getController();
        } catch (IOException e) {
            LOGGER.fatal("ExpensesCategoryTab.fxml wasn't loaded");
            LOGGER.info(e.getMessage());
            throw new RuntimeException("ExpensesCategoryTab.fxml wasn't loaded");
        }
    }

    public WalletController getWalletController() {
        return walletController;
    }

    public IncomeCategoryController getIncomeCategoryController() {
        return incomeCategoryController;
    }

    public ExpensesCategoryController getExpensesCategoryController() {
        return expensesCategoryController;
    }

    public void setMainControllerInit() {
        walletController.setMainController(mainController);
        incomeCategoryController.setMainController(mainController);
        expensesCategoryController.setMainController(mainController);
    }
}
