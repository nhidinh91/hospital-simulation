package com.simulator.hospital.model.logic;

import com.simulator.hospital.framework.*;

import java.util.Random;

/**
 * Represents a customer in the hospital simulation.
 * Tracks the customer's arrival time, removal time, and service time, and assigns a unique ID.
 * Customers are categorized as either "general" or "specialist."
 * Provides methods to calculate waiting time, report results, and track overall statistics
 * for all customers in the simulation.
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
	 * The total service time the customer received in the simulation.
	 */
	private double serviceTime;

	/**
	 * The unique identifier for the customer.
	 */
	private int id;

	/**
	 * The type of the customer, either "general" or "specialist."
	 */
	private String customerType;

	/**
	 * Static counter to assign unique IDs to customers.
	 */
	private static int customerCount = 1;

	/**
	 * Static counter to track the total number of customers served.
	 */
	private static int servedCustomerCount = 0;

	/**
	 * Static variable to accumulate the total waiting time of all customers.
	 */
	private static double sumWaitingTime = 0;

	/**
	 * The x-coordinate of the customer's position in the simulation space.
	 */
	private int x;

	/**
	 * The y-coordinate of the customer's position in the simulation space.
	 */
	private int y;

	/**
	 * Constructs a new customer with a unique ID and randomly assigns the type
	 * as either "general" or "specialist." The arrival time is set to the current
	 * clock time of the simulation.
	 */
	public Customer() {
		id = customerCount++;
		customerType = new Random().nextBoolean() ? "general" : "specialist";
		arrivalTime = Clock.getInstance().getClock(); // Set the arrival time to the current simulation clock
		Trace.out(Trace.Level.INFO, "New customer #" + id + " type: " + customerType + " arrived at " + arrivalTime);
	}

	/**
	 * Gets the total number of customers served in the simulation.
	 *
	 * @return the total number of served customers.
	 */
	public static int getServedCustomerCount() {
		return servedCustomerCount;
	}

	/**
	 * Increments the count of served customers by one.
	 */
	public static void setServedCustomerCount() {
		servedCustomerCount++;
	}

	/**
	 * Gets the time the customer was removed from the simulation.
	 *
	 * @return the removal time of the customer.
	 */
	public double getRemovalTime() {
		return removalTime;
	}

	/**
	 * Sets the time the customer was removed from the simulation.
	 *
	 * @param removalTime the time the customer was removed.
	 */
	public void setRemovalTime(double removalTime) {
		this.removalTime = removalTime;
	}

	/**
	 * Gets the time the customer arrived in the simulation.
	 *
	 * @return the arrival time of the customer.
	 */
	public double getArrivalTime() {
		return arrivalTime;
	}

	/**
	 * Sets the time the customer arrived in the simulation.
	 *
	 * @param arrivalTime the time the customer arrived.
	 */
	public void setArrivalTime(double arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	/**
	 * Adds a specified amount of service time to the customer's total service time.
	 *
	 * @param serviceTime the additional service time.
	 */
	public void addServiceTime(double serviceTime) {
		this.serviceTime += serviceTime;
	}

	/**
	 * Gets the unique ID of the customer.
	 *
	 * @return the customer's unique ID.
	 */
	public int getId() {
		return id;
	}

	/**
	 * Gets the type of the customer, either "general" or "specialist."
	 *
	 * @return the type of the customer.
	 */
	public String getCustomerType() {
		return customerType;
	}

	/**
	 * Sets the x-coordinate of the customer's position in the simulation space.
	 *
	 * @param x the x-coordinate.
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * Gets the x-coordinate of the customer's position in the simulation space.
	 *
	 * @return the x-coordinate.
	 */
	public int getX() {
		return x;
	}

	/**
	 * Sets the y-coordinate of the customer's position in the simulation space.
	 *
	 * @param y the y-coordinate.
	 */
	public void setY(int y) {
		this.y = y;
	}

	/**
	 * Gets the y-coordinate of the customer's position in the simulation space.
	 *
	 * @return the y-coordinate.
	 */
	public int getY() {
		return y;
	}

	/**
	 * Calculates the average waiting time for all customers in the simulation.
	 *
	 * @return the average waiting time of all customers.
	 */
	public static double getAvrWaitingTime() {
		return servedCustomerCount == 0 ? 0 : sumWaitingTime / servedCustomerCount;
	}

	/**
	 * Reports the results for this customer, including arrival time, removal time,
	 * waiting time, and updates the total waiting time for all customers.
	 */
	public void reportResults() {
		double waitingTime = Math.max(this.removalTime - this.arrivalTime - this.serviceTime, 0);
		sumWaitingTime += waitingTime;

		Trace.out(Trace.Level.INFO, "Customer " + id + " arrived: " + arrivalTime);
		Trace.out(Trace.Level.INFO, "Customer " + id + " removed: " + removalTime);
		Trace.out(Trace.Level.INFO, "Customer " + id + " stayed: " + (removalTime - arrivalTime));
		Trace.out(Trace.Level.INFO, "Customer " + id + " waiting for " + waitingTime);

		System.out.println("Current mean of the customer waiting times: " + getAvrWaitingTime());
	}

	/**
	 * Resets the customer count and served customer count for a new simulation.
	 */
	public static void resetCount() {
		customerCount = 1;
		servedCustomerCount = 0;
		sumWaitingTime = 0;
	}
}
