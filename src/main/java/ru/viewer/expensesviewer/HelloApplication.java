package ru.viewer.expensesviewer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.viewer.expensesviewer.controller.IncomeController;
import ru.viewer.expensesviewer.controller.MainController;
import ru.viewer.expensesviewer.model.DbConnection;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;

public class HelloApplication extends Application {
    private static final Logger LOGGER = LogManager.getLogger(IncomeController.class);
    @Override
    @SuppressWarnings("ConstantConditions")
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainView.fxml"));
        Scene scene = new Scene(loader.load());
        try {
            scene.getStylesheets().add(getClass().getResource("bootstrap3.css").toExternalForm());
        } catch (NullPointerException e) {
            LOGGER.error("bootstrap3.css wasn't upload");
        }
        MainController mainController = loader.getController();
        mainController.setStage(stage);
        new Thread(() -> {
            try {
                while (true) {
                    Statement st = DbConnection.getInstance().getConnection().createStatement();
                    st.executeQuery("SELECT 1;");
                    Thread.sleep(180000);
                }
            } catch (SQLException | InterruptedException e) {
                LOGGER.debug("Shadow thread which helps connection to be alive was crashed");
                throw new RuntimeException(e);
            }
        }).start();
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}