package ru.viewer.expensesviewer;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.viewer.expensesviewer.controller.ExpensesController;
import ru.viewer.expensesviewer.model.Car;

import java.io.IOException;

public class HelloApplication extends Application {
    private Scene scene;
    private ObservableList<Car> list = FXCollections.observableArrayList(
            new Car("Nissan", "Skyline", 350000, 17000.00),
            new Car("Nissan", "Days", 25000, 750000.00),
            new Car("Honda", "NBox", 35000, 950000.00),
            new Car("Suzuki", "Alto", 5000, 550000.00),
            new Car("Honda", "Civic", 312000, 250000.00),
            new Car("Toyota", "Rav 4", 180000, 750000.00),
            new Car("Chevrolet", "Niva", 325000, 270000.00)
    );

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("expenses.fxml"));
        this.scene = new Scene(loader.load());
        ExpensesController controller = loader.getController();
        controller.setApplication(this);
        scene.getStylesheets().add(getClass().getResource("bootstrap3.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    public ObservableList<Car> getList() {
        return list;
    }

    public Scene getScene() {
        return scene;
    }
}