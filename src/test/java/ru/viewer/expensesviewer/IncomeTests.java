package ru.viewer.expensesviewer;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import ru.viewer.expensesviewer.model.DbConnection;
import ru.viewer.expensesviewer.model.IncomeModel;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class IncomeTests {
    IncomeModel incomeModel = new IncomeModel();
    public IncomeTests() throws SQLException, ClassNotFoundException {
    }
//    @Test
//    public void shouldReturnDefaultWallet() throws SQLException {
//       assertEquals(incomeModel.getDefaultWallet(), "Карта Альфабанк");
//    }
    @Test
    public void shouldReturnDefaultIncomeCategory() throws SQLException {
        assertEquals(incomeModel.getDefaultIncomeCategory(), "Зарплата");
    }
    @Test
    public void dbConnectionNotNull() throws SQLException, ClassNotFoundException {
        assertNotNull(DbConnection.getInstance());
    }
    @Test
    public void incomeListShouldNotBeNull() throws SQLException {
        assertNotNull(incomeModel.getIncomeList());
    }
//    @Test
//    public void walletListShouldNotBeNull() throws SQLException {
//        assertNotNull(incomeModel.getWalletList());
//    }
    @Test
    public void categoryListShouldNotBeNull() throws SQLException {
        assertNotNull(incomeModel.getIncomeCategoryList());
    }
//    @Test
//    public void shouldReturnWalletBalance() throws SQLException {
//        assertNotNull(incomeModel.getWalletBalance(1));
//    }

}
