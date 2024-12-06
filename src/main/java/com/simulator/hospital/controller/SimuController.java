package com.simulator.hospital.controller;

import com.simulator.hospital.framework.Clock;
import com.simulator.hospital.framework.Trace;
import com.simulator.hospital.model.logic.Customer;
import com.simulator.hospital.model.logic.ServicePoint;
import com.simulator.hospital.model.logic.ServiceUnit;
import com.simulator.hospital.model.logic.SimulatorModel;
import com.simulator.hospital.view.MainMenuViewControl;
import com.simulator.hospital.view.ResultViewControl;
import com.simulator.hospital.view.SimuViewControl;
import javafx.application.Platform;

import java.util.AbstractMap;
import java.util.List;

/**
 * Controller class for managing the simulation process.
 */
public class SimuController implements Runnable {
    private SimulatorModel simuModel;
    private final MainMenuViewControl menuView;
    private final SimuViewControl simuView;
    private final ResultViewControl resultView;
    private long delayTime;
    private final Clock clock;
    private int numberRegister;
    private int numberGeneral;
    private int numberSpecialist;
    private double avgRegisterTime;
    private double avgGeneralTime;
    private double avgSpecialistTime;

    /**
     * Constructs a new SimuController with the specified views.
     *
     * @param menuView the main menu view control
     * @param simuView the simulation view control
     * @param resultView the result view control
     */
    public SimuController(MainMenuViewControl menuView, SimuViewControl simuView, ResultViewControl resultView) {
        this.menuView = menuView;
        this.simuView = simuView;
        this.clock = Clock.getInstance();
        this.delayTime = menuView.getDelayTime(); //initialize with initial delay
        this.resultView = resultView;
    }

    /**
     * Initializes the simulation model with parameters from the menu view.
     */
    public void initializeModel() {
        numberRegister = menuView.getNumberRegister();
        numberGeneral = menuView.getNumberGeneral();
        numberSpecialist = menuView.getNumberSpecialist();
        avgRegisterTime = menuView.getRegisterTime();
        avgGeneralTime = menuView.getGeneralTime();
        avgSpecialistTime = menuView.getSpecialistTime();
        double avgArrivalTime = menuView.getArrivalTime();
        double simulationTime = menuView.getSimulationTime();
        this.simuModel = new SimulatorModel(numberRegister, avgRegisterTime, numberGeneral, avgGeneralTime, numberSpecialist, avgSpecialistTime, avgArrivalTime);
        this.simuModel.setSimulationTime(simulationTime);
    }

    /**
     * Sets a new delay time for the simulation.
     *
     * @param delayTime the new delay time in milliseconds
     */
    public void setDelayTime(long delayTime) {
        this.delayTime = delayTime;
    }

    /**
     * Gets the initial delay time for the simulation.
     *
     * @return the delay time in milliseconds
     */
    public long getDelayTime() {
        return delayTime;
    }

    /**
     * Gets the simulation model.
     *
     * @return the simulation model
     */
    public SimulatorModel getSimuModel() {
        return this.simuModel;
    }

    /**
     * Runs the simulation process.
     */
    @Override
    public void run() {
        Trace.setTraceLevel(Trace.Level.INFO);
        if (simuModel == null) {
            Trace.out(Trace.Level.ERR, "SimulatorModel is not initialized. Please set up the parameters first.");
            return;
        }
        simuModel.initialize();

        while (simuModel.simulate()) {

            // set clock
            clock.setClock(simuModel.currentTime());
            // display clock
            Trace.out(Trace.Level.INFO, "Clock is at " + clock.getClock());
            Platform.runLater(() -> simuView.displayClock(clock.getClock()));

            // Processes all B-events scheduled for the current time
            while (simuModel.currentTime() == clock.getClock()) {
                // process each B-event and display result
                AbstractMap.SimpleEntry<Customer, ServiceUnit> result = simuModel.runEvent(simuModel.processEvent());        // Execute and remove the event from the list
                // get necessary value from result
                int customerId = result.getKey().getId();
                int serviceUnitNumber = result.getValue() != null ? result.getValue().getIndex() : 0;

                Customer customer = result.getKey();
                ServiceUnit serviceUnit = result.getValue(); // might return null

                // call display method from view
                Platform.runLater(() -> {
                    simuView.displayBEvent(customer, serviceUnit);
                });
            }

            // add some delay so here there is delay between 2 phase, wait for animation to complete in phase B in UI
            try {
                Thread.sleep(delayTime / 2);
            } catch (InterruptedException e) {
//                System.err.println(e);
                Trace.out(Trace.Level.ERR, "Simulation thread interrupted.");
                Thread.currentThread().interrupt(); // Reset the interrupted status
                break; // Exit the loop
            }

            // Processes C-phase events, checking if any service points can begin servicing a customer
            for (ServiceUnit serviceUnit : simuModel.getServiceUnits()) {
                // check in the service unit if any service point is available and customer is on queue
                if (!serviceUnit.isReserved() && serviceUnit.isOnQueue()) {
                    // start servicing a customer if conditions are met
                    ServicePoint servicePoint = serviceUnit.beginService();
                    Customer customer = servicePoint.getCurrentCustomer();
                    // get necessary value from result and display in view
                    Trace.out(Trace.Level.INFO, "Customer " + customer.getId() + " is being served at service point " + servicePoint.getId());
                    Platform.runLater(() -> {
                        simuView.displayCEvent(customer, servicePoint);
                    });
                }
            }
            // add some delay so here there is delay between 2 phase, wait for animation to complete in phase B in UI
            try {
                Thread.sleep(delayTime / 2);
            } catch (InterruptedException e) {
//                System.err.println(e);
                Trace.out(Trace.Level.ERR, "Simulation thread interrupted.");
                Thread.currentThread().interrupt(); // Reset the interrupted status
                break; // Exit the loop
            }
        }
        // Ensure results are printed only if the simulation time is completed
        if (isSimulationTimeCompleted()) {
            Platform.runLater(() -> {
                simuModel.results();
                // Get the results from the model
                double avgWaitingTime = simuModel.getAvgWaitingTime();
                List<Integer> customerCount = simuModel.getCustomerCount();
                List<Double> utilization = simuModel.getUtilization();

                // Display the results to ResultViewControl
                resultView.setTable(numberRegister, numberGeneral, numberSpecialist, avgRegisterTime, avgGeneralTime, avgSpecialistTime);
                resultView.display(avgWaitingTime, customerCount, utilization, simuView.getStage());
            });
        }
    }

    /**
     * Checks if the simulation time is completed.
     *
     * @return true if the simulation time is completed, false otherwise
     */
    public boolean isSimulationTimeCompleted() {
        return clock.getClock() >= menuView.getSimulationTime();
    }
}

