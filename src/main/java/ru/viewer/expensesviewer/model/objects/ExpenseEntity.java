package ru.viewer.expensesviewer.model.objects;

import java.time.LocalDate;

public class ExpenseEntity {
    private int id;
    private LocalDate date;
    private String wallet_name;
    private String expense_category;
    private double amount;
    private String comment;

    public ExpenseEntity(int id, LocalDate date, String wallet_name, String expense_category, double amount, String comment) {
        this.id = id;
        this.date = date;
        this.wallet_name = wallet_name;
        this.expense_category = expense_category;
        this.amount = amount;
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "ExpenseEntity{" +
                "id=" + id +
                ", date=" + date +
                ", wallet_name='" + wallet_name + '\'' +
                ", expense_category='" + expense_category + '\'' +
                ", amount=" + amount +
                ", comment='" + comment + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    @SuppressWarnings("unused")
    public LocalDate getDate() {
        return date;
    }
    @SuppressWarnings("unused")
    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getWallet_name() {
        return wallet_name;
    }
    @SuppressWarnings("unused")
    public void setWallet_name(String wallet_name) {
        this.wallet_name = wallet_name;
    }
    @SuppressWarnings("unused")
    public String getExpense_category() {
        return expense_category;
    }
    @SuppressWarnings("unused")
    public void setExpense_category(String expense_category) {
        this.expense_category = expense_category;
    }

    public double getAmount() {
        return amount;
    }
    @SuppressWarnings("unused")
    public void setAmount(double amount) {
        this.amount = amount;
    }
    @SuppressWarnings("unused")
    public String getComment() {
        return comment;
    }
    @SuppressWarnings("unused")
    public void setComment(String comment) {
        this.comment = comment;
    }
}
