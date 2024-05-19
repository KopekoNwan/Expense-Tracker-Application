package com.example.expense_tracker.controller;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

public class ExpenseContainerControllerDelete {

    @FXML
    public AnchorPane expenseContainerDelete;

    @FXML
    public AnchorPane getExpenseContainerDelete() {
        expenseContainerDelete.setVisible(true);
        return expenseContainerDelete;
    }
}
