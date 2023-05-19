package ru.viewer.expensesviewer.controller.settings;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import ru.viewer.expensesviewer.controller.MainController;

import java.io.*;

public class ImportExportController {
    private MainController mainController;
    @FXML
    private TextField textFieldExport;
    @FXML
    private TextField textFieldImport;

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
                Process process = Runtime.getRuntime().exec("cmd /C mysqldump -P 3306 -h localhost -u root -p12345678 simpleexpensesmanager");
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                FileWriter fileWriter = new FileWriter(textFieldExport.getText(), true);

                String line;
                while ((line = reader.readLine()) != null) {
                    fileWriter.write(line + "\n");
                }

                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void doImport() {
        if (textFieldImport.getText() != null) {
            try {
                Runtime.getRuntime().exec("cmd.exe /c mysql -u root -p12345678 simpleexpensesmanager < " + textFieldImport.getText());
                mainController.updateScreenInfo();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}
