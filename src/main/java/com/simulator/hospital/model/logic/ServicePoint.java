package com.simulator.hospital.model.logic;

/**
 * Represents a service point in the hospital simulation.
 * Tracks the status of the service point, the customer being served,
 * service statistics, and location coordinates.
 */
public class ServicePoint {

    /**
     * Unique identifier for the service point.
     */
    private int id;

    /**
     * Static counter to assign unique IDs to service points.
     */
    private static int count = 1;

    /**
     * The current customer being served at this service point.
     */
    private Customer currentCustomer = null;

    /**
     * Total service time for all customers served at this service point.
     */
    private double totalServiceTime;

    /**
     * Total number of customers served at this service point.
     */
    private int totalCustomer;

    /**
     * Mean service time for customers at this service point.
     */
    private double meanServiceTime;

    /**
     * Utilization of the service point (percentage of time in use).
     */
    private double utilization;

    /**
     * The x-coordinate of this service point in the simulation space.
     */
    private int x;

    /**
     * The y-coordinate of this service point in the simulation space.
     */
    private int y;

    /**
     * Constructs a new service point with a unique ID.
     * Initializes service statistics to zero.
     */
    public ServicePoint() {
        this.id = count++;
        totalServiceTime = 0;
        totalCustomer = 0;
    }

    /**
     * Checks if the service point is available to serve a new customer.
     *
     * @return {@code true} if the service point is available, {@code false} otherwise.
     */
    public boolean isAvailable() {
        return currentCustomer == null;
    }

    /**
     * Gets the unique ID of the service point.
     *
     * @return the service point ID.
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the current customer being served at this service point.
     *
     * @return the current customer, or {@code null} if none.
     */
    public Customer getCurrentCustomer() {
        return currentCustomer;
    }

    /**
     * Sets the current customer being served at this service point.
     *
     * @param currentCustomer the customer to set.
     */
    public void setCurrentCustomer(Customer currentCustomer) {
        this.currentCustomer = currentCustomer;
    }

    /**
     * Adds service time for a customer to the total service time.
     *
     * @param serviceTime the service time to add.
     */
    public void addServiceTime(double serviceTime) {
        this.totalServiceTime += serviceTime;
    }

    /**
     * Gets the total service time for all customers at this service point.
     *
     * @return the total service time.
     */
    public double getTotalServiceTime() {
        return totalServiceTime;
    }

    /**
     * Increments the total number of customers served by this service point.
     */
    public void addCustomer() {
        this.totalCustomer++;
    }

    /**
     * Gets the total number of customers served by this service point.
     *
     * @return the total number of customers.
     */
    public int getTotalCustomer() {
        return totalCustomer;
    }

    /**
     * Calculates and returns the mean service time for this service point.
     *
     * @return the mean service time.
     */
    public double getMeanServiceTime() {
        meanServiceTime = totalServiceTime / totalCustomer;
        return meanServiceTime;
    }

    /**
     * Gets the utilization percentage of this service point.
     *
     * @return the utilization percentage.
     */
    public double getUtilization() {
        return utilization;
    }

    /**
     * Sets the utilization percentage of this service point.
     *
     * @param utilization the utilization percentage to set.
     */
    public void setUtilization(double utilization) {
        this.utilization = utilization;
    }

    /**
     * Gets the x-coordinate of the service point in the simulation space.
     *
     * @return the x-coordinate.
     */
    public int getX() {
        return x;
    }

    /**
     * Gets the y-coordinate of the service point in the simulation space.
     *
     * @return the y-coordinate.
     */
    public int getY() {
        return y;
    }

    /**
     * Sets the x-coordinate of the service point in the simulation space.
     *
     * @param x the x-coordinate to set.
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Sets the y-coordinate of the service point in the simulation space.
     *
     * @param y the y-coordinate to set.
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Resets the static counter for service point IDs to 1.
     * Useful when starting a new simulation.
     */
    public static void resetCount() {
        count = 1;
    }
}
