package ru.viewer.expensesviewer.controller;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
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

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(IncomeController.class);
    @FXML
    Tab incomeTab;
    @FXML
    Button exit;
    @FXML
    StackPane mainStackPane;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        KeyCodeCombination altQ = new KeyCodeCombination(KeyCode.Q, KeyCombination.ALT_DOWN);
        EventHandler<KeyEvent> filter = event -> {
            if (altQ.match(event)) {
                exit.fire();
            }
        };
        mainStackPane.addEventFilter(KeyEvent.KEY_PRESSED, filter);

        try {
            AnchorPane anchorPaneIncomeTab = new FXMLLoader(HelloApplication.class.getResource("IncomeTab.fxml")).load();
            incomeTab.setContent(anchorPaneIncomeTab);
        } catch (IOException e) {
            LOGGER.fatal("IncomeTab.fxml wasn't loaded");
        }
    }

    public void exit() {
        System.exit(0);
    }
}
