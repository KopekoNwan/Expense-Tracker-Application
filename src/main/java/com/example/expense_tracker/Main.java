package com.example.expense_tracker;

import com.example.expense_tracker.controller.DatabaseController;
import com.example.expense_tracker.controller.ExpenseTrackerController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    public static Parent expenseTrackerRoot; // Declare the variable
    public static Scene expenseTrackerScene;
    public static Parent addExpensePageRoot;
    public static Scene addExpensePageScene;
    public static FXMLLoader addExpenseLoader;
    public static Stage stage;
    public static FXMLLoader expenseTrackerLoader;

    @Override
    public void start(Stage stage) throws IOException {
        Main.stage = stage;

        // Load the root node of expense_tracker.fxml
        FXMLLoader expenseTrackerLoader = new FXMLLoader(Main.class.getResource("expense_tracker.fxml"));
        expenseTrackerRoot = expenseTrackerLoader.load();
        expenseTrackerScene = new Scene(expenseTrackerRoot);
        Main.expenseTrackerLoader = expenseTrackerLoader;

        // Load the root node of add_expense_page.fxml
        FXMLLoader addExpenseLoader = new FXMLLoader(Main.class.getResource("add_expense_page.fxml"));
        addExpensePageRoot = addExpenseLoader.load();
        addExpensePageScene = new Scene(addExpensePageRoot);
        Main.addExpenseLoader = addExpenseLoader;

        stage.setTitle("Expense Tracker");
        stage.setScene(expenseTrackerScene);
        stage.setResizable(false);
        stage.show();
    }


    public static void main(String[] args) {
        DatabaseController.connectDatabase();
        launch();
    }
}
