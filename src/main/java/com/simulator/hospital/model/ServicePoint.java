package com.simulator.hospital.model;

public class ServicePoint {
    private int id;
    private static int count = 1;
    private Customer currentCustomer = null;
    private double totalServiceTime;
    private int totalCustomer;
    private double meanServiceTime;
    private double utilization;
    private int x;
    private int y;

    public ServicePoint() {
        this.id = count++;
        totalServiceTime = 0;
        totalCustomer = 0;
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

    public void addServiceTime(double serviceTime) {
        this.totalServiceTime += serviceTime;
    }

    public double getTotalServiceTime() {
        return totalServiceTime;
    }

    public void addCustomer(){
        this.totalCustomer ++;
    }

    public int getTotalCustomer() {
        return totalCustomer;
    }

    public double getMeanServiceTime() {
        meanServiceTime = totalServiceTime / totalCustomer;
        return meanServiceTime;
    }

    public double getUtilization() {
        return utilization;
    }

    public void setUtilization(double utilization) {
        this.utilization = utilization;
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

    public static void resetCount() { count = 1; }
}
