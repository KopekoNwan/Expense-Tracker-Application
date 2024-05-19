package com.example.expense_tracker.controller;

import com.example.expense_tracker.Main;
import com.example.expense_tracker.model.Expense;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.image.Image;

import javax.swing.text.html.ImageView;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.expense_tracker.Main.*;

public class DatabaseController {

    public static Connection connection = null;
    public static int EXPENSE_ID = 0;

    public static void connectDatabase() {
        try {
            Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:database/expense_tracker.db";
            connection = DriverManager.getConnection(url);
            System.out.println("Connection to SQLite has been established.");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static void closeDatabase() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Connection to SQLite has been closed.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static Image getImageById(int id) throws SQLException {
        String query = "SELECT icon FROM categories WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    byte[] imageBytes = resultSet.getBytes("icon");
                    if (imageBytes != null) {
                        ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
                        return new Image(inputStream);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return null; // Return null if the image is not found
    }

    public static Map<String, Integer> getCategories() throws SQLException {
        Map<String, Integer> categoryMap = new HashMap<>();

        if (connection == null || connection.isClosed()) {
            throw new SQLException("Database connection is closed.");
        }

        String query = "SELECT id, name FROM categories";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                categoryMap.put(name, id);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return categoryMap;
    }

    public static void addExpenseList() throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLException("Database connection is closed.");
        }

        String query = "SELECT e.id, e.amount, e.description, e.category, c.icon " +
                "FROM expenses e " +
                "JOIN categories c ON e.category = c.name";

        List<Expense> expenseDataList = new ArrayList<>();
        List<Integer> listOfIds = new ArrayList<>();
        String selectSQL = "SELECT amount, description, category FROM expenses WHERE id = ?";
        String updateSQL = "UPDATE expenses SET amount = ?, description = ?, category = ? WHERE id = ?";

        // Retrieve all expense IDs
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT id FROM expenses")) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                listOfIds.add(id);
            }
        }

        // Check if EXPENSE_ID exists and update if necessary
        if (listOfIds.contains(EXPENSE_ID)) {
            try (PreparedStatement selectPstmt = connection.prepareStatement(selectSQL);
                 PreparedStatement updatePstmt = connection.prepareStatement(updateSQL)) {

                selectPstmt.setInt(1, EXPENSE_ID);
                try (ResultSet resultSet = selectPstmt.executeQuery()) {
                    if (resultSet.next()) {
                        long amount = resultSet.getLong("amount");
                        String description = resultSet.getString("description");
                        String category = resultSet.getString("category");

                        AddExpensePageController addExpensePageController = addExpenseLoader.getController();
                        amount = Long.parseLong(addExpensePageController.getAmountTextField().getText());
                        description = addExpensePageController.getDescriptionTextField().getText();
                        category = addExpensePageController.getCategoryChoiceBox().getValue();

                        updatePstmt.setLong(1, amount);
                        updatePstmt.setString(2, description);
                        updatePstmt.setString(3, category);
                        updatePstmt.setInt(4, EXPENSE_ID);

                        int affectedRows = updatePstmt.executeUpdate();
                        if (affectedRows > 0) {
                            System.out.println("Record updated successfully.");
                        } else {
                            System.out.println("No record found with the provided ID.");
                        }
                    }
                }
            }
        }

        // Clear the list of IDs
        listOfIds.clear();

        // Retrieve and populate the expense data list
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                long amount = resultSet.getLong("amount");
                String description = resultSet.getString("description");
                String categoryName = resultSet.getString("category");
                byte[] categoryIcon = resultSet.getBytes("icon");

                // Add the new data to the list
                expenseDataList.add(new Expense(id, amount, description, categoryName, categoryIcon));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }



    // Clear the existing items from the ExpenseTrackerController
        ExpenseTrackerController expenseTrackerController = expenseTrackerLoader.getController();
        expenseTrackerController.getExpenseList().getChildren().clear();
        System.out.println(expenseDataList);
        // Populate the ExpenseTrackerController with the new data
        for (Expense expenseData : expenseDataList) {
            try {
                FXMLLoader loader = new FXMLLoader(Main.class.getResource("expense_container.fxml"));
                FXMLLoader loader_delete = new FXMLLoader(Main.class.getResource("expense_container_delete.fxml"));
                FXMLLoader loader_edit = new FXMLLoader(Main.class.getResource("expense_container_edit.fxml"));
                StackPane expenseContainerRoot = loader.load();
                AnchorPane expenseContainerRootDelete = loader_delete.load();
                AnchorPane expenseContainerRootEdit = loader_edit.load();
                ExpenseContainerController controller = loader.getController();
                ExpenseContainerControllerDelete expenseContainerControllerDelete = loader_delete.getController();
                ExpenseContainerControllerEdit expenseContainerControllerEdit = loader_edit.getController();
                DecimalFormat formatter = new DecimalFormat("#,###");

                controller.getCategoryLabel().setText(expenseData.getCategoryName());
                controller.getDescriptionLabel().setText(expenseData.getDescription());
                controller.getValueLabel().setText(String.valueOf(formatter.format(expenseData.getAmount())));

                // Set the category icon image
                byte[] iconBytes = expenseData.getCategoryIcon();
                if (iconBytes != null) {
                    ByteArrayInputStream iconInputStream = new ByteArrayInputStream(iconBytes);
                    Image categoryImage = new Image(iconInputStream);
                    controller.getCategoryIcon().setImage(categoryImage);
                }

                controller.expenseContainer.setUserData(expenseData.getId());
                controller.slideToDelete(controller.expenseContainer, controller.getExpenseParentContainer(),
                        expenseContainerControllerDelete.getExpenseContainerDelete(),
                        expenseContainerControllerEdit.getExpenseContainerEdit());
                expenseContainerRootDelete.toBack();
                expenseContainerRootEdit.toBack();
                expenseContainerRootDelete.setVisible(false);
                expenseContainerRootEdit.setVisible(false);
                controller.getExpenseParentContainer().getChildren().add(0, expenseContainerRootDelete);
                controller.getExpenseParentContainer().getChildren().add(0, expenseContainerRootEdit);

                expenseTrackerController.getExpenseList().getChildren().add(0, expenseContainerRoot);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        EXPENSE_ID = 0;

    }


    public static void appendToDatabase(Connection connection, String date, double amount, String description, String category) throws SQLException {
        String sql = "INSERT INTO expenses(date, amount, description, category) VALUES(?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, date);
            preparedStatement.setDouble(2, amount);
            preparedStatement.setString(3, description);
            preparedStatement.setString(4, category);

            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("A new expense was inserted successfully!");
            }
        } catch (SQLException e) {
            System.err.println("SQL error occurred: " + e.getMessage());
            throw e;
        }

    }

    public static void sumOfAllExpenses() throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLException("Database connection is closed.");
        }
        String query = "SELECT amount FROM expenses";
        List<Long> expenses = new ArrayList<>();

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {


            while (resultSet.next()) {
                long amount = resultSet.getLong("amount");
                // Add the new data to the list
                expenses.add(amount);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        long totalExpense = 0;

        for(long num : expenses) {
            totalExpense += num;
        }
        DecimalFormat formatter = new DecimalFormat("#, ###");
        ExpenseTrackerController expenseTrackerController = expenseTrackerLoader.getController();

        expenseTrackerController.getTotalAmountLabel().setText(String.valueOf(formatter.format(totalExpense)));
        expenses.clear();

    }

}
