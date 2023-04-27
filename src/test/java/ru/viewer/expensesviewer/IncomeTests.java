package ru.viewer.expensesviewer;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import ru.viewer.expensesviewer.model.IncomeModel;

import java.sql.SQLException;

public class IncomeTests {
    IncomeModel incomeModel = new IncomeModel();

    public IncomeTests() throws SQLException, ClassNotFoundException {
    }

    @Test
    public void shouldCheckDefaultWallet() throws SQLException {
       assertEquals(incomeModel.getDefaultWallet(), "Карта Сбербанка");
    }
}
