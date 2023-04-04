module ru.viewer.expensesviewer {
    requires javafx.controls;
    requires javafx.fxml;
    requires spring.jdbc;
    requires java.sql;


    opens ru.viewer.expensesviewer to javafx.fxml;
    exports ru.viewer.expensesviewer;
    opens ru.viewer.expensesviewer.model to javafx.fxml;
    exports ru.viewer.expensesviewer.model;
    exports ru.viewer.expensesviewer.controller;
    opens ru.viewer.expensesviewer.controller to javafx.fxml;
}