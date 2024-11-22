package com.simulator.hospital.controller;

import com.simulator.hospital.framework.Clock;
import com.simulator.hospital.model.SimulatorModel;
import com.simulator.hospital.view.SimulatorView;

public class SimulatorController implements Runnable {
    private SimulatorModel simuModel;
    private SimulatorView view;
    private long delayTime = 1000;
    private Clock clock;

    public SimulatorController(SimulatorView view) {
        simuModel = new SimulatorModel(1,2,2);
        simuModel.setSimulationTime(40);
        this.clock = Clock.getInstance();
    }

    public void setDelayTime(long delayTime){
        this.delayTime = delayTime;
    }

    public void run() {
        simuModel.initialize();
        while (simuModel.simulate()) {
            clock.setClock(simuModel.currentTime());
            System.out.printf("Clock is at: %.2f\n", clock.getClock());
            simuModel.runBEvents();
            simuModel.tryCEvents();
            try {
                Thread.sleep(delayTime);
            } catch (InterruptedException e) {
                System.err.println(e);
            }
        }
        simuModel.results();
    }
}
