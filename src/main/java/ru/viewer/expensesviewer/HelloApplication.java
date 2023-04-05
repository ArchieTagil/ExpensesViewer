package ru.viewer.expensesviewer;

import javafx.application.Application;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import ru.viewer.expensesviewer.model.Car;

import java.sql.*;

import java.io.IOException;

public class HelloApplication extends Application {

    private static final String URL = "jdbc:mysql://localhost:3306/people";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "12345678";

    private static Connection connection;

//    DriverManagerDataSource dataSource = new DriverManagerDataSource();
//    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
//
//    public HelloApplication() {
//        this.dataSource.setDriverClassName("com.mysql.jdbc.Driver");
//        this.dataSource.setUrl("jdbc:mysql://localhost:3306/people");
//        this.dataSource.setUsername("root");
//        this.dataSource.setPassword("12345678");
//    }

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
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);

            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery("SELECT * FROM person;");

            while (resultSet.next()) {
                System.out.println(resultSet.getString("name"));
            }
            FXMLLoader loader = new FXMLLoader(getClass().getResource("expenses.fxml"));
            this.scene = new Scene(loader.load());
            ExpensesController controller = loader.getController();
            controller.setApplication(this);
            controller.setConnection(connection);
            scene.getStylesheets().add(getClass().getResource("bootstrap3.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
            connection.close();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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