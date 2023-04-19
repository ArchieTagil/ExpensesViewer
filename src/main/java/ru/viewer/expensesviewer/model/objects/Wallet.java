package ru.viewer.expensesviewer.model.objects;

public class Wallet {
    private int id;
    private String name;

    public Wallet() {
    }

    public Wallet(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
