package com.simulator.hospital.controller;

import com.simulator.hospital.framework.Clock;
import com.simulator.hospital.model.SimulatorModel;

public class SimulatorController implements Runnable {
    private SimulatorModel simuModel;
    private Clock clock;

    public SimulatorController() {
        simuModel = new SimulatorModel(1,2,2);
        simuModel.setSimulationTime(40);
        this.clock = Clock.getInstance();
    }

    public void run() {
        simuModel.initialize();
        while (simuModel.simulate()) {
            clock.setClock(simuModel.currentTime());
            simuModel.runBEvents();
            simuModel.tryCEvents();
        }
        simuModel.results();
    }
}
