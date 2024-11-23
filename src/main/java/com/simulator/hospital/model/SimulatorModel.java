package com.simulator.hospital.model;

import com.simulator.eduni.distributions.ContinuousGenerator;
import com.simulator.eduni.distributions.Normal;
import com.simulator.hospital.framework.*;
import com.simulator.eduni.distributions.Negexp;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Random;

/**
 * MyEngine class extends the abstract Engine class to implement a custom simulation.
 * It models a system with three service points that customers must go through sequentially.
 */

public class SimulatorModel {
    private double simulationTime = 0;	// time when the simulation will be stopped
    private Clock clock;				// to simplify the code (clock.getClock() instead Clock.getInstance().getClock())
    protected EventList eventList;
    private final ArrivalProcess arrivalProcess;
    private final ServiceUnit[] serviceUnits;

    /*
     * This is the place where you implement your own simulator
     *
     * Demo simulation case:
     * Simulate three service points, customer goes through all three service points to get serviced
     * 		--> SP1 --> SP2 --> SP3 -->
     */
    public SimulatorModel(int numberRegister, double avgServiceTime1,  int numberGeneral, double avgServiceTime2, int numberSpecialist, double avgServiceTime3, double avgArrivalTime) {
        clock = Clock.getInstance();
        eventList = new EventList();
        serviceUnits = new ServiceUnit[3];
        Random r = new Random();
        // exponential distribution is used to model customer arrivals times, to get variability between programs runs, give a variable seed
        ContinuousGenerator arrivalTime = new Negexp(avgArrivalTime, 5);
        // normal distribution used to model service times
        ContinuousGenerator serviceTime = new Normal(10, 6, 2);

        // Initialize the service points with the chosen service time distribution
        serviceUnits[0] = new ServiceUnit(new Normal(avgServiceTime1, 6, 2), eventList, EventType.DEP1, numberRegister);
        serviceUnits[1] = new ServiceUnit(new Normal(avgServiceTime2, 6, 2), eventList, EventType.DEP2, numberGeneral);
        serviceUnits[2] = new ServiceUnit(new Normal(avgServiceTime3, 6, 2), eventList, EventType.DEP3, numberSpecialist);

        // Initialize the arrival process
        arrivalProcess = new ArrivalProcess(arrivalTime, eventList, EventType.ARR1);
    }

    public void setSimulationTime(double time) {	// define how long we will run the simulation
        simulationTime = time;
    }

    // Initializes the simulation by generating the first arrival event
    public void initialize() {    // First arrival in the system
        arrivalProcess.generateNextEvent();
    }

    // Get the time of the next event in the event list
    public double currentTime(){
        return eventList.getNextEventTime();
    }

    // Checks if the simulation should continue
    public boolean simulate(){
        return clock.getClock() < simulationTime;
    }

    // process event in event list
    public Event processEvent() {
        return eventList.remove();
    }

    // get service unit list
    public ServiceUnit[] getServiceUnits() {
        return serviceUnits;
    }

    // Processes B-phase events, such as arrivals and departures
    public AbstractMap.SimpleEntry<Customer, ServiceUnit> runEvent(Event t) {  // B phase events
        Customer customer;
        ServicePoint currentServicePoint = null;
        AbstractMap.SimpleEntry<Customer, ServiceUnit> result = new AbstractMap.SimpleEntry<>(null, null);

        switch ((EventType) t.getType()) {
            case ARR1:
                // Handle a new customer arrival: add to the queue of the first service point
                customer = new Customer();
                serviceUnits[0].addQueue(customer);
                arrivalProcess.generateNextEvent();        // Schedule the next arrival
                result = new AbstractMap.SimpleEntry<>(customer, serviceUnits[0]);
                System.out.printf("Customer %d is added to queue Register.\n", customer.getId());
                break;

            case DEP1:
                // Handle departure from service point 1: move customer to the queue of service point 2
                customer = serviceUnits[0].endService();           // finish service, remove first customer from serving queue
                currentServicePoint = serviceUnits[0].getSelectedServicePoint(customer);
                currentServicePoint.setCurrentCustomer(null);       // remove customer info from the served service point
                System.out.printf("Customer %d finished service at SP: %d.\n", customer.getId(), currentServicePoint.getId());
                if (customer.getCustomerType().equals("general")) {        // add customer to next suitable service unit according to customer type
                    serviceUnits[1].addQueue(customer);
                    result = new AbstractMap.SimpleEntry<>(customer, serviceUnits[1]);
                    System.out.printf("Customer %d is added to queue General.\n", customer.getId());
                } else {
                    serviceUnits[2].addQueue(customer);
                    result = new AbstractMap.SimpleEntry<>(customer, serviceUnits[2]);
                    System.out.printf("Customer %d is added to queue Specialist.\n", customer.getId());
                }

                break;

            case DEP2:
                // Handle departure from service unit 2: complete service and remove customer from the system
                customer = serviceUnits[1].endService();           // finish service, remove first customer from serving queue
                currentServicePoint = serviceUnits[1].getSelectedServicePoint(customer);
                currentServicePoint.setCurrentCustomer(null);       // remove customer info from the served service point
                customer.setRemovalTime(Clock.getInstance().getClock());   // set end time for customer
                customer.reportResults();
                result = new AbstractMap.SimpleEntry<>(customer, null);       // customer is removed from system, return new position = null
                break;

            case DEP3:
                // Handle departure from service unit 3: remove customer from the system
                customer = serviceUnits[2].endService();           // finish service, remove first customer from serving queue
                currentServicePoint = serviceUnits[2].getSelectedServicePoint(customer);
                currentServicePoint.setCurrentCustomer(null);       // remove customer info from the served service point
                customer.setRemovalTime(Clock.getInstance().getClock());   // set end time for customer
                customer.reportResults();
                result = new AbstractMap.SimpleEntry<>(customer, null);   // customer is removed from system, return new position = null
                break;
        }
        return result;
    }

    // Processes all B-events scheduled for the current time
    public void runBEvents() {
        while (eventList.getNextEventTime() == clock.getClock()){
            runEvent(eventList.remove());		// Execute and remove the event from the list
        }
    }

    // Processes C-phase events, checking if any service points can begin servicing a customer
    public HashMap<Customer, ServicePoint> tryCEvents() {
        HashMap<Customer, ServicePoint> results = new HashMap<>();
        for (ServiceUnit serviceUnit : serviceUnits) {
            // check in the service unit if any service point is available and customer is on queue
            if (!serviceUnit.isReserved() && serviceUnit.isOnQueue()) {
                ServicePoint servicePoint = serviceUnit.beginService();         // Start servicing a customer if conditions are met
                Customer customer = servicePoint.getCurrentCustomer();
                results.put(customer, servicePoint);
                System.out.printf("Customer %d is being served at service point %d\n", customer.getId(), servicePoint.getId());
            }
        }
        return results;
    }

    // Outputs the results of the simulation
    public void results() {
        System.out.println("Simulation ended at " + Clock.getInstance().getClock());
        System.out.println("Average waiting time of customers " + Customer.getAvrWaitingTime());
        for (ServiceUnit serviceUnit : serviceUnits) {
            for (ServicePoint servicePoint : serviceUnit.getServicePoints()) {
                double serviceTime = servicePoint.getTotalServiceTime();
                int totalCustomer = servicePoint.getTotalCustomer();
                servicePoint.setUtilization(serviceTime / simulationTime);
                System.out.printf("Service Point %d:\n", servicePoint.getId());
                System.out.printf("Total service time: %.1f, mean service time: %.1f, total customer: %d, utilization: %.2f\n", serviceTime, servicePoint.getMeanServiceTime(), totalCustomer, servicePoint.getUtilization());
            }
        }
    }

    // get clock instance
    public Clock getClock() {
        return clock;
    }
}
