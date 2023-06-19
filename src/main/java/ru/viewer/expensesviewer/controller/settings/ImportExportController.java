package ru.viewer.expensesviewer.controller.settings;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.viewer.expensesviewer.controller.MainController;
import ru.viewer.expensesviewer.model.DbConnection;

import java.io.*;

public class ImportExportController {
    private static final Logger LOGGER = LogManager.getLogger(ImportExportController.class);
    private MainController mainController;
    @FXML
    private TextField textFieldExport;
    @FXML
    private TextField textFieldImport;
    @FXML
    private Label exportResult;
    @FXML
    private Label importResult;

    public void fileChooserExport() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choice path to export");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("SQL file type:", "*.sql"));
        File file = fileChooser.showSaveDialog(mainController.getStage());
        if (file != null) textFieldExport.setText(file.getPath());
    }

    public void fileChooserImport() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select file to import");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("SQL file type:", "*.sql"));
        File file = fileChooser.showOpenDialog(mainController.getStage());
        if (file != null) textFieldImport.setText(file.getPath());
    }

    public void doExport() {
        if (textFieldExport.getText() != null) {
            try {
                //Process process = Runtime.getRuntime().exec("cmd /C mysqldump -P 3306 -h localhost -u root -p12345678 simpleexpensesmanager");
                Process process = Runtime.getRuntime().exec(DbConnection.getConfig().getProperty("exportCommand"));
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                FileWriter fileWriter = new FileWriter(textFieldExport.getText(), true);

                String line;
                while ((line = reader.readLine()) != null) {
                    fileWriter.write(line + "\n");
                }

                fileWriter.flush();
                fileWriter.close();

                exportResult.setTextFill(Color.GREEN);
                exportResult.setText("Успех!");
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }

    public void doImport() {
        if (textFieldImport.getText() != null) {
            try {
                //Process process = Runtime.getRuntime().exec("cmd.exe /c mysql -u root -p12345678 simpleexpensesmanager" + " < " + textFieldImport.getText());
                Process process = Runtime.getRuntime().exec(DbConnection.getConfig().getProperty("importCommand") + " < " + textFieldImport.getText());
                int processStatus = process.waitFor();
                if (processStatus == 0) {
                    importResult.setTextFill(Color.GREEN);
                    importResult.setText("Успех!");
                }
                mainController.updateScreenInfo();
            } catch (IOException | InterruptedException e) {
                LOGGER.error(e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}
