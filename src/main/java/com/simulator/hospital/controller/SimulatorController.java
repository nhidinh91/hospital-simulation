package com.simulator.hospital.controller;

import com.simulator.hospital.framework.Clock;
import com.simulator.hospital.framework.Trace;
import com.simulator.hospital.model.logic.Customer;
import com.simulator.hospital.model.logic.ServicePoint;
import com.simulator.hospital.model.logic.ServiceUnit;
import com.simulator.hospital.model.logic.SimulatorModel;
import com.simulator.hospital.view.SimulatorView;
import com.simulator.hospital.model.entity.*;
import com.simulator.hospital.model.dao.*;
import javafx.application.Platform;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SimulatorController implements Runnable {
    private SimulatorModel simuModel;
    private SimulatorView view;
    private long delayTime = 1000;
    private final Clock clock;
    private IntervalsDao intervalsDao;
    private ServicePointTypesDao spTypesDao;
    private SimulationTimeDao simuTimeDao;
    private DelayTimeDao delayTimeDao;
    private int numberRegister;
    private int numberGeneral;
    private int numberSpecialist;
    private double avgRegisterTime;
    private double avgGeneralTime;
    private double avgSpecialistTime;
    private double avgArrivalTime;
    private double simulationTime;

    public SimulatorController(SimulatorView view) {
        this.view = view;
        this.simuModel = null;
        this.clock = Clock.getInstance();
        this.intervalsDao = new IntervalsDao();
        this.spTypesDao = new ServicePointTypesDao();
        this.simuTimeDao = new SimulationTimeDao();
        this.delayTimeDao = new DelayTimeDao();
    }

    public void initializeModel() {
        numberRegister = view.getNumberRegister();
        numberGeneral = view.getNumberGeneral();
        numberSpecialist = view.getNumberSpecialist();
        avgRegisterTime = view.getRegisterTime();
        avgGeneralTime = view.getGeneralTime();
        avgSpecialistTime = view.getSpecialistTime();
        avgArrivalTime = view.getArrivalTime();
        simulationTime = view.getSimulationTime();
        this.simuModel = new SimulatorModel(numberRegister, avgRegisterTime, numberGeneral, avgGeneralTime, numberSpecialist, avgSpecialistTime, avgArrivalTime);
        this.simuModel.setSimulationTime(simulationTime);
    }

    public void setDelayTime(long delayTime) {
        this.delayTime = delayTime;
    }

    // set the interval value to the database
    public void setIntervalsDb(String typeName, String category, double time) {
        Intervals interval = new Intervals(typeName, category, time);
        intervalsDao.update(interval);
    }

    // get the interval values from the database
    public HashMap<Integer, Double> getIntervals() {
        List<Intervals> intervals = intervalsDao.getAllIntervals();
        HashMap<Integer, Double> timeIntervals = new HashMap<>();
        for (Intervals interval : intervals) {
            timeIntervals.put(interval.getId(), interval.getTime());
        }
        return timeIntervals;
    }

    // set the number of specified service point type to the database
    public void setNumberOfPointDb(String typeName, int numberPoint) {
        ServicePointTypes servicePointType = new ServicePointTypes(typeName,numberPoint);
        spTypesDao.update(servicePointType);
    }

    // get the number of point values from the database
    public HashMap<Integer, Integer> getNumberOfPoints() {
        List<ServicePointTypes> servicePointTypes = spTypesDao.getAllServicePointTypes();
        HashMap<Integer, Integer> numberOfPoints = new HashMap<>();
        for (ServicePointTypes servicePointType : servicePointTypes) {
            numberOfPoints.put(servicePointType.getId(), servicePointType.getNumberPoints());
        }
        return numberOfPoints;
    }

    // set the simulation time to the database
    public void setSimulationTimeDb(double time) {
        SimulationTime simulationTime = new SimulationTime(time);
        simuTimeDao.update(simulationTime);
    }

    // get the simulation time from the database
    public double getSimulationTimeDb() {
        List<SimulationTime> simulationTimes = simuTimeDao.getSimulationTime();
        return simulationTimes.get(0).getTime();
    }

    // set the delay time to the database
    public void setDelayTimeDb(long time) {
        DelayTime delayTime = new DelayTime(time);
        delayTimeDao.update(delayTime);
    }

    // get the delay time from the database
    public long getDelayTimeDb() {
        List<DelayTime> delayTimes = delayTimeDao.getDelayTime();
        return delayTimes.get(0).getTime();
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
