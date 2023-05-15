package ru.viewer.expensesviewer.model.objects;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Popup {

    public static void display(String title, String text) {
        Stage popUpWindow = new Stage();
        popUpWindow.initModality(Modality.APPLICATION_MODAL);
        popUpWindow.setTitle(title);

        Label label1 = new Label(text);

        Button button1 = new Button("close");
        button1.setOnAction(e-> popUpWindow.close());

        HBox hBox = new HBox();
        hBox.getChildren().addAll(label1, button1);
        hBox.setPadding(new Insets(20, 20, 20, 20));
        hBox.setSpacing(10);
        hBox.setAlignment(Pos.CENTER);
        Scene scene1 = new Scene(hBox);
        popUpWindow.setScene(scene1);
        popUpWindow.showAndWait();
    }
}
