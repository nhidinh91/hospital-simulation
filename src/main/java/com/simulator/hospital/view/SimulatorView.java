package com.simulator.hospital.view;
import com.simulator.hospital.framework.Trace;
//import com.simulator.hospital.framework.Trace.Level;

import com.simulator.hospital.controller.SimulatorController;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.Scanner;

public class SimulatorView extends Application {
    private SimulatorController controller;

    public void init() {
        this.controller = new SimulatorController(this);
    }

    public void start(Stage stage) {
        Scanner scanner = new Scanner(System.in);
        Trace.setTraceLevel(Trace.Level.INFO);
        Thread simulator = new Thread(controller);
        simulator.start();
        while (simulator.isAlive()){
            long delayTime = Long.parseLong(scanner.nextLine());
            controller.setDelayTime(delayTime);
        };
    }
}
