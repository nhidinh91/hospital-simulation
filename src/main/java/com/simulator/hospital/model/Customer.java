package com.simulator.hospital.model;

import com.simulator.hospital.framework.*;

import java.util.Random;

/**
 * Customer class represents a customer in the simulation.
 * It tracks the customer's arrival and removal times and assigns a unique ID to each customer.
 * It also reports results and calculates the mean service time across all customers.
 */
// TODO:
// Customer to be implemented according to the requirements of the simulation model (data!)
public class Customer {
	private double arrivalTime;
	private double removalTime;
	private double serviceTime;
	private int id;
	private String customerType;
	private static int customerCount = 1;		// Counter for generating unique IDs
	private static double sumWaitingTime = 0;				// Sum of all customer service times
	private int x;
	private int y;
	
	public Customer(){
	    id = customerCount++;
	    customerType = new Random().nextBoolean()? "general" : "specialist";
		arrivalTime = Clock.getInstance().getClock();		// Set the arrival time to the current clock time
		Trace.out(Trace.Level.INFO, "New customer #" + id + " type: " + customerType +" arrived at  " + arrivalTime);
	}

	public double getRemovalTime() {
		return removalTime;
	}

	public void setRemovalTime(double removalTime) {
		this.removalTime = removalTime;
	}

	public double getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(double arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	public void addServiceTime(double serviceTime) {
		this.serviceTime += serviceTime;
	}

	public int getId() {
		return id;
	}

	public String getCustomerType(){return customerType;}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public static double getAvrWaitingTime() {
		return sumWaitingTime/ customerCount;
	}

	public void reportResults(){
		double waitingTime = this.removalTime - this.arrivalTime - this.serviceTime;
		sumWaitingTime += waitingTime;
//		Trace.out(Trace.Level.INFO, "\nCustomer " + id + " type: " + customerType + " ready! ");
		Trace.out(Trace.Level.INFO, "Customer "   + id + " arrived: " + arrivalTime);
		Trace.out(Trace.Level.INFO,"Customer "    + id + " removed: " + removalTime);
		Trace.out(Trace.Level.INFO,"Customer "    + id + " stayed: "  + (removalTime - arrivalTime));
		Trace.out(Trace.Level.INFO, "Customer" + id + " waiting for " + waitingTime);

		System.out.println("Current mean of the customer service times " + getAvrWaitingTime());
	}

	//method to reset customer count when new SimulatorModel constructed
	public static void resetCount() { customerCount = 1; }
}
