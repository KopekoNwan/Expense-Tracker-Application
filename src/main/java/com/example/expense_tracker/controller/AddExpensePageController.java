package com.example.expense_tracker.controller;
import javafx.animation.TranslateTransition;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.example.expense_tracker.Main.*;

public class AddExpensePageController {
    @FXML
    private ChoiceBox<String> categoryChoiceBox;

    public final List<String> expenseCategories = new ArrayList<>();

    @FXML
    private TextField amountTextField;

    Map<String, Integer> categories = DatabaseController.getCategories();

    @FXML
    private TextArea descriptionTextField;


    public AddExpensePageController() throws SQLException {
    }

    public ChoiceBox<String> getCategoryChoiceBox() {
        return categoryChoiceBox;
    }

    public int categoryId = 0;

    public TextField getAmountTextField() {
        return amountTextField;
    }

    public TextArea getDescriptionTextField() {
        return descriptionTextField;
    }


    @FXML
    public void initialize() throws SQLException {

        // Set background color when focused
        amountTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                // When focused
                amountTextField.setStyle("-fx-background-color: transparent;");
                amountTextField.setStyle("-fx-text-fill: white; -fx-background-color: transparent; -fx-border-width:  0 0 1 0; -fx-border-color:  transparent transparent #FFC877 transparent");
            } else {
                // When focus lost
                amountTextField.setStyle("-fx-background-color: transparent;");
                amountTextField.setStyle("-fx-text-fill: white; -fx-background-color: transparent; -fx-border-width:  0 0 1 0; -fx-border-color:  transparent transparent #FFC877 transparent");
            }
        });
        // Add a filter to only allow integer values
        amountTextField.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            if (!event.getCharacter().matches("\\d")) {
                event.consume();
            }
        });

        ExpenseTrackerController expenseTrackerController = expenseTrackerLoader.getController();
        expenseTrackerController.getAdd_expense_button().setOnMouseEntered(e -> {
            Button addExpenseButton = expenseTrackerController.getAdd_expense_button();
            addExpenseButton.setStyle("-fx-background-radius: 100; -fx-background-color: derive(#FDAB57, 20%);");
            addExpenseButton.setScaleX(1.1); // Increase X scale by 10%
            addExpenseButton.setScaleY(1.1); // Increase Y scale by 10%
        });
        expenseTrackerController.getAdd_expense_button().setOnMouseExited(e -> {
            Button addExpenseButton = expenseTrackerController.getAdd_expense_button();
            addExpenseButton.setStyle("-fx-background-radius: 100; -fx-background-color: #FDAB57;");
            addExpenseButton.setScaleX(1.0); // Reset X scale
            addExpenseButton.setScaleY(1.0); // Reset Y scale
        });

        DatabaseController.addExpenseList();
        DatabaseController.sumOfAllExpenses();
        // Add categories to the choice box
        categoryChoiceBox.getItems().addAll(categories.keySet());

        Map<String, Integer> finalCategories = categories;
        categoryChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && finalCategories != null) {
                categoryId = finalCategories.get(newValue);
            }
        });
        // Set the value to the first name from the categories table
        if (!categories.isEmpty()) {
            String firstCategoryName = categories.keySet().iterator().next();
            categoryChoiceBox.setValue(firstCategoryName);
        }
    }


    @FXML
    public void returnMainMenu(ActionEvent event) throws IOException, SQLException {
        DatabaseController.EXPENSE_ID = 0;
        DatabaseController.addExpenseList();
        DatabaseController.sumOfAllExpenses();
        stage.setScene(expenseTrackerScene);
    }


    @FXML
    public void confirmExpense(ActionEvent event) throws IOException, SQLException {
        if (DatabaseController.EXPENSE_ID == 0) {

        LocalDate currentDate = LocalDate.now();
        String dateString = currentDate.toString();
        double amount = Double.parseDouble(amountTextField.getText());
        String description = descriptionTextField.getText();
        String selectedCategoryName = categoryChoiceBox.getValue();

        DatabaseController.appendToDatabase(DatabaseController.connection, dateString, amount, description, selectedCategoryName);
}
        DatabaseController.addExpenseList();
        DatabaseController.sumOfAllExpenses();
        amountTextField.clear();
        descriptionTextField.clear();
        stage.setScene(expenseTrackerScene);
    }






}
