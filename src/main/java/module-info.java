module com.example.expense_tracker {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires java.sql;
    requires org.xerial.sqlitejdbc;
    requires java.desktop;

    opens com.example.expense_tracker to javafx.fxml;
    exports com.example.expense_tracker;
    exports com.example.expense_tracker.controller;
    opens com.example.expense_tracker.controller to javafx.fxml;
}