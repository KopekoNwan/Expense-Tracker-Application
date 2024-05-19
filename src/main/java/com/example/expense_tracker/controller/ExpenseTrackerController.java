package com.example.expense_tracker.controller;

import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.sql.SQLException;

import static com.example.expense_tracker.Main.*;

public class ExpenseTrackerController {
    @FXML
    private Button add_expense_button;

    @FXML
    private AnchorPane add_expense_page;

    @FXML
    VBox expenseList;

    @FXML
    public Label totalAmountLabel;


    @FXML
    public Label getTotalAmountLabel() {
        return totalAmountLabel;
    }

    @FXML
    public Button getAdd_expense_button() {
        return add_expense_button;
    }

    @FXML
    public VBox getExpenseList() {
        return expenseList;
    }

    @FXML
    public void addExpense(ActionEvent event) throws SQLException {
        DatabaseController.addExpenseList();
        stage.setScene(addExpensePageScene);
    }





}
