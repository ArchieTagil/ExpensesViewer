package ru.viewer.expensesviewer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import ru.viewer.expensesviewer.model.MainModel;

public class MainModelTests {
    MainModel mainModel = new MainModel();

    @Test
    public void shouldReturnDefaultWalletName() {
        assertNotNull(mainModel.getDefaultWalletName());
    }

    @Test
    public void shouldReturnDefaultWalletBalance() {
        assertEquals(mainModel.defaultWalletBalance(), 30000);
    }
}
