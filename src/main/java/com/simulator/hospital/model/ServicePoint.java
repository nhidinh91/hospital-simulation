package com.simulator.hospital.model;

public class ServicePoint {
    private int id;
    private static int count = 1;
    private Customer currentCustomer = null;
    private int x;
    private int y;

    public ServicePoint() {
        this.id = count++;
    }

    public boolean isAvailable() {
        return currentCustomer == null;
    }

    public int getId() {
        return id;
    }

    public Customer getCurrentCustomer() {
        return currentCustomer;
    }

    public void setCurrentCustomer(Customer currentCustomer) {
        this.currentCustomer = currentCustomer;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }
}
