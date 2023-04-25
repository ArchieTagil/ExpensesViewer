package ru.viewer.expensesviewer.controller;

import javafx.event.ActionEvent;
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
import ru.viewer.expensesviewer.HelloApplication;
import ru.viewer.expensesviewer.model.DbConnection;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    private Connection connection;
    private HelloApplication Application;
    @FXML
    Tab incomeTab;
    @FXML
    Button exit;
    @FXML
    StackPane mainStackPane;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        KeyCodeCombination altQ = new KeyCodeCombination(KeyCode.Q, KeyCombination.ALT_DOWN);
        EventHandler filter = new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (altQ.match(event)) {
                    exit.fire();
                }
            }
        };
        mainStackPane.addEventFilter(KeyEvent.KEY_PRESSED, filter);

        try {
            AnchorPane anchorPaneIncomeTab = new FXMLLoader(HelloApplication.class.getResource("IncomeTab.fxml")).load();
            incomeTab.setContent(anchorPaneIncomeTab);
        } catch (IOException e) {
            System.out.println("File not found");
        }
    }

    public void setApplication(HelloApplication helloApplication) throws SQLException, ClassNotFoundException {
        this.Application = helloApplication;
        connection = DbConnection.getInstance().getConnection();
        System.out.println("setApplication");
    }

    public void exit(ActionEvent actionEvent) {
        System.exit(0);
    }
}
