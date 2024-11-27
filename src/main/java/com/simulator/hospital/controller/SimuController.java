package com.simulator.hospital.controller;

import com.simulator.hospital.framework.Clock;
import com.simulator.hospital.framework.Trace;
import com.simulator.hospital.model.Customer;
import com.simulator.hospital.model.ServicePoint;
import com.simulator.hospital.model.ServiceUnit;
import com.simulator.hospital.model.SimulatorModel;
import com.simulator.hospital.view.MainMenuViewControl;
import com.simulator.hospital.view.SimuViewControl;
import javafx.application.Platform;
import java.util.AbstractMap;

public class SimuController implements Runnable {
    private SimulatorModel simuModel;
    private final MainMenuViewControl menuView;
    private final SimuViewControl simuView;
    private long delayTime;
    private final Clock clock;

    public SimuController(MainMenuViewControl menuView, SimuViewControl simuView) {
        this.menuView = menuView;
        this.simuView = simuView;
        this.clock = Clock.getInstance();
    }

    public void initializeModel() {
        int numberRegister = menuView.getNumberRegister();
        int numberGeneral = menuView.getNumberGeneral();
        int numberSpecialist = menuView.getNumberSpecialist();
        double avgRegisterTime = menuView.getRegisterTime();
        double avgGeneralTime = menuView.getGeneralTime();
        double avgSpecialistTime = menuView.getSpecialistTime();
        double avgArrivalTime = menuView.getArrivalTime();
        double simulationTime = menuView.getSimulationTime();
        this.delayTime = menuView.getDelayTime();
        this.simuModel = new SimulatorModel(numberRegister, avgRegisterTime, numberGeneral, avgGeneralTime, numberSpecialist, avgSpecialistTime, avgArrivalTime);
        this.simuModel.setSimulationTime(simulationTime);
    }

    public long getDelayTime() {
        return this.delayTime;
    }

    public void setDelayTime(long delayTime) {
        this.delayTime = delayTime;
    }

    public SimulatorModel getSimuModel(){
        return this.simuModel;
    }

    @Override
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
                Platform.runLater(() -> simuView.displayClock(clock.getClock()));

            // Processes all B-events scheduled for the current time
            while (simuModel.currentTime() == clock.getClock()) {
                // process each B-event and display result
                AbstractMap.SimpleEntry<Customer, ServiceUnit> result = simuModel.runEvent(simuModel.processEvent());        // Execute and remove the event from the list
                // get necessary value from result
                int customerId = result.getKey().getId();
                int serviceUnitNumber = result.getValue() != null ? result.getValue().getIndex() : 0;

                // get location x,y of next servicep unit;
//                int serviceUnitPosX = result.getValue() != null ? result.getValue().getX() : 0;
//                int serviceUnitPosY = result.getValue() != null ? result.getValue().getY() : 0;

                Customer customer = result.getKey();
                ServiceUnit serviceUnit = result.getValue(); // might return null


                // call display method from view
                Platform.runLater(() -> {
//                    simuView.displayBEvent(customerId, serviceUnitNumber);
//                    simuView.displayBEvent2(customerId, serviceUnitNumber);
                    simuView.displayBEvent3(customer, serviceUnit);
                });
            }

            // add some delay so there are delay between 2 phase, to move between location
            try {
                Thread.sleep(delayTime/2);
            } catch (InterruptedException e) {
                System.err.println(e);
            }

            // Processes C-phase events, checking if any service points can begin servicing a customer
            for (ServiceUnit serviceUnit : simuModel.getServiceUnits()) {
                // check in the service unit if any service point is available and customer is on queue
                if (!serviceUnit.isReserved() && serviceUnit.isOnQueue()) {
                    // start servicing a customer if conditions are met
                    ServicePoint servicePoint = serviceUnit.beginService();
                    Customer customer = servicePoint.getCurrentCustomer();
                    // get necessary value from result and display in view
                    Platform.runLater(() -> {
//                        simuView.displayCEvent(customer.getId(), servicePoint.getId());
//                        simuView.displayCEvent2(customer.getId(), servicePoint.getId());
                        simuView.displayCEvent3(customer, servicePoint);
                    });
//                  System.out.printf("Customer %d is being served at service point %d\n", customer.getId(), servicePoint.getId());
                }
            }

            try {
                // change this so the total delay time is the same
//                Thread.sleep(delayTime);
                Thread.sleep(delayTime/2);
            } catch (InterruptedException e) {
                System.err.println(e);
            }
        }
        // Ensure results are printed after the simulation loop
        Platform.runLater(() -> simuModel.results());
    }
}

