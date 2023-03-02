module ru.viewer.expensesviewer {
    requires javafx.controls;
    requires javafx.fxml;


    opens ru.viewer.expensesviewer to javafx.fxml;
    exports ru.viewer.expensesviewer;
    opens ru.viewer.expensesviewer.model to javafx.fxml;
    exports ru.viewer.expensesviewer.model;
}