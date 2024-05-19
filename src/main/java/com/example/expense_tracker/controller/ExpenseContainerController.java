package com.example.expense_tracker.controller;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.scene.image.ImageView;
import java.sql.*;

import static com.example.expense_tracker.Main.*;

public class ExpenseContainerController {
    @FXML
    public AnchorPane expenseContainer;

    @FXML
    public Label categoryLabel;


    @FXML
    public StackPane expenseParentContainer;

    @FXML
    public Label descriptionLabel;

    @FXML
    public StackPane valueLabelPane;

    @FXML
    public Text valueLabel;

    @FXML
    public StackPane getExpenseParentContainer() {
        return expenseParentContainer;
    }

    public Label getCategoryLabel() {
        return categoryLabel;
    }

    public Label getDescriptionLabel() {
        return descriptionLabel;
    }

    public Text getValueLabel() {
        return valueLabel;
    }

    @FXML
    public ImageView categoryIcon;

    @FXML
    public ImageView getCategoryIcon() {
        return categoryIcon;
    }

    public void removeExpenseFromDatabase(Object expenseId) {
        String url = "jdbc:sqlite:database/expense_tracker.db";
        int idToDelete = (int) expenseId;
        String sql = "DELETE FROM expenses WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Set the corresponding parameter
            pstmt.setInt(1, idToDelete);

            // Execute the delete statement
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("A record was deleted successfully.");
            } else {
                System.out.println("No record found with the provided ID.");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    public void slideToDelete(AnchorPane expenseContainer, StackPane expenseParentContainer, AnchorPane expenseContainerDelete, AnchorPane expenseContainerEdit) {
        // Variables to track the sliding
        final double[] initialX = new double[1];
        final boolean[] isSliding = {false};

        // Mouse press event
        expenseContainer.setOnMousePressed(event -> {
            initialX[0] = event.getSceneX();
        });

        // Mouse drag event
        expenseContainer.setOnMouseDragged(event -> {
            double deltaX = event.getSceneX() - initialX[0];
            if (Math.abs(deltaX) > 10) {
                isSliding[0] = true;
                expenseContainer.setTranslateX(deltaX);

                // Update visibility based on drag direction and distance
                if (deltaX > 10) {
                    expenseContainerDelete.setVisible(true);
                    expenseContainerEdit.setVisible(false);
                } else if (deltaX < -10) {
                    expenseContainerEdit.setVisible(true);
                    expenseContainerDelete.setVisible(false);
                }
            }
        });

        // Mouse release event
        expenseContainer.setOnMouseReleased(event -> {
            if (isSliding[0]) {
                if (expenseContainer.getTranslateX() > 70) {
                    // Animate and remove the node
                    TranslateTransition slideOut = new TranslateTransition(Duration.millis(300), expenseContainer);
                    slideOut.setToX(300);
                    slideOut.setOnFinished(e -> {
                        Object expenseId = expenseContainer.getUserData();
                        removeExpenseFromDatabase(expenseId);

                        // Assuming expenseParentContainer's parent is a VBox
                        Parent parent = expenseParentContainer.getParent();
                        if (parent instanceof VBox) {
                            ((VBox) parent).getChildren().remove(expenseParentContainer);
                        } else {
                            System.err.println("Expected a VBox but found " + parent.getClass().getName());
                        }

                        try {
                            DatabaseController.addExpenseList();
                        } catch (SQLException ex) {
                            throw new RuntimeException(ex);
                        }
                        try {
                            DatabaseController.sumOfAllExpenses();
                        } catch (SQLException ex) {
                            throw new RuntimeException(ex);
                        }
                        stage.setScene(expenseTrackerScene);
                    });
                    slideOut.play();
                } else if (expenseContainer.getTranslateX() < -70) { // Check if dragged to the left
                    extractDataFromDatabase(expenseContainer.getUserData());

                    stage.setScene(addExpensePageScene);
                    System.out.println("Dragged to the left");
                } else {
                    // Animate back to original position
                    TranslateTransition slideBack = new TranslateTransition(Duration.millis(300), expenseContainer);
                    slideBack.setToX(0);
                    slideBack.setOnFinished(e -> {
                        // Make expenseContainerDelete and expenseContainerEdit invisible after the slide back animation finishes
                        expenseContainerDelete.setVisible(false);
                        expenseContainerEdit.setVisible(false);
                    });
                    slideBack.play();
                }
                isSliding[0] = false;
            }
        });
    }

    public void extractDataFromDatabase(Object expenseID) {
        String url = "jdbc:sqlite:database/expense_tracker.db";
        int containerID = (int) expenseID;
        DatabaseController.EXPENSE_ID = containerID;

        String selectSQL = "SELECT amount, description, category FROM expenses WHERE id = ?";
        String updateSQL = "UPDATE expenses SET amount = ?, description = ?, category = ? WHERE id = ?";

        try (Connection connection = DriverManager.getConnection(url);
            PreparedStatement selectPstmt = connection.prepareStatement(selectSQL);
            PreparedStatement updatePstmt = connection.prepareStatement(updateSQL))
        {
            selectPstmt.setInt(1, containerID);
            ResultSet resultSet = selectPstmt.executeQuery();

            if (resultSet.next()) {
                // Extract Data from the resultSet
                AddExpensePageController addExpensePageController = addExpenseLoader.getController();

                addExpensePageController.getAmountTextField().setText(String.valueOf(resultSet.getLong("amount")));
                addExpensePageController.getCategoryChoiceBox().setValue(resultSet.getString("category"));
                addExpensePageController.getDescriptionTextField().setText(resultSet.getString("description"));

            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

}
