package ru.viewer.expensesviewer.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.viewer.expensesviewer.HelloApplication;
import ru.viewer.expensesviewer.controller.reports.TablesController;
import ru.viewer.expensesviewer.controller.settings.ExpensesCategoryController;
import ru.viewer.expensesviewer.controller.settings.IncomeCategoryController;
import ru.viewer.expensesviewer.controller.settings.WalletController;
import ru.viewer.expensesviewer.model.MainModel;
import ru.viewer.expensesviewer.model.objects.ExpenseEntity;
import ru.viewer.expensesviewer.model.objects.IncomeEntity;
import ru.viewer.expensesviewer.model.objects.MovementEntity;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

public class MainController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(IncomeController.class);

    private Stage stage;
    private static final MainModel mainModel = new MainModel();
    private IncomeController incomeController;
    private ExpensesController expensesController;
    private MovementsController movementsController;
    private ReportsController reportsController;
    private SettingsController settingsController;
    @FXML
    private Tab expensesTab;
    @FXML
    private Tab incomeTab;
    @FXML
    private Tab movementsTab;
    @FXML
    private Tab reportsTab;
    @FXML
    private Tab settingsTab;
    @FXML
    private Button exit;
    @FXML
    private StackPane mainStackPane;
    @FXML
    public Label displayWalletName;
    @FXML
    public Label displayWalletBalance;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initBalance();
        KeyCodeCombination altQ = new KeyCodeCombination(KeyCode.Q, KeyCombination.ALT_DOWN);
        EventHandler<KeyEvent> filter = event -> {
            if (altQ.match(event)) {
                exit.fire();
            }
        };
        mainStackPane.addEventFilter(KeyEvent.KEY_PRESSED, filter);

        try {
            FXMLLoader incomeLoader = new FXMLLoader();
            incomeLoader.setLocation(HelloApplication.class.getResource("IncomeTab.fxml"));
            AnchorPane anchorPaneIncomeTab = incomeLoader.load();
            incomeTab.setContent(anchorPaneIncomeTab);
            incomeController = incomeLoader.getController();
            incomeController.setMainController(this);
        } catch (IOException e) {
            LOGGER.fatal("IncomeTab.fxml wasn't loaded");
            throw new RuntimeException("IncomeTab.fxml wasn't loaded");
        }

        try {
            FXMLLoader expensesLoader = new FXMLLoader();
            expensesLoader.setLocation(HelloApplication.class.getResource("ExpensesTab.fxml"));
            AnchorPane anchorPaneExpensesTab = expensesLoader.load();
            expensesTab.setContent(anchorPaneExpensesTab);
            expensesController = expensesLoader.getController();
            expensesController.setMainController(this);
        } catch (IOException e) {
            LOGGER.fatal("ExpensesTab.fxml wasn't loaded");
            throw new RuntimeException(e);
        }

        try {
            FXMLLoader movementsLoader = new FXMLLoader();
            movementsLoader.setLocation(HelloApplication.class.getResource("MovementsTab.fxml"));
            AnchorPane anchorPaneMovementsTab = movementsLoader.load();
            movementsTab.setContent(anchorPaneMovementsTab);
            movementsController = movementsLoader.getController();
            movementsController.setMainController(this);
        } catch (IOException e) {
            LOGGER.fatal("MovementsTab.fxml wasn't loaded");
            throw new RuntimeException(e);
        }

        try {
            FXMLLoader reportsLoader = new FXMLLoader();
            reportsLoader.setLocation(HelloApplication.class.getResource("Reports.fxml"));
            AnchorPane anchorPaneReports = reportsLoader.load();
            reportsTab.setContent(anchorPaneReports);
            reportsController = reportsLoader.getController();
        } catch (IOException e) {
            LOGGER.fatal("Reports.fxml wasn't loaded");
            throw new RuntimeException(e);
        }

        try {
            FXMLLoader settingsLoader = new FXMLLoader();
            settingsLoader.setLocation(HelloApplication.class.getResource("Settings.fxml"));
            AnchorPane anchorPaneSettingsTab = settingsLoader.load();
            settingsTab.setContent(anchorPaneSettingsTab);
            settingsController = settingsLoader.getController();
            settingsController.setMainController(this);
            settingsController.setMainControllerInit();
        } catch (IOException e) {
            LOGGER.fatal("SettingsTab.fxml wasn't loaded");
            LOGGER.info(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void initBalance() {
        String defaultWalletName = mainModel.getDefaultWalletName();
        double defaultWalletBalance = mainModel.defaultWalletBalance();
        displayWalletName.setText(defaultWalletName);
        displayWalletBalance.setText(String.valueOf(defaultWalletBalance));
    }
    public static List<Boolean> getTrueFalseList() {
        List<Boolean> listTrueFalse = new ArrayList<>();
        listTrueFalse.add(Boolean.TRUE);
        listTrueFalse.add(Boolean.FALSE);
        return listTrueFalse;
    }
    public static TextFormatter<String> getOnlyDigitsTextFormatter() {
        UnaryOperator<TextFormatter.Change> textFieldOnlyDigitsFilter;
        textFieldOnlyDigitsFilter = change -> {
            String text = change.getText();

            if (text.matches("[0-9,/.]*")) {
                return change;
            }
            return null;
        };
        return new TextFormatter<>(textFieldOnlyDigitsFilter);
    }

    public static String getDefaultWalletName() {
        return mainModel.getDefaultWalletName();
    }

    public static Map<Integer, String> getWalletList() {
        return mainModel.getWalletList();
    }

    public static double getWalletBalanceById(int id) {
        return mainModel.getWalletBalanceById(id);
    }

    public static int updateWalletBalanceById(int id, double newValue) {
        return mainModel.updateWalletBalanceById(id, newValue);
    }
    public static int getWalletIdByName(String name) {
        return MainController.getWalletList().entrySet().stream().
                filter(e -> e.getValue().equals(name)).findFirst().orElseThrow(() -> {
                    LOGGER.fatal("walletEditCommit gets null");
                    return new NullPointerException("walletEditCommit gets null");
                }).getKey();
    }
    public static Map<Integer, String> getIncomeCategoryList() {
        return mainModel.getIncomeCategoryList();
    }
    public static String getDefaultIncomeCategory() {
        return mainModel.getDefaultIncomeCategory();
    }
    public static Map<Integer, String> getExpensesCategoryList() {
        return mainModel.getExpensesCategoryList();
    }
    public static String getDefaultExpensesCategory() {
        return mainModel.getDefaultExpensesCategory();
    }
    @SuppressWarnings("Duplicates")
    public static Callback<TableColumn<ExpenseEntity, LocalDate>, TableCell<ExpenseEntity, LocalDate>> dateCallbackForExpenses = new Callback<>() {
        @Override
        public TableCell<ExpenseEntity, LocalDate> call(TableColumn<ExpenseEntity, LocalDate> expenseEntityLocalDateTableColumn) {
            return new TableCell<>() {
                private final DateTimeFormatter format = DateTimeFormatter.ofPattern("dd.MM.yyyy");

                @Override
                protected void updateItem(LocalDate localDate, boolean b) {
                    super.updateItem(localDate, b);
                    if (localDate == null) {
                        setText(null);
                    } else {
                        this.setText(localDate.format(format));
                    }
                }
            };
        }
    };
    @SuppressWarnings("Duplicates")
    public static Callback<TableColumn<ExpenseEntity, Double>, TableCell<ExpenseEntity, Double>> amountCallbackForExpenses = new Callback<>() {
        @Override
        public TableCell<ExpenseEntity, Double> call(TableColumn<ExpenseEntity, Double> expenseEntityDoubleTableColumn) {
            return new TableCell<>() {
                private TextField textField;

                @Override
                public void startEdit() {
                    super.startEdit();
                    setText(null);
                    createTextField();
                    setGraphic(textField);
                    textField.setText(getItem().toString());
                    textField.selectAll();
                }

                @Override
                public void cancelEdit() {
                    super.cancelEdit();
                    setGraphic(null);
                    setText(getItem().toString());
                }

                @Override
                protected void updateItem(Double aDouble, boolean empty) {
                    super.updateItem(aDouble, empty);
                    if (empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        if (isEditing()) {
                            if (textField != null) {
                                textField.setText(getItem().toString());
                            }
                            setText(null);
                            setGraphic(textField);
                        } else {
                            setText(getItem().toString());
                            setGraphic(null);
                        }
                    }
                }

                private void createTextField() {
                    textField = new TextField(getItem().toString());
                    textField.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
                        if (keyEvent.getCode() == KeyCode.ENTER) {
                            commitEdit(Double.parseDouble(textField.getText()));
                            keyEvent.consume();
                        }
                    });

                    textField.focusedProperty().addListener((observableValue, aBoolean, t1) -> {
                        if (!t1) {
                            try {
                                commitEdit(Double.valueOf(textField.getText()));
                            } catch (NumberFormatException ignore) {
                                cancelEdit();
                            }
                        }
                    });

                    textField.setTextFormatter(MainController.getOnlyDigitsTextFormatter());
                }
            };
        }
    };
    @SuppressWarnings("Duplicates")
    public static Callback<TableColumn<IncomeEntity, LocalDate>, TableCell<IncomeEntity, LocalDate>> dateCallbackForIncome = new Callback<>() {
        @Override
        public TableCell<IncomeEntity, LocalDate> call(TableColumn<IncomeEntity, LocalDate> expenseEntityLocalDateTableColumn) {
            return new TableCell<>() {
                private final DateTimeFormatter format = DateTimeFormatter.ofPattern("dd.MM.yyyy");

                @Override
                protected void updateItem(LocalDate localDate, boolean b) {
                    super.updateItem(localDate, b);
                    if (localDate == null) {
                        setText(null);
                    } else {
                        this.setText(localDate.format(format));
                    }
                }
            };
        }
    };
    @SuppressWarnings("Duplicates")
    public static Callback<TableColumn<IncomeEntity, Double>, TableCell<IncomeEntity, Double>> amountCallbackForIncome = new Callback<>() {
        @Override
        public TableCell<IncomeEntity, Double> call(TableColumn<IncomeEntity, Double> incomeEntityDoubleTableColumn) {
            return new TableCell<>() {
                private TextField textField;

                @Override
                public void startEdit() {
                    super.startEdit();
                    setText(null);
                    createTextField();
                    setGraphic(textField);
                    textField.setText(getItem().toString());
                    textField.selectAll();
                }

                @Override
                public void cancelEdit() {
                    super.cancelEdit();
                    setGraphic(null);
                    setText(getItem().toString());
                }

                @Override
                protected void updateItem(Double aDouble, boolean empty) {
                    super.updateItem(aDouble, empty);
                    if (empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        if (isEditing()) {
                            if (textField != null) {
                                textField.setText(getItem().toString());
                            }
                            setText(null);
                            setGraphic(textField);
                        } else {
                            setText(getItem().toString());
                            setGraphic(null);
                        }
                    }
                }

                private void createTextField() {
                    textField = new TextField(getItem().toString());
                    textField.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
                        if (keyEvent.getCode() == KeyCode.ENTER) {
                            commitEdit(Double.parseDouble(textField.getText()));
                            keyEvent.consume();
                        }
                    });

                    textField.focusedProperty().addListener((observableValue, aBoolean, t1) -> {
                        if (!t1) {
                            try {
                                commitEdit(Double.valueOf(textField.getText()));
                            } catch (NumberFormatException ignore) {
                                cancelEdit();
                            }
                        }
                    });

                    textField.setTextFormatter(MainController.getOnlyDigitsTextFormatter());
                }
            };
        }
    };
    @SuppressWarnings("Duplicates")
    public static Callback<TableColumn<MovementEntity, LocalDate>, TableCell<MovementEntity, LocalDate>> dateCallbackForMovements = new Callback<>() {
        @Override
        public TableCell<MovementEntity, LocalDate> call(TableColumn<MovementEntity, LocalDate> movementEntityLocalDateTableColumn) {
            return new TableCell<>() {
                private final DateTimeFormatter format = DateTimeFormatter.ofPattern("dd.MM.yyyy");

                @Override
                protected void updateItem(LocalDate localDate, boolean b) {
                    super.updateItem(localDate, b);
                    if (localDate == null) {
                        setText(null);
                    } else {
                        this.setText(localDate.format(format));
                    }
                }
            };
        }
    };
    @SuppressWarnings("Duplicates")
    public static Callback<TableColumn<MovementEntity, Double>, TableCell<MovementEntity, Double>> amountCallbackForMovements = new Callback<>() {
        @Override
        public TableCell<MovementEntity, Double> call(TableColumn<MovementEntity, Double> movementEntityDoubleTableColumn) {
            return new TableCell<>() {
                private TextField textField;

                @Override
                public void startEdit() {
                    super.startEdit();
                    setText(null);
                    createTextField();
                    setGraphic(textField);
                    textField.setText(getItem().toString());
                    textField.selectAll();
                }

                @Override
                public void cancelEdit() {
                    super.cancelEdit();
                    setGraphic(null);
                    setText(getItem().toString());
                }

                @Override
                protected void updateItem(Double aDouble, boolean empty) {
                    super.updateItem(aDouble, empty);
                    if (empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        if (isEditing()) {
                            if (textField != null) {
                                textField.setText(getItem().toString());
                            }
                            setText(null);
                            setGraphic(textField);
                        } else {
                            setText(getItem().toString());
                            setGraphic(null);
                        }
                    }
                }

                private void createTextField() {
                    textField = new TextField(getItem().toString());
                    textField.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
                        if (keyEvent.getCode() == KeyCode.ENTER) {
                            commitEdit(Double.parseDouble(textField.getText()));
                            keyEvent.consume();
                        }
                    });

                    textField.focusedProperty().addListener((observableValue, aBoolean, t1) -> {
                        if (!t1) {
                            try {
                                commitEdit(Double.valueOf(textField.getText()));
                            } catch (NumberFormatException ignore) {
                                cancelEdit();
                            }
                        }
                    });

                    textField.setTextFormatter(MainController.getOnlyDigitsTextFormatter());
                }
            };
        }
    };
    public void updateScreenInfo() {
        LOGGER.debug("information was updated");
        WalletController walletController = settingsController.getWalletController();
        IncomeCategoryController incomeCategoryController = settingsController.getIncomeCategoryController();
        ExpensesCategoryController expensesCategoryController = settingsController.getExpensesCategoryController();
        TablesController tablesController = reportsController.getTablesController();

        incomeController.updateVisualInformation();
        expensesController.updateVisualInformation();
        movementsController.updateVisualInformation();
        incomeCategoryController.updateVisualInformation();
        expensesCategoryController.updateVisualInformation();
        walletController.updateVisualInformation();
        tablesController.initDynamicSelectFields();
        initBalance();
    }
    public static ObservableList<String> getWalletObservableList() {
        return FXCollections.observableArrayList(getWalletList().values());
    }
    public void exit() {
        System.exit(0);
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public Stage getStage() {
        return stage;
    }

    public void refresh() {
        this.updateScreenInfo();
    }
}
