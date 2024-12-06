package com.simulator.hospital.model.logic;

import com.simulator.hospital.framework.*;

import java.util.Random;

/**
 * Represents a customer in the simulation.
 * Tracks arrival and removal times, assigns a unique ID, and categorizes customers as "general" or "specialist".
 * Provides methods to calculate waiting time, report results, and track overall statistics.
 */
public class Customer {

	/**
	 * The time the customer arrived in the simulation.
	 */
	private double arrivalTime;

	/**
	 * The time the customer was removed from the simulation.
	 */
	private double removalTime;

	/**
	 * The total service time for the customer.
	 */
	private double serviceTime;

	/**
	 * The unique identifier for the customer.
	 */
	private int id;

	/**
	 * The type of the customer, either "general" or "specialist".
	 */
	private String customerType;

	/**
	 * Static counter for generating unique customer IDs.
	 */
	private static int customerCount = 1;

	/**
	 * Static variable to track the sum of all customer waiting times.
	 */
	private static double sumWaitingTime = 0;

	/**
	 * The x-coordinate of the customer in the simulation space (if applicable).
	 */
	private int x;

	/**
	 * The y-coordinate of the customer in the simulation space (if applicable).
	 */
	private int y;

	/**
	 * Constructs a new customer with a unique ID and a randomly assigned type.
	 * Sets the arrival time to the current simulation clock time.
	 */
	public Customer() {
		id = customerCount++;
		customerType = new Random().nextBoolean() ? "general" : "specialist";
		arrivalTime = Clock.getInstance().getClock(); // Set arrival time to current clock time
		Trace.out(Trace.Level.INFO, "New customer #" + id + " type: " + customerType + " arrived at " + arrivalTime);
	}

	/**
	 * Gets the removal time of the customer.
	 *
	 * @return the removal time.
	 */
	public double getRemovalTime() {
		return removalTime;
	}

	/**
	 * Sets the removal time of the customer.
	 *
	 * @param removalTime the time the customer was removed from the simulation.
	 */
	public void setRemovalTime(double removalTime) {
		this.removalTime = removalTime;
	}

	/**
	 * Gets the arrival time of the customer.
	 *
	 * @return the arrival time.
	 */
	public double getArrivalTime() {
		return arrivalTime;
	}

	/**
	 * Sets the arrival time of the customer.
	 *
	 * @param arrivalTime the time the customer arrived in the simulation.
	 */
	public void setArrivalTime(double arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	/**
	 * Adds a specified amount of service time to the customer's total service time.
	 *
	 * @param serviceTime the service time to add.
	 */
	public void addServiceTime(double serviceTime) {
		this.serviceTime += serviceTime;
	}

	/**
	 * Gets the unique ID of the customer.
	 *
	 * @return the customer ID.
	 */
	public int getId() {
		return id;
	}

	/**
	 * Gets the type of the customer.
	 *
	 * @return the customer type ("general" or "specialist").
	 */
	public String getCustomerType() {
		return customerType;
	}

	/**
	 * Sets the x-coordinate of the customer.
	 *
	 * @param x the x-coordinate.
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * Sets the y-coordinate of the customer.
	 *
	 * @param y the y-coordinate.
	 */
	public void setY(int y) {
		this.y = y;
	}

	/**
	 * Gets the x-coordinate of the customer.
	 *
	 * @return the x-coordinate.
	 */
	public int getX() {
		return x;
	}

	/**
	 * Gets the y-coordinate of the customer.
	 *
	 * @return the y-coordinate.
	 */
	public int getY() {
		return y;
	}

	/**
	 * Calculates and returns the average waiting time for all customers.
	 *
	 * @return the average waiting time.
	 */
	public static double getAvrWaitingTime() {
		return sumWaitingTime / customerCount;
	}

	/**
	 * Reports the results for this customer, including arrival time, removal time, and waiting time.
	 * Updates the total waiting time and prints the current mean waiting time.
	 */
	public void reportResults() {
		double waitingTime = Math.max(this.removalTime - this.arrivalTime - this.serviceTime, 0);
		sumWaitingTime += waitingTime;

		Trace.out(Trace.Level.INFO, "Customer " + id + " arrived: " + arrivalTime);
		Trace.out(Trace.Level.INFO, "Customer " + id + " removed: " + removalTime);
		Trace.out(Trace.Level.INFO, "Customer " + id + " stayed: " + (removalTime - arrivalTime));
		Trace.out(Trace.Level.INFO, "Customer " + id + " waiting for " + waitingTime);

		System.out.println("Current mean of the customer service times: " + getAvrWaitingTime());
	}

	/**
	 * Resets the customer count to 1.
	 * Useful when initializing a new simulation.
	 */
	public static void resetCount() {
		customerCount = 1;
	}
}
