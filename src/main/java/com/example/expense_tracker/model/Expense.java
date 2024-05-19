package com.example.expense_tracker.model;

public class Expense {
    private int id;
    private long amount;
    private String description;
    private String categoryName;
    private byte[] categoryIcon;

    public Expense(int id, long amount, String description, String categoryName, byte[] categoryIcon) {
        this.id = id;
        this.amount = amount;
        this.description = description;
        this.categoryName = categoryName;
        this.categoryIcon = categoryIcon;
    }

    public long getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public int getId() {
        return id;
    }

    public byte[] getCategoryIcon() {
        return categoryIcon;
    }
}









