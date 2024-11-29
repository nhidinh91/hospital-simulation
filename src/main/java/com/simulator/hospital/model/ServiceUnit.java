package com.simulator.hospital.model;

import com.simulator.eduni.distributions.ContinuousGenerator;
import com.simulator.hospital.framework.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Objects;

import com.simulator.hospital.framework.Trace;

/**
 * ServicePoint class models a point of service in the simulation,
 * where customers wait in a queue and receive service when available.
 */
// TODO:
// Service Point functionalities & calculations (+ variables needed) and reporting to be implemented
public class ServiceUnit {
	private LinkedList<Customer> queue = new LinkedList<>();
	private LinkedList<Customer> servingQueue = new LinkedList<>();
	private ArrayList<ServicePoint> servicePoints = new ArrayList<>();
	private ContinuousGenerator generator;
	private EventList eventList;
	private EventType eventTypeScheduled;
	private int x;
	private int y;
	private int index;
	private static int count = 1;

	public ServiceUnit(ContinuousGenerator generator, EventList eventList, EventType type, int servicePointNumber){
		this.eventList = eventList;
		this.generator = generator;
		this.eventTypeScheduled = type;
		for (int i = 1; i <= servicePointNumber; i++) {
			ServicePoint servicePoint = new ServicePoint();
			servicePoints.add(servicePoint);
		}
		this.index = count++;
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

	public int getIndex() {
		return index;
	}

	// Adds a customer to the queue. The first customer in the queue will be serviced
	public void addQueue(Customer a) {	// The first customer of the queue is always in service
		queue.add(a);
//		Trace.out(Trace.Level.INFO, "Add customer" + a.getId() + " to queue type " + type );
	}

	// Remove customer from serving queue, complete the service
	public Customer endService() {
		return servingQueue.poll();
	}

	// Begins servicing the first customer in the queue ( remove from queue and add to the serving queue. The first customer will complete service first)
	public ServicePoint beginService() {
		Customer servingCustomer = queue.poll();
		servingQueue.add(servingCustomer);
		ServicePoint selectedServicePoint = null;
//		Trace.out(Trace.Level.INFO, "Starting a new service for the customer #" + servingCustomer.getId());
		for (ServicePoint servicePoint : servicePoints) {
			if (servicePoint.isAvailable()) {
				servicePoint.setCurrentCustomer(servingCustomer);
				selectedServicePoint = servicePoint;
				break;
			}
		}
		double serviceTime = generator.sample();
		servingCustomer.addServiceTime(serviceTime);
		selectedServicePoint.addServiceTime(serviceTime);
		selectedServicePoint.addCustomer();
		eventList.add(new Event(eventTypeScheduled, Clock.getInstance().getClock()+serviceTime));
		return selectedServicePoint;
	}

	// Checks if the service point is currently reserved
	public boolean isReserved(){
		boolean reserved = true;
		for (ServicePoint servicePoint : servicePoints) {
			if (servicePoint.isAvailable()) {
				reserved = false;
				break;
			}
		}
		return reserved;
	}

	// Checks if there are any customers waiting in the queue
	public boolean isOnQueue(){
		return !queue.isEmpty();
	}

	// retrieve selected service point
	public ServicePoint getSelectedServicePoint(Customer customer) {
		ServicePoint selectedServicePoint = null;
		for (ServicePoint servicePoint : servicePoints) {
			if (!servicePoint.isAvailable()){
				if (servicePoint.getCurrentCustomer().equals(customer)){
					selectedServicePoint = servicePoint;
				}
			}
		}
		return selectedServicePoint;
	}

	public ArrayList<ServicePoint> getServicePoints() {
		return servicePoints;
	}

	//method to reset serviceUnit count when new SimulatorModel constructed
	public static void resetCount() { count = 1;}
}
