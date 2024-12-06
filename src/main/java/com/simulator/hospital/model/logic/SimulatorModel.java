package com.simulator.hospital.model.logic;

import com.simulator.eduni.distributions.ContinuousGenerator;
import com.simulator.eduni.distributions.Normal;
import com.simulator.hospital.framework.*;
import com.simulator.eduni.distributions.Negexp;

import java.util.*;

/**
 * Models a hospital simulation where customers are processed through multiple service points sequentially.
 * The simulation tracks customer arrivals, service times, and departures while providing
 * detailed metrics like utilization and average waiting times.
 */
public class SimulatorModel {

    /**
     * The total simulation time in seconds.
     */
    private double simulationTime = 0;

    /**
     * The simulation clock to track the current simulation time.
     */
    private Clock clock;

    /**
     * The event list to manage and process simulation events.
     */
    protected EventList eventList;

    /**
     * The arrival process to generate customer arrival events.
     */
    private final ArrivalProcess arrivalProcess;

    /**
     * Array of service units representing the stages in the simulation.
     */
    private final ServiceUnit[] serviceUnits;

    /**
     * The average waiting time of customers in the simulation.
     */
    private double avgWaitingTime = 0;

    /**
     * A list storing the total number of customers served by each service point.
     */
    private List<Integer> customerCount = new ArrayList<>();

    /**
     * A list storing the utilization percentage of each service point.
     */
    private List<Double> utilization = new ArrayList<>();

    /**
     * Constructs a new simulation model with the specified parameters.
     *
     * @param numberRegister   Number of service points in the registration unit.
     * @param avgServiceTime1  Average service time for the registration unit.
     * @param numberGeneral    Number of service points in the general unit.
     * @param avgServiceTime2  Average service time for the general unit.
     * @param numberSpecialist Number of service points in the specialist unit.
     * @param avgServiceTime3  Average service time for the specialist unit.
     * @param avgArrivalTime   Average arrival time for customers.
     */
    public SimulatorModel(int numberRegister, double avgServiceTime1, int numberGeneral, double avgServiceTime2, int numberSpecialist, double avgServiceTime3, double avgArrivalTime) {
        clock = Clock.getInstance();

        // Reset components for a new simulation run
        clock.reset();
        ServiceUnit.resetCount();
        Customer.resetCount();
        ServicePoint.resetCount();

        eventList = new EventList();
        serviceUnits = new ServiceUnit[3];

        // Initialize service units with normal distribution for service times
        serviceUnits[0] = new ServiceUnit(new Normal(avgServiceTime1, 6, 2), eventList, EventType.DEP1, numberRegister);
        serviceUnits[1] = new ServiceUnit(new Normal(avgServiceTime2, 6, 2), eventList, EventType.DEP2, numberGeneral);
        serviceUnits[2] = new ServiceUnit(new Normal(avgServiceTime3, 6, 2), eventList, EventType.DEP3, numberSpecialist);

        // Initialize arrival process with exponential distribution for arrival times
        ContinuousGenerator arrivalTime = new Negexp(avgArrivalTime, 5);
        arrivalProcess = new ArrivalProcess(arrivalTime, eventList, EventType.ARR1);
    }

    /**
     * Sets the simulation runtime.
     *
     * @param time The total simulation time in seconds.
     */
    public void setSimulationTime(double time) {
        simulationTime = time;
    }

    /**
     * Initializes the simulation by scheduling the first customer arrival.
     */
    public void initialize() {
        arrivalProcess.generateNextEvent();
    }

    /**
     * Gets the time of the next scheduled event.
     *
     * @return The time of the next event in the event list.
     */
    public double currentTime() {
        return eventList.getNextEventTime();
    }

    /**
     * Checks if the simulation should continue running.
     *
     * @return {@code true} if the current clock time is less than the simulation time; otherwise {@code false}.
     */
    public boolean simulate() {
        return clock.getClock() < simulationTime;
    }

    /**
     * Processes the next event in the event list.
     *
     * @return The event that was processed.
     */
    public Event processEvent() {
        return eventList.remove();
    }

    /**
     * Gets the array of service units in the simulation.
     *
     * @return An array of service units.
     */
    public ServiceUnit[] getServiceUnits() {
        return serviceUnits;
    }

    /**
     * Processes a B-phase event, such as customer arrivals and departures.
     *
     * @param t The event to process.
     * @return A key-value pair where the key is the customer and the value is the service unit.
     */
    public AbstractMap.SimpleEntry<Customer, ServiceUnit> runEvent(Event t) {
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
                Trace.out(Trace.Level.INFO, "Customer " + customer.getId() + " is added to queue Register");
                break;

            case DEP1:
                // Handle departure from service point 1: move customer to the queue of service point 2
                customer = serviceUnits[0].endService();           // finish service, remove first customer from serving queue
                currentServicePoint = serviceUnits[0].getSelectedServicePoint(customer);
                currentServicePoint.setCurrentCustomer(null); // remove customer info from the served service point
                Trace.out(Trace.Level.INFO, "Customer " + customer.getId() + " finished service at service point " + currentServicePoint.getId());
                if (customer.getCustomerType().equals("general")) {        // add customer to next suitable service unit according to customer type
                    serviceUnits[1].addQueue(customer);
                    result = new AbstractMap.SimpleEntry<>(customer, serviceUnits[1]);
                    Trace.out(Trace.Level.INFO, "Customer " + customer.getId() + " is added to queue General.");
                } else {
                    serviceUnits[2].addQueue(customer);
                    result = new AbstractMap.SimpleEntry<>(customer, serviceUnits[2]);
                    Trace.out(Trace.Level.INFO, "Customer " + customer.getId() + " is added to queue Specialist.");
                }
                break;

            case DEP2:
                // Handle departure from service unit 2: complete service and remove customer from the system
                customer = serviceUnits[1].endService();           // finish service, remove first customer from serving queue
                currentServicePoint = serviceUnits[1].getSelectedServicePoint(customer);
                currentServicePoint.setCurrentCustomer(null);       // remove customer info from the served service point
                Trace.out(Trace.Level.INFO, "Customer " + customer.getId() + " finished service at service point " + currentServicePoint.getId());
                customer.setRemovalTime(Clock.getInstance().getClock());   // set end time for customer
                Customer.setServedCustomerCount();
                customer.reportResults();
                result = new AbstractMap.SimpleEntry<>(customer, null);       // customer is removed from system, return new position = null
                break;

            case DEP3:
                // Handle departure from service unit 3: remove customer from the system
                customer = serviceUnits[2].endService();           // finish service, remove first customer from serving queue
                currentServicePoint = serviceUnits[2].getSelectedServicePoint(customer);
                currentServicePoint.setCurrentCustomer(null);
                Trace.out(Trace.Level.INFO, "Customer " + customer.getId() + " finished service at service point " + currentServicePoint.getId());// remove customer info from the served service point
                customer.setRemovalTime(Clock.getInstance().getClock());   // set end time for customer
                Customer.setServedCustomerCount();
                customer.reportResults();
                result = new AbstractMap.SimpleEntry<>(customer, null);   // customer is removed from system, return new position = null
                break;
        }
        return result;
    }

    /**
     * Processes all B-phase events scheduled for the current time.
     */
    public void runBEvents() {
        while (eventList.getNextEventTime() == clock.getClock()) {
            runEvent(eventList.remove());		// Execute and remove the event from the list
        }
    }

    /**
     * Processes C-phase events, starting service for customers if conditions are met.
     *
     * @return A map where the keys are customers and the values are their service points.
     */
    public HashMap<Customer, ServicePoint> tryCEvents() {
        HashMap<Customer, ServicePoint> results = new HashMap<>();
        for (ServiceUnit serviceUnit : serviceUnits) {
            if (!serviceUnit.isReserved() && serviceUnit.isOnQueue()) {
                ServicePoint servicePoint = serviceUnit.beginService();
                Customer customer = servicePoint.getCurrentCustomer();
                results.put(customer, servicePoint);
            }
        }
        return results;
    }

    /**
     * Outputs the simulation results, including utilization and average waiting times.
     */
    public void results() {
        Trace.out(Trace.Level.INFO, "Simulation ended at " + Clock.getInstance().getClock());
        Trace.out(Trace.Level.INFO, "Average waiting time of customers " + Customer.getAvrWaitingTime());
        avgWaitingTime = Customer.getAvrWaitingTime();
        for (ServiceUnit serviceUnit : serviceUnits) {
            for (ServicePoint servicePoint : serviceUnit.getServicePoints()) {
                double serviceTime = servicePoint.getTotalServiceTime();
                int totalCustomer = servicePoint.getTotalCustomer();
                servicePoint.setUtilization((Math.round(serviceTime / simulationTime * 10.0)) / 10.0);
                Trace.out(Trace.Level.INFO, "Service Point :" + servicePoint.getId());
                Trace.out(Trace.Level.INFO, "Total service time: " + serviceTime + ", mean service time: " + servicePoint.getMeanServiceTime() + ", total customer: " + totalCustomer + ", utilization: " + servicePoint.getUtilization());
                customerCount.add(totalCustomer);
                utilization.add(servicePoint.getUtilization());
            }
        }
    }

    /**
     * Gets the average waiting time of customers in the simulation.
     *
     * @return The average waiting time.
     */
    public double getAvgWaitingTime() {
        return avgWaitingTime;
    }

    /**
     * Gets the total number of customers served by each service point.
     *
     * @return A list of customer counts for each service point.
     */
    public List<Integer> getCustomerCount() {
        return customerCount;
    }

    /**
     * Gets the utilization percentages of each service point.
     *
     * @return A list of utilization percentages for each service point.
     */
    public List<Double> getUtilization() {
        return utilization;
    }

    /**
     * Gets the clock instance used in the simulation.
     *
     * @return The clock instance.
     */
    public Clock getClock() {
        return clock;
    }
}
