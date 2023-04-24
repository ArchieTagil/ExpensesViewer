package ru.viewer.expensesviewer.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            AnchorPane anchorPaneIncomeTab = new FXMLLoader(getClass().getResource("IncomeTab.fxml")).load();
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
    }
}
