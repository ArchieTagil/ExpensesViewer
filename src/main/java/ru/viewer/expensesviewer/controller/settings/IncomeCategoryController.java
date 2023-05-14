package ru.viewer.expensesviewer.controller.settings;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.viewer.expensesviewer.controller.IncomeController;
import ru.viewer.expensesviewer.controller.MainController;
import ru.viewer.expensesviewer.model.DbConnection;
import ru.viewer.expensesviewer.model.objects.Popup;
import ru.viewer.expensesviewer.model.objects.settings.CategoryEntity;
import ru.viewer.expensesviewer.model.objects.settings.WalletEntity;

import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class IncomeCategoryController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(IncomeController.class);
    private final Connection connection = DbConnection.getInstance().getConnection();
    private MainController mainController;
    @FXML
    private AnchorPane incomeCategoryAnchorPane;
    @FXML
    private TableView<CategoryEntity> incomeCategoryListTable;
    @FXML
    private TableColumn<CategoryEntity, Integer> categoryId;
    @FXML
    private TableColumn<CategoryEntity, String> categoryName;
    @FXML
    private TableColumn<CategoryEntity, Boolean> categoryDefault;
    @FXML
    private TextField newCategoryName;
    @FXML
    private Button categoryAdd;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initHotKeys();
        incomeCategoryListTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        incomeCategoryListTable.setEditable(true);
        incomeCategoryListTable.setItems(getIncomeCategoryEntityList());

        categoryName.setCellFactory(TextFieldTableCell.forTableColumn());
        categoryDefault.setCellFactory(ChoiceBoxTableCell.forTableColumn(FXCollections.observableArrayList(MainController.getTrueFalseList())));

        categoryId.setCellValueFactory(new PropertyValueFactory<>("categoryId"));
        categoryName.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        categoryDefault.setCellValueFactory(new PropertyValueFactory<>("categoryDefault"));
    }

    public void deleteRows(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.DELETE) {
            ObservableList<CategoryEntity> list = incomeCategoryListTable.getSelectionModel().getSelectedItems();
            for (CategoryEntity entity : list) {
                deleteCategory(entity.getCategoryId());
            }
            mainController.updateScreenInfo();
        }
    }

    private void deleteCategory(int categoryId) {
        try(Statement statement = connection.createStatement()) {
            statement.executeUpdate("DELETE FROM `income_category` WHERE income_category_id = " + categoryId + ";");
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void nameEditCommit(TableColumn.CellEditEvent<CategoryEntity, String> cellEditEvent) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE `income_category` SET `income_category_name` = ? WHERE income_category_id = ?;")) {
            preparedStatement.setString(1, cellEditEvent.getNewValue());
            preparedStatement.setInt(2, cellEditEvent.getRowValue().getCategoryId());
            preparedStatement.executeUpdate();
            mainController.updateScreenInfo();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void defaultEditCommit(TableColumn.CellEditEvent<CategoryEntity, Boolean> cellEditEvent) {
        LOGGER.debug("Edit income category default");
        try(Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM `income_category` WHERE `income_default` = TRUE;");
            String sqlSetNewValue = "" +
                    "UPDATE `income_category` SET `income_default` = " + cellEditEvent.getNewValue() + " " +
                    "WHERE `income_category_id` = " + cellEditEvent.getRowValue().getCategoryId() + ";";
            rs.next();
            int countOfTrue = rs.getInt(1);
            LOGGER.debug(countOfTrue);

            if (countOfTrue == 0) {
                if (cellEditEvent.getNewValue() == Boolean.FALSE) {
                    Popup.display("Ошибка", "Должна быть хотя бы одна категория по умолчанию");
                } else {
                    statement.executeUpdate(sqlSetNewValue);
                    mainController.updateScreenInfo();
                }
                return;
            }
            ResultSet getExistingDefaultTrueId = statement.executeQuery("SELECT `income_category_id` FROM income_category WHERE income_default = TRUE");
            getExistingDefaultTrueId.next();
            int existingDefaultTrueId = getExistingDefaultTrueId.getInt(1);

            String sqlSetFalse = "UPDATE `income_category` SET `income_default` = FALSE WHERE `income_category_id` = " + existingDefaultTrueId + ";";

            if (cellEditEvent.getNewValue() == Boolean.TRUE && cellEditEvent.getOldValue() == Boolean.FALSE && countOfTrue == 1) {
                LOGGER.info("Управление галочками вызывается, newValue == Boolean.TRUE && oldValue == Boolean.FALSE && countOfTrue == 1");
                statement.executeUpdate(sqlSetFalse);
                statement.executeUpdate(sqlSetNewValue);
            } else if (countOfTrue > 1 && cellEditEvent.getNewValue() == Boolean.FALSE) {
                LOGGER.info("countOfTrue > 1 && newValue == Boolean.FALSE");
                statement.executeUpdate(sqlSetNewValue);
            } else if (countOfTrue == 1 && existingDefaultTrueId == cellEditEvent.getRowValue().getCategoryId() && cellEditEvent.getNewValue() == Boolean.FALSE) {
                LOGGER.info("Must Display PopUP");
                Popup.display("Ошибка", "Должена быть хотя бы одна категория по умолчанию!");
            }
            mainController.updateScreenInfo();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void addNewCategory() {
        try(PreparedStatement statement = connection.prepareStatement("INSERT INTO `income_category` (income_category_name, income_default) VALUES (?, 0)")) {
            statement.setString(1, newCategoryName.getText());
            statement.execute();
            mainController.updateScreenInfo();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private ObservableList<CategoryEntity> getIncomeCategoryEntityList() {
        try (Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery("SELECT * FROM `income_category`;");
            List<CategoryEntity> categoryEntityList = new ArrayList<>();
            while (rs.next()) {
                categoryEntityList.add(new CategoryEntity(
                        rs.getInt("income_category_id"),
                        rs.getString("income_category_name"),
                        rs.getBoolean("income_default")
                ));
            }
            return FXCollections.observableArrayList(categoryEntityList);
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void updateVisualInformation() {
        incomeCategoryListTable.setItems(getIncomeCategoryEntityList());
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @SuppressWarnings("Duplicates")
    private void initHotKeys() {
        KeyCodeCombination alt1 = new KeyCodeCombination(KeyCode.DIGIT1, KeyCombination.ALT_DOWN);
        KeyCodeCombination ctrlEnter = new KeyCodeCombination(KeyCode.ENTER, KeyCombination.SHORTCUT_DOWN);

        EventHandler<KeyEvent> filter = event -> {
            if (ctrlEnter.match(event)) {
                categoryAdd.fire();
            } else if (alt1.match(event)) {
                newCategoryName.requestFocus();
            }
        };
        incomeCategoryAnchorPane.addEventFilter(KeyEvent.KEY_PRESSED, filter);
    }
}
