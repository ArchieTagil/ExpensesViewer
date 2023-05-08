package ru.viewer.expensesviewer.model.objects;

import java.time.LocalDate;

public class MovementEntity {
    private int id;
    private LocalDate date;
    private String wallet_debit_name;
    private String wallet_credit_name;
    private double amount;
    private String comment;

    public MovementEntity(int id, LocalDate date, String wallet_debit_name, String wallet_credit_name, double amount, String comment) {
        this.id = id;
        this.date = date;
        this.wallet_debit_name = wallet_debit_name;
        this.wallet_credit_name = wallet_credit_name;
        this.amount = amount;
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "IncomeTable{" + '\t' +
                "id=" + id + '\t' +
                "date=" + date + '\t' +
                "wallet_debit_name='" + wallet_debit_name + '\t' +
                "wallet_credit_category='" + wallet_credit_name + '\t' +
                "amount=" + amount + '\t' +
                "comment='" + comment + '\t' +
                '}';
    }

    public int getId() {
        return id;
    }
    @SuppressWarnings("unused")
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
    @SuppressWarnings("unused")
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

    @SuppressWarnings("unused")
    public String getWallet_debit_name() {
        return wallet_debit_name;
    }
    @SuppressWarnings("unused")
    public void setWallet_debit_name(String wallet_debit_name) {
        this.wallet_debit_name = wallet_debit_name;
    }
    @SuppressWarnings("unused")
    public String getWallet_credit_name() {
        return wallet_credit_name;
    }
    @SuppressWarnings("unused")
    public void setWallet_credit_name(String wallet_credit_category) {
        this.wallet_credit_name = wallet_credit_category;
    }
}
