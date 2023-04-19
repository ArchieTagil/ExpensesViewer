package ru.viewer.expensesviewer.model.objects;

import javafx.beans.property.*;

public class Car {
    private SimpleStringProperty make;
    private SimpleStringProperty model;
    private SimpleIntegerProperty distance;
    private SimpleDoubleProperty price;

    public Car(String make, String model, Integer distance, Double price) {
        this.make = new SimpleStringProperty(make);
        this.model = new SimpleStringProperty(model);
        this.distance = new SimpleIntegerProperty(distance);
        this.price = new SimpleDoubleProperty(price);
    }

    public String getMake() {
        return make.get();
    }

    public StringProperty makeProperty() {
        return make;
    }

    public void setMake(String make) {
        this.make.set(make);
    }

    public String getModel() {
        return model.get();
    }

    public StringProperty modelProperty() {
        return model;
    }

    public void setModel(String model) {
        this.model.set(model);
    }

    public int getDistance() {
        return distance.get();
    }

    public IntegerProperty distanceProperty() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance.set(distance);
    }

    public double getPrice() {
        return price.get();
    }

    public DoubleProperty priceProperty() {
        return price;
    }

    public void setPrice(double price) {
        this.price.set(price);
    }
}
