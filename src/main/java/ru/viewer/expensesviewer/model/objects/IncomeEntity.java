package ru.viewer.expensesviewer.model.objects;

import java.time.LocalDateTime;

public class IncomeEntity {
    private int id;
    private LocalDateTime date;
    private String wallet_name;
    private String income_category;
    private double amount;
    private String comment;

    public IncomeEntity(int id, LocalDateTime date, String wallet_name, String income_category, double amount, String comment) {
        this.id = id;
        this.date = date;
        this.wallet_name = wallet_name;
        this.income_category = income_category;
        this.amount = amount;
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "IncomeTable{" + '\t' +
                "id=" + id + '\t' +
                "date=" + date + '\t' +
                "wallet_name='" + wallet_name + '\t' +
                "income_category='" + income_category + '\t' +
                "amount=" + amount + '\t' +
                "comment='" + comment + '\t' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

//    public String getWallet_name() {
//        return wallet_name.get();
//    }
//
//    public SimpleStringProperty wallet_nameProperty() {
//        return wallet_name;
//    }
//
//    public void setWallet_name(String wallet_name) {
//        this.wallet_name.set(wallet_name);
//    }


    public String getWallet_name() {
        return wallet_name;
    }

    public void setWallet_name(String wallet_name) {
        this.wallet_name = wallet_name;
    }

    public String getIncome_category() {
        return income_category;
    }

    public void setIncome_category(String income_category) {
        this.income_category = income_category;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
