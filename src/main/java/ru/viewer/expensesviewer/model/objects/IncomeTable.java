package ru.viewer.expensesviewer.model.objects;

import java.time.LocalDateTime;

public class IncomeTable {
    private LocalDateTime date;
    private String wallet_name;
    private String income_category;
    private double amount;
    private String comment;

    public IncomeTable(LocalDateTime date, String wallet_name, String income_category, double amount, String comment) {
        this.date = date;
        this.wallet_name = wallet_name;
        this.income_category = income_category;
        this.amount = amount;
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "IncomeTable{" +
                "date=" + date +
                ", wallet_name='" + wallet_name + '\'' +
                ", income_category='" + income_category + '\'' +
                ", amount=" + amount +
                ", comment='" + comment + '\'' +
                '}';
    }
}
