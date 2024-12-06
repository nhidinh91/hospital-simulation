package com.simulator.hospital.model.logic;

import com.simulator.eduni.distributions.ContinuousGenerator;
import com.simulator.hospital.framework.*;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Models a service unit in the simulation.
 * A service unit consists of a queue of customers, a set of service points,
 * and functionality for managing service processes.
 */
public class ServiceUnit {

	/**
	 * Queue of customers waiting for service.
	 */
	private LinkedList<Customer> queue = new LinkedList<>();

	/**
	 * Queue of customers currently being served.
	 */
	private LinkedList<Customer> servingQueue = new LinkedList<>();

	/**
	 * List of service points associated with this service unit.
	 */
	private ArrayList<ServicePoint> servicePoints = new ArrayList<>();

	/**
	 * Random number generator for determining service times.
	 */
	private ContinuousGenerator generator;

	/**
	 * Event list used to schedule events for this service unit.
	 */
	private EventList eventList;

	/**
	 * Event type scheduled for service completion.
	 */
	private EventType eventTypeScheduled;

	/**
	 * X-coordinate of the service unit in the simulation space.
	 */
	private int x;

	/**
	 * Y-coordinate of the service unit in the simulation space.
	 */
	private int y;

	/**
	 * Index of this service unit for identification.
	 */
	private int index;

	/**
	 * Static counter for generating unique indices for service units.
	 */
	private static int count = 1;

	/**
	 * Constructs a new service unit with the specified parameters.
	 *
	 * @param generator          the generator for creating service times.
	 * @param eventList          the event list for scheduling events.
	 * @param type               the event type for service completion.
	 * @param servicePointNumber the number of service points in this unit.
	 */
	public ServiceUnit(ContinuousGenerator generator, EventList eventList, EventType type, int servicePointNumber) {
		this.eventList = eventList;
		this.generator = generator;
		this.eventTypeScheduled = type;
		for (int i = 1; i <= servicePointNumber; i++) {
			ServicePoint servicePoint = new ServicePoint();
			servicePoints.add(servicePoint);
		}
		this.index = count++;
	}

	/**
	 * Gets the x-coordinate of the service unit.
	 *
	 * @return the x-coordinate.
	 */
	public int getX() {
		return x;
	}

	/**
	 * Gets the y-coordinate of the service unit.
	 *
	 * @return the y-coordinate.
	 */
	public int getY() {
		return y;
	}

	/**
	 * Sets the x-coordinate of the service unit.
	 *
	 * @param x the x-coordinate to set.
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * Sets the y-coordinate of the service unit.
	 *
	 * @param y the y-coordinate to set.
	 */
	public void setY(int y) {
		this.y = y;
	}

	/**
	 * Gets the index of the service unit.
	 *
	 * @return the index.
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * Adds a customer to the waiting queue.
	 *
	 * @param customer the customer to add to the queue.
	 */
	public void addQueue(Customer customer) {
		queue.add(customer);
	}

	/**
	 * Completes the service for the first customer in the serving queue.
	 *
	 * @return the customer whose service was completed.
	 */
	public Customer endService() {
		return servingQueue.poll();
	}

	/**
	 * Begins servicing the first customer in the queue.
	 * Assigns the customer to an available service point and schedules a service completion event.
	 *
	 * @return the service point where the customer is being served.
	 */
	public ServicePoint beginService() {
		Customer servingCustomer = queue.poll();
		servingQueue.add(servingCustomer);
		ServicePoint selectedServicePoint = null;

		for (ServicePoint servicePoint : servicePoints) {
			if (servicePoint.isAvailable()) {
				servicePoint.setCurrentCustomer(servingCustomer);
				selectedServicePoint = servicePoint;
				break;
			}
		}

		double serviceTime;
		do {
			serviceTime = generator.sample(); // Generate service time
		} while (serviceTime <= 0);

		servingCustomer.addServiceTime(serviceTime);
		selectedServicePoint.addServiceTime(serviceTime);
		selectedServicePoint.addCustomer();
		eventList.add(new Event(eventTypeScheduled, Clock.getInstance().getClock() + serviceTime));

		return selectedServicePoint;
	}

	/**
	 * Checks if all service points in this unit are currently reserved.
	 *
	 * @return {@code true} if all service points are reserved, {@code false} otherwise.
	 */
	public boolean isReserved() {
		for (ServicePoint servicePoint : servicePoints) {
			if (servicePoint.isAvailable()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks if there are customers waiting in the queue.
	 *
	 * @return {@code true} if the queue is not empty, {@code false} otherwise.
	 */
	public boolean isOnQueue() {
		return !queue.isEmpty();
	}

	/**
	 * Retrieves the service point assigned to a specific customer.
	 *
	 * @param customer the customer whose service point is to be retrieved.
	 * @return the service point assigned to the customer, or {@code null} if none.
	 */
	public ServicePoint getSelectedServicePoint(Customer customer) {
		for (ServicePoint servicePoint : servicePoints) {
			if (!servicePoint.isAvailable() && servicePoint.getCurrentCustomer().equals(customer)) {
				return servicePoint;
			}
		}
		return null;
	}

	/**
	 * Gets the list of service points in this service unit.
	 *
	 * @return a list of service points.
	 */
	public ArrayList<ServicePoint> getServicePoints() {
		return servicePoints;
	}

	/**
	 * Resets the static counter for service unit indices.
	 * Useful when starting a new simulation.
	 */
	public static void resetCount() {
		count = 1;
	}
}
