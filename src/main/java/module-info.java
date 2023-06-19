module ru.viewer.expensesviewer {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.j;
    requires org.apache.logging.log4j;

    opens ru.viewer.expensesviewer to javafx.fxml;
    exports ru.viewer.expensesviewer;

    opens ru.viewer.expensesviewer.model to javafx.fxml;
    exports ru.viewer.expensesviewer.model;

    opens ru.viewer.expensesviewer.controller to javafx.fxml;
    exports ru.viewer.expensesviewer.controller;

    exports ru.viewer.expensesviewer.model.objects;
    opens ru.viewer.expensesviewer.model.objects to javafx.fxml;

    exports ru.viewer.expensesviewer.controller.settings;
    opens ru.viewer.expensesviewer.controller.settings to javafx.fxml;

    exports ru.viewer.expensesviewer.controller.reports;
    opens ru.viewer.expensesviewer.controller.reports to javafx.fxml;

    exports ru.viewer.expensesviewer.model.objects.settings;
    opens ru.viewer.expensesviewer.model.objects.settings to javafx.fxml;
}