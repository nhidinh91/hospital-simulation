package com.simulator.hospital.view;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;


/**
 * The class is the mapping of the Customer class inside the model onto the view
 * this class contain also the shape and color of the object
 * it also have the position inside the view
 * The view will use this class for animation render
 */
public class CustomerView {
    private final int id;
    private double x;
    private double y;
    String serviceUnitName;
    private boolean isInQueue;
    private Circle circle;
    private Color color;
    private String customerType;

    /**
     * Constructs a new CustomerView object with the specified id, x and y coordinates, and service unit name.
     *
     * @param id the unique identifier for the customer
     * @param x the x-coordinate of the customer's position
     * @param y the y-coordinate of the customer's position
     * @param serviceUnitName the name of the service unit the customer is associated with
     */
    public CustomerView(int id, double x, double y, String serviceUnitName) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.serviceUnitName = serviceUnitName;
        this.isInQueue = false;
    }

    /**
     * Sets the customer type and updates the color of the circle based on the customer type.
     *
     * @param customerType the type of the customer (e.g., "general" or other types)
     */
    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    /**
     * Gets the customer type.
     *
     * @return the type of the customer
     */
    public String getCustomerType() {
        return this.customerType;
    }


    /**
     * Gets the color of the customer's circle.
     *
     * @return the color of the customer's circle
     */
    public Color getColor() {
        return this.color;
    }


    /**
     * Gets the circle representing the customer.
     *
     * @return the circle representing the customer
     */
    public Circle getCircle() {
        return this.circle;
    }


    /**
     * Sets the circle representing the customer and updates its color based on the customer type.
     *
     * @param circle the circle representing the customer
     */
    public void setCircle(Circle circle) {
        this.circle = circle;
        this.setColor(this.customerType);
        this.circle.setFill(this.color);
    }


    /**
     * Sets the color of the customer's circle based on the customer type.
     *
     * @param customerType the type of the customer (e.g., "general" or other types)
     */
    private void setColor(String customerType) {
        if (this.customerType.equals("general")) {
            this.color = Color.GREEN;
        } else {
            this.color = Color.RED;
        }
    }


    /**
     * Sets the name of the service unit the customer is associated with.
     *
     * @param serviceUnitName the name of the service unit
     */
    public void setServiceUnitName(String serviceUnitName) {
        this.serviceUnitName = serviceUnitName;
    }


    /**
     * Gets the name of the service unit the customer is associated with.
     *
     * @return the name of the service unit
     */
    public String getServiceUnitName() {
        return this.serviceUnitName;
    }


    /**
     * Sets the x-coordinate of the customer's position.
     *
     * @param x the x-coordinate of the customer's position
     */
    public void setX(double x) {
        this.x = x;
    }


    /**
     * Sets the y-coordinate of the customer's position.
     *
     * @param y the y-coordinate of the customer's position
     */
    public void setY(double y) {
        this.y = y;
    }


    /**
     * Gets the x-coordinate of the customer's position.
     *
     * @return the x-coordinate of the customer's position
     */
    public double getX() {
        return this.x;
    }


    /**
     * Gets the y-coordinate of the customer's position.
     *
     * @return the y-coordinate of the customer's position
     */
    public double getY() {
        return this.y;
    }


    /**
     * Sets the customer's queue status.
     *
     * @param inQueue the new queue status of the customer
     */
    public void setInQueue(boolean inQueue) {
        this.isInQueue = inQueue;
    }

    /**
     * Returns a string representation of the customer, including their ID, position, and service unit name.
     * If the service unit name is "arrival", it indicates the customer has arrived at the system.
     * Otherwise, it indicates whether the customer is in the queue or at the service point.
     *
     * @return a string representation of the customer
     */
    @Override
    public String toString() {
        if (this.serviceUnitName.equals("arrival")) {
            return "customer " + this.id + " arrive at system,  pos = (" + this.x + "," + this.y + ")";
        }
        String inQueueStr = "Queue";
        if (!this.isInQueue) {
            inQueueStr = "service point";
        }
        return "customer " + this.id + " is at service unit " + this.serviceUnitName + " , in " + inQueueStr + " pos = (" + this.x + "," + this.y + ")";
    }

}
