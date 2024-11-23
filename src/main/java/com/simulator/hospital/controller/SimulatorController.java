package com.simulator.hospital.controller;

import com.simulator.hospital.framework.Clock;
import com.simulator.hospital.framework.Trace;
import com.simulator.hospital.model.Customer;
import com.simulator.hospital.model.ServicePoint;
import com.simulator.hospital.model.ServiceUnit;
import com.simulator.hospital.model.SimulatorModel;
import com.simulator.hospital.view.SimulatorView;
import javafx.application.Platform;

import java.util.AbstractMap;
import java.util.HashMap;

public class SimulatorController implements Runnable {
    private SimulatorModel simuModel;
    private SimulatorView view;
    private long delayTime = 1000;
    private final Clock clock;

    public SimulatorController(SimulatorView view) {
        this.view = view;
        this.simuModel = null;
        this.clock = Clock.getInstance();
    }

    public void initializeModel() {
        int numberRegister = view.getNumberRegister();
        int numberGeneral = view.getNumberGeneral();
        int numberSpecialist = view.getNumberSpecialist();
        double avgRegisterTime = view.getRegisterTime();
        double avgGeneralTime = view.getGeneralTime();
        double avgSpecialistTime = view.getSpecialistTime();
        double avgArrivalTime = view.getArrivalTime();
        double simulationTime = view.getSimulationTime();
        this.simuModel = new SimulatorModel(numberRegister, avgRegisterTime, numberGeneral, avgGeneralTime, numberSpecialist, avgSpecialistTime, avgArrivalTime);
        this.simuModel.setSimulationTime(simulationTime);
    }

    public void setDelayTime(long delayTime) {
        this.delayTime = delayTime;
    }

    public void run() {
        Trace.setTraceLevel(Trace.Level.INFO);
        if (simuModel == null) {
            System.err.println("SimulatorModel is not initialized. Please set up the parameters first.");
            return;
        }
        simuModel.initialize();
        while (simuModel.simulate()) {
            // set clock
            clock.setClock(simuModel.currentTime());
            // display clock
            Platform.runLater(() -> view.displayClock(clock.getClock()));

            // Processes all B-events scheduled for the current time
            while (simuModel.currentTime() == clock.getClock()) {
                // process each B-event and display result
                AbstractMap.SimpleEntry<Customer, ServiceUnit> result = simuModel.runEvent(simuModel.processEvent());        // Execute and remove the event from the list
                // get necessary value from result
                int customerId = result.getKey().getId();
                int serviceUnitNumber = result.getValue() != null ? result.getValue().getIndex() : 0;
                // call display method from view
                Platform.runLater(() -> view.displayBEvent(customerId, serviceUnitNumber));
            }

            // Processes C-phase events, checking if any service points can begin servicing a customer
            for (ServiceUnit serviceUnit : simuModel.getServiceUnits()) {
                // check in the service unit if any service point is available and customer is on queue
                if (!serviceUnit.isReserved() && serviceUnit.isOnQueue()) {
                    // start servicing a customer if conditions are met
                    ServicePoint servicePoint = serviceUnit.beginService();
                    Customer customer = servicePoint.getCurrentCustomer();
                    // get necessary value from result and display in view
                    Platform.runLater(() -> view.displayCEvent(customer.getId(), servicePoint.getId()));
//                  System.out.printf("Customer %d is being served at service point %d\n", customer.getId(), servicePoint.getId());
                }
            }

            try {
                Thread.sleep(delayTime);
            } catch (InterruptedException e) {
                System.err.println(e);
            }
        }
        simuModel.results();
    }
}
