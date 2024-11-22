package com.simulator.hospital.model;

import com.simulator.eduni.distributions.ContinuousGenerator;
import com.simulator.eduni.distributions.Normal;
import com.simulator.hospital.framework.*;
import com.simulator.eduni.distributions.Negexp;

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
    public SimulatorModel(int servicePointNum1, int servicePointNum2, int servicePointNum3) {
        clock = Clock.getInstance();
        eventList = new EventList();
        serviceUnits = new ServiceUnit[3];
        Random r = new Random();
        // exponential distribution is used to model customer arrivals times, to get variability between programs runs, give a variable seed
        ContinuousGenerator arrivalTime = new Negexp(2, 5);
        // normal distribution used to model service times
        ContinuousGenerator serviceTime = new Normal(10, 6, 2);

        // Initialize the service points with the chosen service time distribution
        serviceUnits[0] = new ServiceUnit(new Normal(5, 6, 2), eventList, EventType.DEP1, servicePointNum1);
        serviceUnits[1] = new ServiceUnit(new Normal(10, 6, 2), eventList, EventType.DEP2, servicePointNum2);
        serviceUnits[2] = new ServiceUnit(new Normal(15, 6, 2), eventList, EventType.DEP3, servicePointNum3);

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

    // Processes B-phase events, such as arrivals and departures
    public HashMap<Customer, ServiceUnit> runEvent(Event t) {  // B phase events
        HashMap<Customer, ServiceUnit> results = new HashMap<>();
        Customer a;
        ServicePoint currentServicePoint = null;

        switch ((EventType) t.getType()) {
            case ARR1:
                // Handle a new customer arrival: add to the queue of the first service point
                a = new Customer();
                serviceUnits[0].addQueue(a);
                arrivalProcess.generateNextEvent();        // Schedule the next arrival
                results.put(a, serviceUnits[0]);
                break;

            case DEP1:
                // Handle departure from service point 1: move customer to the queue of service point 2
                a = serviceUnits[0].endService();           // finish service, remove first customer from serving queue
                if (a.getCustomerType().equals("general")) {        // add customer to next suitable service unit according to customer type
                    serviceUnits[1].addQueue(a);
                    results.put(a, serviceUnits[1]);
                } else {
                    serviceUnits[2].addQueue(a);
                    results.put(a, serviceUnits[2]);
                }
                currentServicePoint = serviceUnits[0].getSelectedServicePoint(a);
                currentServicePoint.setCurrentCustomer(null);       // remove customer info from the served service point
                System.out.printf("Customer %d finished service at SP: %d", a.getId(), currentServicePoint.getId());
                break;

            case DEP2:
                // Handle departure from service unit 2: complete service and remove customer from the system
                a = serviceUnits[1].endService();           // finish service, remove first customer from serving queue
                currentServicePoint = serviceUnits[1].getSelectedServicePoint(a);
                currentServicePoint.setCurrentCustomer(null);       // remove customer info from the served service point
                a.setRemovalTime(Clock.getInstance().getClock());   // set end time for customer
                a.reportResults();
                results.put(a, null);       // customer is removed from system, return new position = null
                break;

            case DEP3:
                // Handle departure from service unit 3: remove customer from the system
                a = serviceUnits[2].endService();           // finish service, remove first customer from serving queue
                currentServicePoint = serviceUnits[2].getSelectedServicePoint(a);
                currentServicePoint.setCurrentCustomer(null);       // remove customer info from the served service point
                a.setRemovalTime(Clock.getInstance().getClock());   // set end time for customer
                a.reportResults();
                results.put(a, null);       // customer is removed from system, return new position = null
                break;
        }
        return results;
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
        System.out.println("Results ... are currently missing");
    }

    // get clock instance
    public Clock getClock() {
        return clock;
    }
}
