package com.simulator.hospital.view;
import com.simulator.hospital.framework.Trace;
//import com.simulator.hospital.framework.Trace.Level;

import com.simulator.hospital.controller.SimulatorController;

public class SimulatorView {
    public static void main(String[] args) {
        Trace.setTraceLevel(Trace.Level.INFO);
        SimulatorController controller = new SimulatorController();
        controller.run();
    }
}
