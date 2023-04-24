package ru.viewer.expensesviewer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.viewer.expensesviewer.controller.MainController;

import java.io.IOException;
import java.sql.SQLException;

public class HelloApplication extends Application {
    private Scene scene;

    @Override
    public void start(Stage stage) throws IOException, SQLException, ClassNotFoundException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("expenses.fxml"));
        this.scene = new Scene(loader.load());
        MainController controller = loader.getController();
        controller.setApplication(this);
        scene.getStylesheets().add(getClass().getResource("bootstrap3.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}