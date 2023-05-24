package ru.viewer.expensesviewer.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.viewer.expensesviewer.HelloApplication;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ReportsController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(IncomeController.class);
    @FXML
    private Tab tables;
    @FXML
    @SuppressWarnings("unused")
    private Tab graphics;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
       try {
            FXMLLoader tablesLoader = new FXMLLoader();
            tablesLoader.setLocation(HelloApplication.class.getResource("reports/tablesTab.fxml"));
            tables.setContent(tablesLoader.load());
        } catch (IOException e) {
            LOGGER.fatal("tablesTab.fxml wasn't loaded");
            LOGGER.info(e.getMessage());
            throw new RuntimeException();
        }
    }
}
