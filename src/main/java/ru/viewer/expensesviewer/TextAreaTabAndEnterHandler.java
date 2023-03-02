package ru.viewer.expensesviewer;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.beans.value.*;
import javafx.collections.ObservableList;
import javafx.event.*;
import javafx.scene.*;
import javafx.scene.control.*;
import static javafx.scene.input.KeyCode.ENTER;
import static javafx.scene.input.KeyCode.TAB;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.*;

public class TextAreaTabAndEnterHandler extends Application {
    final Label status = new Label();

    public static void main(String[] args) { launch(args); }

    @Override public void start(final Stage stage) {
        final TextArea textArea1 = new TabAndEnterIgnoringTextArea();
        final TextArea textArea2 = new TabAndEnterIgnoringTextArea();

        final Button defaultButton = new Button("OK");
        defaultButton.setDefaultButton(true);
        defaultButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent event) {
                status.setText("Default Button Pressed");
            }
        });

        textArea1.textProperty().addListener(new ClearStatusListener());
        textArea2.textProperty().addListener(new ClearStatusListener());

        VBox layout = new VBox(10);
        layout.setStyle("-fx-background-color: cornsilk; -fx-padding: 10px;");
        layout.getChildren().setAll(
                textArea1,
                textArea2,
                defaultButton,
                status
        );

        stage.setScene(
                new Scene(layout)
        );
        stage.show();
    }

    class ClearStatusListener implements ChangeListener<String> {
        @Override public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            status.setText("");
        }
    }

    class TabAndEnterIgnoringTextArea extends TextArea {
        final TextArea myTextArea = this;

        TabAndEnterIgnoringTextArea() {
            addEventFilter(KeyEvent.KEY_PRESSED, new TabAndEnterHandler());
        }

        class TabAndEnterHandler implements EventHandler<KeyEvent> {
            private KeyEvent recodedEvent;

            @Override public void handle(KeyEvent event) {
                if (recodedEvent != null) {
                    recodedEvent = null;
                    return;
                }

                Parent parent = myTextArea.getParent();
                if (parent != null) {
                    switch (event.getCode()) {
                        case ENTER:
                            if (event.isControlDown()) {
                                recodedEvent = recodeWithoutControlDown(event);
                                myTextArea.fireEvent(recodedEvent);
                            } else {
                                Event parentEvent = event.copyFor(parent, parent);
                                myTextArea.getParent().fireEvent(parentEvent);
                            }
                            event.consume();
                            break;

                        case TAB:
                            if (event.isControlDown()) {
                                recodedEvent = recodeWithoutControlDown(event);
                                myTextArea.fireEvent(recodedEvent);
                            } else {
                                ObservableList<Node> children = parent.getChildrenUnmodifiable();
                                int idx = children.indexOf(myTextArea);
                                if (idx >= 0) {
                                    for (int i = idx + 1; i < children.size(); i++) {
                                        if (children.get(i).isFocusTraversable()) {
                                            children.get(i).requestFocus();
                                            break;
                                        }
                                    }
                                    for (int i = 0; i < idx; i++) {
                                        if (children.get(i).isFocusTraversable()) {
                                            children.get(i).requestFocus();
                                            break;
                                        }
                                    }
                                }
                            }
                            event.consume();
                            break;
                    }
                }
            }

            private KeyEvent recodeWithoutControlDown(KeyEvent event) {
                return new KeyEvent(
                        event.getEventType(),
                        event.getCharacter(),
                        event.getText(),
                        event.getCode(),
                        event.isShiftDown(),
                        false,
                        event.isAltDown(),
                        event.isMetaDown()
                );
            }
        }
    }
}